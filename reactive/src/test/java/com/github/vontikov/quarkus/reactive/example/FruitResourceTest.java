package com.github.vontikov.quarkus.reactive.example;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.IsNot.not;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
class FruitResourceTest {

    @Test
    public void testListAllFruits() {
        given()
                .when().get("/fruits")
                .then()
                .statusCode(200)
                .body(
                        containsString("Kiwi"),
                        containsString("Durian"),
                        containsString("Pomelo"),
                        containsString("Lychee"));

        given()
                .when().delete("/fruits/1")
                .then()
                .statusCode(204);

        given()
                .when().get("/fruits")
                .then()
                .statusCode(200)
                .body(
                        not(containsString("Kiwi")),
                        containsString("Durian"),
                        containsString("Pomelo"),
                        containsString("Lychee"));
    }
}
