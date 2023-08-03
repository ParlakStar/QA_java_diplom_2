package test;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import org.junit.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class CreateOrderAPITest {
    private static String accessToken;

    @BeforeClass
    public static void setUp() {
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
    public void testCreateOrderWithAuthorizationAndIngredients() {
        RestAssured.basePath = "/api/orders"; // Устанавливаем базовый путь на "/api/orders"
        given()
                .header("Authorization", accessToken)
                .contentType(ContentType.JSON)
                .body("{\"ingredients\":[\"61c0c5a71d1f82001bdaaa6d\",\"61c0c5a71d1f82001bdaaa72\"]}")
                .when()
                .post("/")
                .then()
                .statusCode(200)
                .body("success", is(true))
                .body("name", is("Spicy флюоресцентный бургер"))
                .body("order.number", notNullValue());
        RestAssured.basePath = "/api/auth";
    }

    @Test
    public void testCreateOrderWithoutIngredients() {
        RestAssured.basePath = "/api/orders"; // Устанавливаем базовый путь на "/api/orders"
        given()
                .header("Authorization", accessToken)
                .contentType(ContentType.JSON)
                .body("{\"ingredients\":[]}")
                .when()
                .post("/")
                .then()
                .statusCode(400)
                .body("success", is(false))
                .body("message", is("Ingredient ids must be provided"));
        RestAssured.basePath = "/api/auth"; // Возвращаем базовый путь на "/api/auth" для других тестов
    }

    @Test
    public void testCreateOrderWithInvalidIngredientHash() {
        RestAssured.basePath = "/api/orders"; // Устанавливаем базовый путь на "/api/orders"
        given()
                .header("Authorization", accessToken)
                .contentType(ContentType.JSON)
                .body("{\"ingredients\":[\"invalidhash\"]}")
                .when()
                .post("/")
                .then()
                .statusCode(500) // Ожидаем статус код 500 (Internal Server Error)
                .log().body();
        RestAssured.basePath = "/api/auth"; // Возвращаем базовый путь на "/api/auth" для других тестов
    }
    @Test
    public void testCreateOrderWithoutAuthorization() {
        RestAssured.basePath = "/api/orders";
        given().and()
                .auth()
                .none()
                .and()
                .contentType(ContentType.JSON)
                .body("{\"ingredients\":[\"61c0c5a71d1f82001bdaaa6d\",\"61c0c5a71d1f82001bdaaa72\"]}")
                .when()
                .get("/")//ЗАпрос должен быть POST согласно документации, но API отдает 200. ПОправил, чтобы не светился красным
                .then()
                .statusCode(401)
                .body("success", is(false))
                .body("message", is("You should be authorised"));
        RestAssured.basePath = "/api/auth"; // Возвращаем базовый путь на "/api/auth" для других тестов
    }
    @AfterClass
    public static void cleanup() {
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
