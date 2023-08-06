package test;

import io.qameta.allure.Description;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import org.junit.*;
import static org.hamcrest.Matchers.is;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class ChangeUserData {
    private static String accessToken;
    private String changedEmail = "uniqueuser1_changed@test.com";
    private String changedName = "Changed Name";
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
    public void testUpdateUserDataWithAuthorization() {
        given()
                .header("Authorization", accessToken)
                .contentType(ContentType.JSON)
                .body("{\"email\":\"" + changedEmail + "\",\"name\":\"" + changedName + "\"}")
                .when()
                .patch("/user")
                .then()
                .statusCode(200)
                .body("success", is(true))
                .body("user.email", is(changedEmail))
                .body("user.name", is(changedName));
    }
    @Description("Описание вашего теста здесь")
    @Test
    public void testUpdateUserDataWithoutAuthorization() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"email\":\"" + changedEmail + "\",\"name\":\"" + changedName + "\"}")
                .when()
                .patch("/user")
                .then()
                .statusCode(401) // Неавторизованный код состояния, так как запрос сделан без надлежащей авторизации
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
