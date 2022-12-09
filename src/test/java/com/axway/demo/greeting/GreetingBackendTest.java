package com.axway.demo.greeting;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class GreetingBackendTest {

	@Test
	public void testGetDefaultGreeting() {
		given() //
				.when().get("/greetings") //
				.then().statusCode(200).contentType(is(MediaType.APPLICATION_JSON))
				.body("message", is(GreetingService.DEFAULT_GREETING));
	}
}