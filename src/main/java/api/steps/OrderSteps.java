package api.steps;

import api.model.Ingredients;
import api.model.Order;
import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.response.Response;
import org.hamcrest.Matchers;

import static io.restassured.RestAssured.given;

public class OrderSteps {

    @Step("Получение списка ингредиентов(объектов). GET запрос на ручку /api/ingredients")
    public Ingredients getIngredients(){
        return given()
                .header("Content-Type", "application/json")
                .log().all()
                .get("/api/ingredients")
                .body()
                .as(Ingredients.class);
    }

    @Step("Создание заказа с авторизацией. POST запрос на ручку /api/orders")
    public Response createOrderWithAuthorization(Order order, String token){
        return given().log().all().filter(new AllureRestAssured())
                .header("Content-Type", "application/json")
                .header("authorization", token)
                .body(order)
                .when()
                .post("/api/orders");
    }

    @Step("Создание заказа без авторизации. POST запрос на ручку /api/orders")
    public  Response createOrderWithoutAuthorization(Order order){
        return given().log().all()
                .filter(new AllureRestAssured())
                .header("Content-Type", "application/json")
                .body(order)
                .when()
                .post("/api/orders");
    }

    @Step("Неуспешный ответ сервера на создание заказа без ингредиентов")
    public void checkFailedResponseApiOrders(Response response){
        response.then().log().all()
                .assertThat().body("success", Matchers.is(false))
                .and().body("message", Matchers.is("Ingredient ids must be provided"))
                .and().statusCode(400);
    }

}