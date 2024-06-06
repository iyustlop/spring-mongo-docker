package com.olivar.springbootmongodbdocker.infrastructure.rest.core;

import com.olivar.springbootmongodbdocker.annotations.SslWebTestClientSpringBootTest;
import com.olivar.springbootmongodbdocker.infrastructure.rest.core.dto.ManufactureObjectMother;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MongoDBContainer;

@SslWebTestClientSpringBootTest
class ManufacturerResourceIT {

	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

	@DynamicPropertySource
	static void  configureProperties(DynamicPropertyRegistry registry){
		registry.add("spring.data.mongodb.uri", mongoDBContainer::getConnectionString);
	}

	@BeforeAll
	static void beforeAll(){
		mongoDBContainer.start();
	}

	@AfterAll
	static void afterAll(){
		mongoDBContainer.stop();
	}

	@Autowired
	private WebTestClient webTestClient;

	@Test
	@DisplayName("Given a valid manufacturer When creates Then response with HTTP200 and the agent detail")
	void create_a_valid_agent() {
		webTestClient.post().uri("/manufacturer")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.bodyValue(ManufactureObjectMother.randomManufacturerRequest())
				.exchange()
				.expectStatus().isOk();
	}
}
