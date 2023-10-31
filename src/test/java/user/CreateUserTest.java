package user;

import api.model.User;
import api.steps.RestClient;
import api.steps.UserSteps;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

import static io.restassured.RestAssured.given;

public class CreateUserTest extends RestClient {

    private String name;
    private String email;
    private String password;
    private UserSteps userSteps;
    private User user;



    @Before
    public void createRandomData() throws InterruptedException {
        RestClient.getBaseSpec();
        name = RandomStringUtils.randomAlphanumeric(4, 20);
        email = RandomStringUtils.randomAlphanumeric(6, 10) + "@yandex.ru";
        password = RandomStringUtils.randomAlphanumeric(10, 20);
        userSteps = new UserSteps();
        user = new User();
        Thread.sleep(200);
    }


    @Test
    @DisplayName("Регистрация уникального пользователя")
    @Description("Регистрация уникального пользователя со случайным набором данных. Проверка успешного ответа сервера")
    public void createUserTest() {
        user = new User(name, email, password);
        Response response = userSteps.sendPostRequestApiAuthRegister(user);
        response.then().log().all()
                .assertThat().body("success", Matchers.is(true))
                .and().body("user.email", Matchers.is(email.toLowerCase(Locale.ROOT)))
                .and().body("user.name", Matchers.is(name))
                .and().body("accessToken", Matchers.notNullValue())
                .and().body("refreshToken", Matchers.notNullValue())
                .and().statusCode(200);
    }


    @Test
    @DisplayName("Регистрация уже созданного пользователя")
    @Description("Регистрация уже созданного пользователя со случайным набором данных. Проверка неуспешного ответа сервера")
    public void createTwoIdenticalUsersTest() throws InterruptedException {
        user = new User(name, email, password);
        userSteps.sendPostRequestApiAuthRegister(user);
        Thread.sleep(100);
        Response response = userSteps.sendPostRequestApiAuthRegister(user);
        response.then().log().all()
                .assertThat().body("success", Matchers.is(false))
                .and().body("message", Matchers.is("User already exists"))
                .and().statusCode(403);
    }

    @Test
    @DisplayName("Регистрация пользователя без имени")
    @Description("Регистрация пользователя без имени, но со случайными e-mail и паролем. Проверка неуспешного ответа сервера")
    public void createUserWithoutNameTest() {
        user.setEmail(email);
        user.setPassword(password);
        Response response = userSteps.sendPostRequestApiAuthRegister(user);
        userSteps.checkFailedResponseApiAuthRegister(response);
    }

    @Test
    @DisplayName("Регистрация пользователя без E-mail")
    @Description("Регистрация пользователя без E-mail, но со случайными именем и паролем. Проверка неуспешного ответа сервера")
    public void createUserWithoutEmailTest() {
        user.setName(name);
        user.setPassword(password);
        Response response = userSteps.sendPostRequestApiAuthRegister(user);
        userSteps.checkFailedResponseApiAuthRegister(response);
    }

    @Test
    @DisplayName("Регистрация пользователя без пароля")
    @Description("Регистрация пользователя без пароля, но со случайными E-mail и именем. Проверка неуспешного ответа сервера")
    public void createUserWithoutPasswordTest() {
        user.setEmail(email);
        user.setName(name);
        Response response = userSteps.sendPostRequestApiAuthRegister(user);
        userSteps.checkFailedResponseApiAuthRegister(response);
    }

    @Test
    @DisplayName("Регистрация пользователя без имени и E-mail")
    @Description("Регистрация пользователя без имени и E-mail, но со случайным паролем. Проверка неуспешного ответа сервера")
    public void createUserWithoutNameAndEmailTest() {
        user.setPassword(password);
        Response response = userSteps.sendPostRequestApiAuthRegister(user);
        userSteps.checkFailedResponseApiAuthRegister(response);
    }

    @Test
    @DisplayName("Регистрация пользователя без имени и пароля")
    @Description("Регистрация пользователя без имени и пароля, но со случайным E-mail. Проверка неуспешного ответа сервера")
    public void createUserWithoutNameAndPasswordTest() {
        user.setEmail(email);
        Response response = userSteps.sendPostRequestApiAuthRegister(user);
        userSteps.checkFailedResponseApiAuthRegister(response);
    }

    @Test
    @DisplayName("Регистрация пользователя без E-mail и пароля")
    @Description("Регистрация пользователя без E-mail и пароля, но со случайным именем. Проверка неуспешного ответа сервера")
    public void createUserWithoutEmailAndPasswordTest() {
        user.setName(name);
        Response response = userSteps.sendPostRequestApiAuthRegister(user);
        userSteps.checkFailedResponseApiAuthRegister(response);
    }

    @Test
    @DisplayName("Регистрация пользователя без всех данных")
    @Description("Регистрация пользователя без всех данных. Проверка неуспешного ответа сервера")
    public void createUserWithoutAllTest() {
        Response response = userSteps.sendPostRequestApiAuthRegister(user);
        userSteps.checkFailedResponseApiAuthRegister(response);
    }

    @After
    @DisplayName("Удаление пользователя")
    @Description("Удаление пользователя с созданными рандомными данными")
    public void deleteRandomUser() {
        given().log().all()
                .spec(getBaseSpec())
                .header("accessToken", "application/json")
                .body(user)
                .delete();
    }
    }
