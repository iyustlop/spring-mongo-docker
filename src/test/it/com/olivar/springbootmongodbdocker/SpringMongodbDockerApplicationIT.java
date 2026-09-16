package com.olivar.springbootmongodbdocker;

import com.olivar.springbootmongodbdocker.dao.ManufacturerRepo;
import com.olivar.springbootmongodbdocker.domain.model.Manufacturer;
import com.olivar.springbootmongodbdocker.service.ManufacturerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mongodb.MongoDBContainer;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class SpringMongodbDockerApplicationIT {

    @Container
    @ServiceConnection
    static MongoDBContainer mongo = new MongoDBContainer("mongo:7.0")
            .waitingFor(
                    Wait.forSuccessfulCommand("mongosh --quiet --eval \"db.runCommand({ ping: 1 })\"")
                            .withStartupTimeout(Duration.ofSeconds(120))
            );

    @Autowired
    private ManufacturerService manufacturerService;

    @Autowired
    private ManufacturerRepo manufacturerRepo;

    @Test
    void writeAManufacturer() {
        Manufacturer manufacturer = new Manufacturer("AAAA", "Abarth");

        manufacturerService.saveManufacturer(manufacturer);

        assertThat(manufacturerRepo.findAll()).hasSize(1);
    }
}