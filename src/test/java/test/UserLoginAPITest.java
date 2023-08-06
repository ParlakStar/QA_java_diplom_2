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

public class UserLoginAPITest {
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
    public void testLoginWithValidCredentials() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"email\":\"uniqueuser1@test.com\",\"password\":\"test123\"}")
                .when()
                .post("/login")
                .then()
                .statusCode(200)
                .body("success", is(true))
                .body("user.email", is("uniqueuser1@test.com"))
                .body("user.name", is("Unique User"));
    }
    @Description("Описание вашего теста здесь")
    @Test
    public void testLoginWithInvalidCredentials() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"email\":\"uniqueuser1@test.com\",\"password\":\"incorrectPassword\"}")
                .when()
                .post("/login")
                .then()
                .statusCode(401) // Несанкционированный код состояния, так как попытка входа не удалась
                .body("success", is(false));
    }
    @Description("Описание вашего теста здесь")
    @Test
    public void testLoginWithChangedCredentials() {
        // Предположим, вы изменили адрес электронной почты или пароль.
        String changedEmail = "uniqueuser1_changed@test.com";
        String changedPassword = "changedPassword";

        given()
                .contentType(ContentType.JSON)
                .body("{\"email\":\"" + changedEmail + "\",\"password\":\"" + changedPassword + "\"}")
                .when()
                .post("/login")
                .then()
                .statusCode(401) // Несанкционированный код состояния, так как попытка входа не удалась
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