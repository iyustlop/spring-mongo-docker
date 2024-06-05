package com.olivar.springbootmongodbdocker.infrastructure.rest.core;

import com.olivar.springbootmongodbdocker.annotations.SslWebTestClientSpringBootTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@SslWebTestClientSpringBootTest
class SpringbootMongodbDockerApplicationTests {


	@Test
	@DisplayName("Given a valid manufacturer When creates Then response with HTTP200 and the agent detail")
	void create_a_valid_agent() {

	}

}
