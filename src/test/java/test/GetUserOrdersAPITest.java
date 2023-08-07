package test;

import io.qameta.allure.Description;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import test.api.OrderAPI;
import test.api.UserAPI;

public class GetUserOrdersAPITest {
    private String accessToken;

    @Before
    public void setUp() {
        accessToken = UserAPI.createUser("uniqueuser1@test.com", "test123", "Unique User");
    }

    @Description("Описание вашего теста здесь")
    @Test
    public void testGetUserOrdersWithAuthorization() {
        OrderAPI.getUserOrdersWithAuthorization(accessToken);
    }

    @Description("Описание вашего теста здесь")
    @Test
    public void testGetUserOrdersWithoutAuthorization() {
        OrderAPI.getUserOrdersWithoutAuthorization();
    }

    @After
    public void cleanup() {
        UserAPI.deleteUser(accessToken);
    }
}
