package test;

import io.qameta.allure.Description;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import test.api.UserAPI;

public class ChangeUserDataTest {
    private String accessToken;
    private String changedEmail = "uniqueuser1_changed@test.com";
    private String changedName = "Changed Name";

    @Before
    public void setUp() {
        accessToken = UserAPI.createUser("uniqueuser1@test.com", "test123", "Unique User");
    }

    @Description("Обновление данных авторизованного юзера")
    @Test
    public void testUpdateUserDataWithAuthorization() {
        UserAPI.updateUserData(accessToken, changedEmail, changedName);
    }

    @Description("Обновление данных неавторизованного юзера")
    @Test
    public void testUpdateUserDataWithoutAuthorization() {
        UserAPI.updateUserDataWithoutAuthorization(changedEmail, changedName);
    }

    @After
    public void cleanup() {
        UserAPI.deleteUser(accessToken);
    }
}
