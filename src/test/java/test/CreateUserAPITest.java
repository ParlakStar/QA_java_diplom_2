package test;
import io.qameta.allure.Description;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class CreateUserAPITest {
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
    @Description("Описание вашего теста здесь")
    @Test
    public void testUserExists() {
        // Проверяем, что пользователь успешно создан и можем получить информацию о нем с помощью запроса /user
        given()
                .header("Authorization", accessToken)
                .when()
                .get("/user")
                .then()
                .statusCode(200)
                .body("success", is(true))
                .body("user.email", is("uniqueuser1@test.com"))
                .body("user.name", is("Unique User"));
    }
    @Description("Описание вашего теста здесь")
    @Test
    public void testCreateAlreadyRegisteredUser() {
        // Создаем пользователя, которого мы уже создали в методе setUp()
        given()
                .contentType(ContentType.JSON)
                .body("{\"email\":\"uniqueuser1@test.com\",\"password\":\"test123\",\"name\":\"Unique User\"}")
                .when()
                .post("/register")
                .then()
                .statusCode(403) // Ожидаем статус код 400 (Bad Request), так как пользователь уже существует
                .body("success", is(false));
    }
    @Description("Описание вашего теста здесь")
    @Test
    public void testCreateUserWithMissingField() {
        // Создаем пользователя без указания обязательного поля "name"
        given()
                .contentType(ContentType.JSON)
                .body("{\"email\":\"test@test.com\",\"password\":\"test123\"}")
                .when()
                .post("/register")
                .then()
                .statusCode(403) // Ожидаем статус код 401 (Bad Request), так как поле "name" обязательно
                .body("success", is(false));
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