package com.olivar.springbootmongodbdocker.infrastructure.inboud.rest.core;

import com.olivar.springbootmongodbdocker.dao.ManufacturerRepo;
import com.olivar.springbootmongodbdocker.domain.model.Manufacturer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mongodb.MongoDBContainer;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureTestRestTemplate
class ManufacturerIT {

    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0")
            .waitingFor(
                    Wait.forSuccessfulCommand("mongosh --quiet --eval \"db.runCommand({ ping: 1 })\"")
                            .withStartupTimeout(Duration.ofSeconds(120))
            );

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ManufacturerRepo manufacturerRepo;

    @BeforeEach
    void setUp() {
        manufacturerRepo.deleteAll();
    }

    @Test
    void shouldSaveManufacturerAndPersistItInMongo() {
        Manufacturer request = new Manufacturer("Porsche", "P01");

        ResponseEntity<Manufacturer> postResponse = restTemplate.postForEntity(
                "/manufacturer", request, Manufacturer.class
        );

        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(postResponse.getBody()).isNotNull();
        assertThat(postResponse.getBody().getName()).isEqualTo("Porsche");
        assertThat(postResponse.getBody().getCode()).isEqualTo("P01");
        assertThat(manufacturerRepo.findAll()).hasSize(1);
    }

    @Test
    void shouldRetrieveAllManufacturers() {
        manufacturerRepo.save(new Manufacturer("Ferrari", "F01"));
        manufacturerRepo.save(new Manufacturer("Lamborghini", "L01"));

        ResponseEntity<List<Manufacturer>> getResponse = restTemplate.exchange(
                "/manufacturer",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).hasSize(2);
        assertThat(getResponse.getBody())
                .extracting(Manufacturer::getCode)
                .containsExactlyInAnyOrder("F01", "L01");
    }
}