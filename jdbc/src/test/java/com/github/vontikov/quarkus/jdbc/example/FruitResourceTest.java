package com.github.vontikov.quarkus.jdbc.example;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

@QuarkusTest
@QuarkusTestResource(PostgresResource.class) 
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
                .when().get("/fruits/1")
                .then()
                .statusCode(200)
                .body(containsString("Kiwi"));

        given()
                .when().get("/fruits/100")
                .then()
                .statusCode(404);

        given()
                .when().delete("/fruits/1")
                .then()
                .statusCode(204);

        given()
                .when().get("/fruits/1")
                .then()
                .statusCode(404);

        given()
                .when().delete("/fruits/100")
                .then()
                .statusCode(404);

        given()
                .contentType("application/json") 
                .body("{\"name\": \"Watermelon\"}")
                .when().post("/fruits")
                .then()
                .statusCode(201);
    }
}
