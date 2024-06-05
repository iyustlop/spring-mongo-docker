package com.olivar.springbootmongodbdocker.infrastructure.rest.core;

import com.olivar.springbootmongodbdocker.annotations.SslWebTestClientSpringBootTest;
import com.olivar.springbootmongodbdocker.infrastructure.rest.core.dto.ManufactureObjectMother;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SslWebTestClientSpringBootTest
class ManufacturerResourceIT {

	@Autowired
	private WebTestClient webTestClient;

	@Test
	@DisplayName("Given a valid manufacturer When creates Then response with HTTP200 and the agent detail")
	void create_a_valid_agent() {
		webTestClient.post().uri("")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.bodyValue(ManufactureObjectMother.randomManufacturerRequest())
				.exchange()
				.expectStatus().isOk();
	}

}
