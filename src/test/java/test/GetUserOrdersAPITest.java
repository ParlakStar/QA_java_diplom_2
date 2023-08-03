package test;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class GetUserOrdersAPITest {
    private String accessToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        RestAssured.basePath = "/api/auth";

        // Создаем пользователя
        given()
                .contentType(ContentType.JSON)
                .body("{\"email\":\"uniqueuser1@test.com\",\"password\":\"test123\",\"name\":\"Unique User\"}")
                .when()
                .post("/register")
                .then()
                .statusCode(200)
                .body("success", is(true))
                .body("user.email", is("uniqueuser1@test.com"))
                .body("user.name", is("Unique User"));

        // Логинимся и получаем accessToken
        accessToken = given()
                .contentType(ContentType.JSON)
                .body("{\"email\":\"uniqueuser1@test.com\",\"password\":\"test123\"}")
                .when()
                .post("/login")
                .then()
                .statusCode(200)
                .body("success", is(true))
                .body("user.email", is("uniqueuser1@test.com"))
                .body("user.name", is("Unique User"))
                .extract()
                .path("accessToken");
    }

    @Test
    public void testGetUserOrdersWithAuthorization() {
        RestAssured.basePath = "/api/orders"; // Устанавливаем базовый путь на "/api/orders"
        given()
                .header("Authorization", accessToken)
                .when()
                .get("/")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("success", equalTo(true))
                .body("orders", notNullValue())
                .body("total", greaterThanOrEqualTo(0))
                .body("totalToday", greaterThanOrEqualTo(0))
                .log().body();
        RestAssured.basePath = "/api/auth"; // Возвращаем базовый путь на "/api/auth" для других тестов
    }
    @Test
    public void testGetUserOrdersWithoutAuthorization() {
        RestAssured.basePath = "/api/orders"; // Устанавливаем базовый путь на "/api/orders"
        given()
                .when()
                .get("/")
                .then()
                .statusCode(401) // Ожидаем код ответа 401 (Unauthorized)
                .contentType(ContentType.JSON) // Убедимся, что контент тип JSON
                .body("success", equalTo(false)) // Проверим, что поле "success" равно false
                .body("message", is("You should be authorised")) // Проверим текст сообщения
                .log().body();
        RestAssured.basePath = "/api/auth"; // Возвращаем базовый путь на "/api/auth" для других тестов
    }
    @After
    public void cleanup() {
        // Удаляем пользователя после теста
        given()
                .header(new Header("Authorization", accessToken))
                .when()
                .delete("/user")
                .then()
                .statusCode(202)
                .body("success", equalTo(true));
    }
}

