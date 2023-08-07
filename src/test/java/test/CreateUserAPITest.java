package test;

import io.qameta.allure.Description;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import test.api.UserAPI;

public class CreateUserAPITest {
    private String accessToken;

    @Before
    public void setUp() {
        accessToken = UserAPI.createUser("uniqueuser1@test.com", "test123", "Unique User");
    }

    @Description("Выход пользователя")
    @Test
    public void testUserExists() {
        UserAPI.getUserInfo(accessToken, "uniqueuser1@test.com", "Unique User");
    }

    @Description("Регистрация с теми же данными")
    @Test
    public void testCreateAlreadyRegisteredUser() {
        UserAPI.createAlreadyRegisteredUser("uniqueuser1@test.com", "test123", "Unique User");
    }

    @Description("Регистрация без имени")
    @Test
    public void testCreateUserWithMissingField() {
        UserAPI.createUserWithMissingField("test@test.com", "test123");
    }

    @After
    public void cleanup() {
        UserAPI.deleteUser(accessToken);
    }
}