package order;

import api.model.Ingredient;
import api.model.Ingredients;
import api.model.Order;
import api.model.User;
import api.steps.OrderSteps;
import api.steps.RestClient;
import api.steps.UserSteps;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static io.restassured.RestAssured.given;


public class CreateOrderTest extends RestClient {

    private String name;
    private String email;
    private String password;
    private UserSteps userSteps;
    private User user;
    private String accessToken;
    private OrderSteps orderSteps;
    private List<String> ingr;
    private Order order;


    @Before
    public void setUp() {
        RestClient.getBaseSpec();
        name = RandomStringUtils.randomAlphanumeric(4, 20);
        email = RandomStringUtils.randomAlphanumeric(6, 10) + "@ya.ru";
        password = RandomStringUtils.randomAlphanumeric(10, 20);
        userSteps = new UserSteps();
        orderSteps = new OrderSteps();
        user = new User(name, email, password);
        Response resp = userSteps.sendPostRequestApiAuthRegister(user);
        accessToken = JsonPath.from(resp.getBody().asString()).get("accessToken");
        ingr = new ArrayList<>();
        order = new Order(ingr);
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    @Description("Создание заказа без авторизации. Проверка успешного ответа от сервера")
    public void createOrderWithoutAuthorizationTest() {
        Ingredients ingredients = orderSteps.getIngredients();
        ingr.add(ingredients.getData().get(0).get_id());
        ingr.add(ingredients.getData().get(8).get_id());
        ingr.add(ingredients.getData().get(4).get_id());
        Response response = orderSteps.createOrderWithoutAuthorization(order);
        response.then().log().all()
                .assertThat().body("success", Matchers.is(true))
                .and().body("name", Matchers.notNullValue())
                .and().body("order.number", Matchers.any(Integer.class))
                .and().statusCode(200);
    }

    @Test
    @DisplayName("Создание заказа с авторизацией")
    @Description("Создание заказа с авторизацией. Проверка успешного ответа от сервера")
    public void createOrderWithAuthorizationTest() throws InterruptedException {
        Ingredients ingredients = orderSteps.getIngredients();
        Thread.sleep(100);
        ingr.add(ingredients.getData().get(1).get_id());
        ingr.add(ingredients.getData().get(3).get_id());
        ingr.add(ingredients.getData().get(5).get_id());
        int sumPrice = ingredients.getData().stream().filter(ingredient -> ingr.contains(ingredient.get_id()))
                .map(Ingredient::getPrice).mapToInt(i -> i).sum();
        Response response = orderSteps.createOrderWithAuthorization(order, accessToken);
        response.then().log().all()
                .assertThat().body("success", Matchers.is(true))
                .and().body("name", Matchers.notNullValue())
                .and().body("order.number", Matchers.any(Integer.class))
                .and().body("order.ingredients", Matchers.notNullValue())
                .and().body("order._id", Matchers.notNullValue())
                .and().body("order.owner.name", Matchers.is(name))
                .and().body("order.owner.email", Matchers.is(email.toLowerCase(Locale.ROOT)))
                .and().body("order.status", Matchers.is("done"))
                .and().body("order.name", Matchers.notNullValue())
                .and().body("order.price", Matchers.is(sumPrice))
                .and().statusCode(200);
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов и без авторизации")
    @Description("Создание заказа без ингредиентов и без авторизации. Проверка неуспешного ответа от сервера")
    public void createEmptyOrderWithoutAuthorization() {
        Response response = orderSteps.createOrderWithoutAuthorization(order);
        orderSteps.checkFailedResponseApiOrders(response);
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов с авторизацией")
    @Description("Создание заказа без ингредиентов с авторизацией. Проверка неуспешного ответа от сервера")
    public void createEmptyOrderWithAuthorization() throws InterruptedException {
        Thread.sleep(100);
        Response response = orderSteps.createOrderWithAuthorization(order, accessToken);
        orderSteps.checkFailedResponseApiOrders(response);
    }

    @Test
    @DisplayName("Создание заказа без авторизации с неверным хэшем ингредиентов")
    @Description("Создание заказа без авторизации с неверным хэшем ингредиентов. Проверка ошибки сервера")
    public void createOrderWithoutAuthorizationWithWrongHashTest() throws InterruptedException {
        Ingredients ingredients = orderSteps.getIngredients();
        Thread.sleep(100);
        ingr.add(ingredients.getData().get(0).get_id() + "1234qwerty1234");
        ingr.add(ingredients.getData().get(8).get_id() + "5678uiop00");
        Response response = orderSteps.createOrderWithoutAuthorization(order);
        response.then().log().all()
                .statusCode(500);
    }

    @Test
    @DisplayName("Создание заказа с авторизацией с неверным хешем ингредиентов")
    @Description("Создание заказа с авторизацией с неверным хешем ингредиентов. Проверка ошибки сервера")
    public void createOrderWithAuthorizationWithWrongHashTest() {
        Ingredients ingredients = orderSteps.getIngredients();
        ingr.add(ingredients.getData().get(1).get_id() + "1234qwerty1234");
        ingr.add(ingredients.getData().get(2).get_id() + "5678uiop00");
        Response response = orderSteps.createOrderWithAuthorization(order, accessToken);
        response.then().log().all()
                .statusCode(500);
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