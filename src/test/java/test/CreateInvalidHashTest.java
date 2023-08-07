package test;

import io.qameta.allure.Description;
import io.restassured.http.ContentType;
import org.junit.Test;
import test.api.UserAPI;
import static io.restassured.RestAssured.given;

public class CreateInvalidHashTest {

    private static final String BASE_URI = "https://stellarburgers.nomoreparties.site";
    private static final String ORDERS_PATH = "/api/orders";

    @Description("Создание бургера с неправильным хэшом ингредиентов")
    @Test
    public void testCreateOrderWithInvalidIngredientHash() {
        // Create a new user
        String accessToken = UserAPI.createUser("uniqueuser1@test.com", "test123", "Unique User");

        // Proceed with the order creation test
        String requestBody = "{\"ingredients\":[\"\"]}";

        given()
                .baseUri(BASE_URI)
                .basePath(ORDERS_PATH)
                .header("Authorization", accessToken)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/")
                .then()
                .statusCode(500)
                .log().all();


        UserAPI.deleteUser(accessToken);
    }
}
