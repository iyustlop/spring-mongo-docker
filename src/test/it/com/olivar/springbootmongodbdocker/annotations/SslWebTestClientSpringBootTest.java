package com.olivar.springbootmongodbdocker.annotations;

import com.olivar.springbootmongodbdocker.config.WebTestClientConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(WebTestClientConfiguration.class)
@TestPropertySource("/application-test.yml")
public @interface SslWebTestClientSpringBootTest {
}
