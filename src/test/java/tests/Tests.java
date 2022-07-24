package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import models.lombok.GenerateData;
import models.lombok.MorpheusData;
import models.lombok.UserData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static specs.Specs.*;

public class Tests {

    @Owner("Никита")
    @DisplayName("Обновление данных о пользователе")
    @Description("Изменение имени и профессии уже существующего пользователя")
    @Test
    void createTest() {
        UserData userData = new UserData();
        userData.setName("Misha");
        userData.setJob("QA");
        MorpheusData morpheusData =
                given()
                        .spec(reqresIn)
                        .body(userData)
                        .when()
                        .patch("/api/users/2")
                        .then()
                        .log().status()
                        .log().body()
                        .spec(positiveSpec)
                        .extract().as(MorpheusData.class);

        assertThat(morpheusData.getName()).isEqualTo("Misha");
        assertThat(morpheusData.getJob()).isEqualTo("QA");
    }

    @Owner("Никита")
    @DisplayName("Успешный запрос на наличие пользователя")
    @Test
    void succsessfulTest() {
        UserData userData = new UserData();
        userData.setEmail("eve.holt@reqres.in");
        userData.setPassword("cityslicka");
        GenerateData dataUser =
                given()
                        .spec(reqresIn)
                        .body(userData)
                        .when()
                        .post("/api/login")
                        .then()
                        .log().status()
                        .log().body()
                        .spec(positiveSpec)
                        .extract().as(GenerateData.class);
        assertThat(dataUser.getToken()).isNotNull();

    }

    @Owner("Никита")
    @DisplayName("ошибка в Email клиента")
    @Description("Ошибка должна быть на стороне клиента")
    @Test
    void unsuccsessfulTest() {
        UserData userData = new UserData();
        userData.setEmail("eve.holt@reqres.com");
        userData.setPassword("cityslicka");
        GenerateData dataUser =
                given()
                        .spec(reqresIn)
                        .body(userData)
                        .when()
                        .post("/api/login")
                        .then()
                        .log().status()
                        .log().body()
                        .spec(negativeSpec)
                        .extract().as(GenerateData.class);
        assertThat(dataUser.getError()).isEqualTo("user not found");
    }

    @Owner("Никита")
    @DisplayName("Ошибка 404 из-за ошибки в URL")
    @Description("По адресу /api/unknown/23 отсутствует пользователь, должна быть 404")
    @Test
    void notFoundTest() {
        given()
                .spec(reqresIn)
                .when()
                .get("/api/unknown/23")
                .then()
                .log().status()
                .log().body()
                .spec(notFoundSpec);
    }

    @Owner("Никита")
    @Test
    @DisplayName("Проверка URl' в support")
    void checkUrl() {
        given()
                .spec(reqresIn)
                .when()
                .get("/api/users/2")
                .then()
                .log().status()
                .log().body()
                .spec(positiveSpec)
                .body("support.url", is("https://reqres.in/#support-heading"));
    }

    @Owner("Никита")
    @Test
    @DisplayName("Проверка имени пользователя")
    void checkFirstName() {
        given()
                .spec(reqresIn)
                .when()
                .get("/api/users/2")
                .then()
                .log().status()
                .log().body()
                .spec(positiveSpec)
                .body("data.first_name", is("Janet"));
    }

}
