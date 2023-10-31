package order;

import api.model.User;
import api.steps.RestClient;
import api.steps.UserSteps;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;

public class GetUserOrderTest extends RestClient {

    private String email;
    private String password;
    private String name;
    private UserSteps userSteps;
    private User user;
    private String accessToken;
    private static final String ORDERS_PATH = "api/orders";

    @Before
    public void setUp() throws InterruptedException {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        name = RandomStringUtils.randomAlphanumeric(4, 20);
        email = RandomStringUtils.randomAlphanumeric(6, 10) + "@ya.ru";
        password = RandomStringUtils.randomAlphanumeric(10, 20);
        userSteps = new UserSteps();
        user = new User(name, email, password);
        Response resp = userSteps.sendPostRequestApiAuthRegister(user);
        accessToken = JsonPath.from(resp.getBody().asString()).get("accessToken");
        Thread.sleep(200);
    }

    @Test
    @DisplayName("Получение списка заказов авторизованного пользователя")
    @Description("Получение списка заказов авторизованного пользователя. Проверка успешного ответа от сервера")
    public void getUserOrderWithAuthorizationTest() {
        Response response = given().log().all()
                .header("Content-Type", "application/json")
                .header("authorization", accessToken)
                .when()
                .get(ORDERS_PATH);
        response.then().log().all()
                .assertThat().body("success", Matchers.is(true))
                .and().body("orders", Matchers.notNullValue())
                .and().body("total", Matchers.any(Integer.class))
                .and().body("totalToday", Matchers.any(Integer.class))
                .and().statusCode(200);
    }


    @Test
    @DisplayName("Получение списка заказов без авторизации")
    @Description("Получение списка заказов без авторизации. Проверка неуспешного ответа от сервера")
    public void getUserOrderWithoutAuthorizationTest() {
        Response response = given().log().all()
                .header("Content-Type", "application/json")
                .when()
                .get(ORDERS_PATH);
        response.then().log().all()
                .assertThat().body("success", Matchers.is(false))
                .and().body("message", Matchers.is("You should be authorised"))
                .and().statusCode(401);
    }

    @After
    public void deleteRandomUser() {
        given().log().all()
                .spec(getBaseSpec())
                .header("accessToken", "application/json")
                .body(user)
                .delete();
    }
}