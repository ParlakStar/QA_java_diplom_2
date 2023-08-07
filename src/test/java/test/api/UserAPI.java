package test.api;

import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class UserAPI {
    private static final String BASE_URI = "https://stellarburgers.nomoreparties.site";
    private static final String AUTH_PATH = "/api/auth";

    public static String createUser(String email, String password, String name) {
        return given()
                .baseUri(BASE_URI)
                .basePath(AUTH_PATH)
                .contentType(ContentType.JSON)
                .body("{\"email\":\"uniqueuser1@test.com\",\"password\":\"test123\",\"name\":\"Unique User\"}")
                .when()
                .post("/register")
                .then()
                .statusCode(200)
                .body("success", is(true))
                .body("user.email", is("uniqueuser1@test.com"))
                .body("user.name", is("Unique User"))
                .extract()
                .path("accessToken");
    }

    public static void deleteUser(String accessToken) {
        given()
                .baseUri(BASE_URI)
                .basePath(AUTH_PATH)
                .header("Authorization", accessToken)
                .when()
                .delete("/user")
                .then()
                .statusCode(202)
                .body("success", is(true));
    }

    public static void updateUserData(String accessToken, String changedEmail, String changedName) {
        given()
                .baseUri(BASE_URI)
                .basePath(AUTH_PATH)
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

    public static void updateUserDataWithoutAuthorization(String changedEmail, String changedName) {
        given()
                .baseUri(BASE_URI)
                .basePath(AUTH_PATH)
                .contentType(ContentType.JSON)
                .body("{\"email\":\"" + changedEmail + "\",\"name\":\"" + changedName + "\"}")
                .when()
                .patch("/user")
                .then()
                .statusCode(401) // Unauthorized status code, as the request is made without proper authorization
                .body("success", is(false));
    }
    public static void getUserInfo(String accessToken, String email, String name) {
        given()
                .baseUri(BASE_URI)
                .basePath(AUTH_PATH)
                .header("Authorization", accessToken)
                .when()
                .get("/user")
                .then()
                .statusCode(200)
                .body("success", is(true))
                .body("user.email", is(email))
                .body("user.name", is(name));
    }

    public static void createAlreadyRegisteredUser(String email, String password, String name) {
        given()
                .baseUri(BASE_URI)
                .basePath(AUTH_PATH)
                .contentType(ContentType.JSON)
                .body("{\"email\":\"" + email + "\",\"password\":\"" + password + "\",\"name\":\"" + name + "\"}")
                .when()
                .post("/register")
                .then()
                .statusCode(403) // Ожидаем статус код 403 , так как пользователь уже существует
                .body("success", is(false));
    }

    public static void createUserWithMissingField(String email, String password) {
        given()
                .baseUri(BASE_URI)
                .basePath(AUTH_PATH)
                .contentType(ContentType.JSON)
                .body("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}")
                .when()
                .post("/register")
                .then()
                .statusCode(403) // Ожидаем статус код 401 (Bad Request), так как поле "name" обязательно
                .body("success", is(false));
    }
    public static void loginWithValidCredentials(String email, String password) {
        given()
                .baseUri(BASE_URI)
                .basePath(AUTH_PATH)
                .contentType(ContentType.JSON)
                .body("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}")
                .when()
                .post("/login")
                .then()
                .statusCode(200)
                .body("success", is(true));
    }

    public static void loginWithInvalidCredentials(String email, String password) {
        given()
                .baseUri(BASE_URI)
                .basePath(AUTH_PATH)
                .contentType(ContentType.JSON)
                .body("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}")
                .when()
                .post("/login")
                .then()
                .statusCode(401) // Unauthorized status code, as the request is made with invalid credentials
                .body("success", is(false));
    }

    public static void loginWithChangedCredentials(String email, String password) {
        given()
                .baseUri(BASE_URI)
                .basePath(AUTH_PATH)
                .contentType(ContentType.JSON)
                .body("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}")
                .when()
                .post("/login")
                .then()
                .statusCode(401) // Unauthorized status code, as the request is made with changed credentials
                .body("success", is(false));
    }

}
