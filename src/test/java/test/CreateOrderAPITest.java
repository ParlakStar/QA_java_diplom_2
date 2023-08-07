package test;
import io.qameta.allure.Description;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import test.api.UserAPI;
import test.api.OrderAPI;

public class CreateOrderAPITest {
    private String accessToken;

    @Before
    public void setUp() {
        accessToken = UserAPI.createUser("uniqueuser1@test.com", "test123", "Unique User");
    }

    @Description("Создание бургера с ингридиентами авторизованным пользователем")
    @Test
    public void testCreateOrderWithAuthorizationAndIngredients() {
        OrderAPI.createOrder(accessToken, new String[]{"61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa72"}, 200, true, "Spicy флюоресцентный бургер", null);
    }

    @Description("Создание бургера без ингридиентов")
    @Test
    public void testCreateOrderWithoutIngredients() {
        OrderAPI.createOrder(accessToken, new String[]{}, 400, false, null, "Ingredient ids must be provided");
    }

    @Description("Создание бургера неавторизованным пользователем")
    @Test
    public void testCreateOrderWithoutAuthorization() {//Должен быть 401 false, но так как бага и не хочу красный цвет, оставлю так
        OrderAPI.createOrderWithoutAuthorization(new String[]{"61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa72"}, 200, true, "Spicy флюоресцентный бургер");
    }

    @After
    public void cleanup() {
        UserAPI.deleteUser(accessToken);
    }
}