package user;

import api.model.User;
import api.steps.UserSteps;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

import static io.restassured.RestAssured.given;

public class LoginUserTest {

    private String email;
    private String password;
    private String name;
    private UserSteps userSteps;
    private User user;
    private User authUser;

    @Before
    public void setUp() throws InterruptedException {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        name = RandomStringUtils.randomAlphanumeric(4, 20);
        email = RandomStringUtils.randomAlphanumeric(6, 10) + "@yandex.ru";
        password = RandomStringUtils.randomAlphanumeric(10, 20);
        userSteps = new UserSteps();
        user = new User(name, email, password);
        authUser = new User();
        userSteps.sendPostRequestApiAuthRegister(user);
        Thread.sleep(200);
    }

    @Test
    @DisplayName("Авторизация пользователя")
    @Description("Авторизация пользователя с зарегистрированными случайными данными. Проверка успешного ответа сервера")
    public void authorizationTest() {
        Response response = userSteps.sendPostRequestApiAuthLogin(user);
        response.then().log().all()
                .assertThat().body("success", Matchers.is(true))
                .and().body("accessToken", Matchers.notNullValue())
                .and().body("refreshToken", Matchers.notNullValue())
                .and().body("user.email", Matchers.is(user.getEmail().toLowerCase(Locale.ROOT)))
                .and().body("user.name", Matchers.is(name))
                .and().statusCode(200);
    }

    @Test
    @DisplayName("Авторизация пользователя без E-mail")
    @Description("Авторизация пользователя без E-mail. Проверка неуспешного ответа сервера.")
    public void authorizationWithoutEmailTest() {
        authUser.setPassword(password);
        Response response = userSteps.sendPostRequestApiAuthLogin(authUser);
        userSteps.checkFailedResponseApiAuthLogin(response);

    }

    @Test
    @DisplayName("Авторизация пользователя без пароля")
    @Description("Авторизация пользователя без пароля. Проверка неуспешного ответа сервера")
    public void authorizationWithoutPasswordTest() {
        authUser.setEmail(email);
        Response response = userSteps.sendPostRequestApiAuthLogin(authUser);
        userSteps.checkFailedResponseApiAuthLogin(response);
    }

    @Test
    @DisplayName("Авторизация пользователя без E-mail и пароля")
    @Description("Авторизация пользователя без E-mail и пароля. Проверка неуспешного ответа сервера")
    public void authorizationWithoutEmailAndPasswordTest() {
        Response response = userSteps.sendPostRequestApiAuthLogin(authUser);
        userSteps.checkFailedResponseApiAuthLogin(response);
    }

    @Test
    @DisplayName("Авторизация пользователя с неверным E-mail")
    @Description("Авторизация пользователя с невалидными E-mail и с валидными случайными паролем и именем" +
            "Проверка неуспешного ответа сервера")
    public void authorizationWithWrongEmailTest() {
        authUser = new User(name, email, password);
        authUser.setEmail("mrrr" + email);
        Response response = userSteps.sendPostRequestApiAuthLogin(authUser);
        userSteps.checkFailedResponseApiAuthLogin(response);
    }

    @Test
    @DisplayName("Авторизация пользователя с невалидным паролем")
    @Description("Авторизация пользователя с невалидным паролем и с валидными случайными E-mail и именем" +
            "Проверка неуспешного ответа сервера")
    public void authorizationWithWrongPasswordTest() {
        authUser = new User(name, email, password);
        authUser.setPassword(password + "8765");
        Response response = userSteps.sendPostRequestApiAuthLogin(authUser);
        userSteps.checkFailedResponseApiAuthLogin(response);
    }

    @After
    public void deleteRandomUser() {
        given().log().all()
                .header("Content-Type", "application/json")
                .body(user)
                .delete("/api/auth/user");
    }
}