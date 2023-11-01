package order;

import api.model.User;
import api.steps.OrderClient;
import api.steps.UserClient;
import api.steps.UserCredentials;
import api.steps.UserGeneration;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;

public class GetUserOrderTests {
    private final UserClient userClient = new UserClient();
    private final User randomUser = UserGeneration.createRandomUser();
    private final UserCredentials userCredentials = new UserCredentials(randomUser.getEmail(), randomUser.getPassword(), randomUser.getName());
    private final OrderClient orderClient = new OrderClient();
    private int actualStatusCode;
    private String accessToken;

    @Test
    @DisplayName("Получаем список заказов юзера")
    public void getOrdersUserTest() {
        UserClient.createUser(randomUser);
        ValidatableResponse userResponse = UserClient.authUser(userCredentials);
        accessToken = userResponse.extract().path("accessToken");
        ValidatableResponse response = orderClient.getOrders(accessToken);
        actualStatusCode = response.extract().statusCode();
        Assert.assertEquals(SC_OK, actualStatusCode);
    }

    @Test
    @DisplayName("Получаем список заказов неавторизированного юзера")
    public void getOrdersUserWithoutAuthTest() {
        UserClient.createUser(randomUser);
        ValidatableResponse response = orderClient.getOrdersWithoutAuthorization();
        actualStatusCode = response.extract().statusCode();
        Assert.assertEquals(SC_UNAUTHORIZED, actualStatusCode);
    }

    @After
    public void cleanUp() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }

}