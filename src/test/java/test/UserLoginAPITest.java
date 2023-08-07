package test;

import io.qameta.allure.Description;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import test.api.UserAPI;

public class UserLoginAPITest {
    private String accessToken;

    @Before
    public void setUp() {
        accessToken = UserAPI.createUser("uniqueuser1@test.com", "test123", "Unique User");
    }

    @Description("Проверка авторизации")
    @Test
    public void testLoginWithValidCredentials() {
        UserAPI.loginWithValidCredentials("uniqueuser1@test.com", "test123");
    }

    @Description("Проверка невалидной авторизации")
    @Test
    public void testLoginWithInvalidCredentials() {
        UserAPI.loginWithInvalidCredentials("uniqueuser1@test.com", "incorrectPassword");
    }

    @Description("Проверка входа с измененными учетными данными")
    @Test
    public void testLoginWithChangedCredentials() {
        UserAPI.loginWithChangedCredentials("uniqueuser1@test.com", "changedPassword");
    }

    @After
    public void cleanup() {
        UserAPI.deleteUser(accessToken);
    }
}
