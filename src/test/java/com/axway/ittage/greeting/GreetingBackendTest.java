package com.axway.ittage.greeting;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import javax.ws.rs.core.MediaType;

@QuarkusTest
public class GreetingBackendTest {

    @Test
    @TestSecurity(authorizationEnabled = false)
    public void testHelloEndpoint() {
        given()
          .when().get("/greetings")
          .then()
             .statusCode(200)
             .contentType(is(MediaType.APPLICATION_JSON))
             .body("message", is("Hallo IT-Tage!"));
    }

}