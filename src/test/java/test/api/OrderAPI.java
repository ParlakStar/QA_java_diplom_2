package test.api;

import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class OrderAPI {
    private static final String BASE_URI = "https://stellarburgers.nomoreparties.site";
    private static final String ORDERS_PATH = "/api/orders";

    public static void createOrder(String accessToken, String[] ingredients, int statusCode, Boolean success, String name, String expectedMessage) {
        String requestBody = formRequestBody(ingredients);

        given()
                .baseUri(BASE_URI)
                .basePath(ORDERS_PATH)
                .header("Authorization", accessToken)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .log().all()
                .when()
                .post("/")
                .then()
                .statusCode(statusCode)
                .body("success", success != null ? is(success) : is(nullValue()))
                .body("name", name != null ? is(name) : is(nullValue()))
                .body("message", expectedMessage != null ? is(expectedMessage) : (success != null && success ? is(nullValue()) : notNullValue()));
    }

    public static void createOrderWithoutAuthorization(String[] ingredients, int statusCode, boolean success, String name) {
        String requestBody = formRequestBody(ingredients);

        given()
                .baseUri(BASE_URI)
                .basePath(ORDERS_PATH)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/")
                .then()
                .statusCode(statusCode)
                .body("success", is(success))
                .body("name", is(name))
                .body("order.number", notNullValue());
    }

    public static void getUserOrdersWithAuthorization(String accessToken) {
        given()
                .baseUri(BASE_URI)
                .basePath(ORDERS_PATH)
                .header("Authorization", accessToken)
                .when()
                .get("/")
                .then()
                .statusCode(200)
                .body("success", is(true));
    }

    public static void getUserOrdersWithoutAuthorization() {
        given()
                .baseUri(BASE_URI)
                .basePath(ORDERS_PATH)
                .when()
                .get("/")
                .then()
                .statusCode(401) // Unauthorized status code, as the request is made without proper authorization
                .body("success", is(false));
    }

    private static String formRequestBody(String[] ingredients) {
        if (ingredients.length == 0) {
            return "{\"ingredients\":[]}";
        } else {
            return "{\"ingredients\":[\"" + String.join("\",\"", ingredients) + "\"]}";
        }
    }
}
