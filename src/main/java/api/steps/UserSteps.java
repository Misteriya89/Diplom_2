package api.steps;

import api.model.User;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.hamcrest.Matchers;

import java.util.Locale;

import static io.restassured.RestAssured.given;

public class UserSteps {

    private final static String ERROR_MESSAGE_TEXT_REGISTER = "Email, password and name are required fields";
    private final static String ERROR_MESSAGE_TEXT_LOGIN = "email or password are incorrect";
    private final static String ERROR_MESSAGE_TEXT_USER = "You should be authorised";

    @Step("Регистрация пользователя. POST запрос на ручку /api/auth/register")
    public Response sendPostRequestApiAuthRegister(User user) {
        return given().log().all()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post("/api/auth/register");
    }

    @Step("Неуспешный ответ сервера на попытку регистрации пользователя")
    public void checkFailedResponseApiAuthRegister(Response response) {
        response.then().log().all()
                .assertThat().body("success", Matchers.is(false))
                .and().body("message", Matchers.is(ERROR_MESSAGE_TEXT_REGISTER))
                .and().statusCode(403);
    }


    @Step("Авторизация пользователя. POST запрос на ручку /api/auth/login")
    public Response sendPostRequestApiAuthLogin(User user) {
        return given()
                .log()
                .all()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post("/api/auth/login");
    }

    @Step("Неуспешный ответ сервера на попытку авторизации пользователя")
    public void checkFailedResponseApiAuthLogin(Response response) {
        response.then().log().all()
                .assertThat().body("success", Matchers.is(false))
                .and().body("message", Matchers.is(ERROR_MESSAGE_TEXT_LOGIN))
                .and().statusCode(401);
    }

    @Step("Изменение данных пользователя с авторизацией. PATCH запрос на ручку /api/auth/user")
    public Response sendPatchRequestWithAuthorizationApiAuthUser(User user, String token) {
        return given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("authorization", token)
                .body(user)
                .when()
                .patch("/api/auth/user");
    }

    @Step("Изменение данных пользователя без авторизации. PATCH запрос на ручку /api/auth/user")
    public Response sendPatchRequestWithoutAuthorizationApiAuthUser(User user) {
        return given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .body(user)
                .when()
                .patch("/api/auth/user");
    }

    @Step("Успешный ответ сервера на изменение данных пользователя")
    public void checkSuccessResponseApiAuthUser(Response response, String email, String name) {
        response.then().log().all()
                .assertThat()
                .body("success", Matchers.is(true))
                .and().body("user.email", Matchers.is(email.toLowerCase(Locale.ROOT)))
                .and().body("user.name", Matchers.is(name))
                .and().statusCode(200);
    }

    @Step("Неуспешный ответ сервера на изменение данных пользователя")
    public void checkFailedResponseApiAuthUser(Response response) {
        response.then().log().all()
                .assertThat().body("success", Matchers.is(false))
                .and().body("message", Matchers.is(ERROR_MESSAGE_TEXT_USER))
                .and().statusCode(401);
    }
}