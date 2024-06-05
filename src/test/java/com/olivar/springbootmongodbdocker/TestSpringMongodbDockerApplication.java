package com.olivar.springbootmongodbdocker;

import com.olivar.springbootmongodbdocker.model.Manufacturer;
import com.olivar.springbootmongodbdocker.service.ManufacturerService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class TestSpringMongodbDockerApplication {

    @Container
    static MongoDBContainer mongo = new MongoDBContainer("mongo:latest");
    private ManufacturerService manufacturerService= new ManufacturerService();

    @DynamicPropertySource
    public static void mongoProperties(DynamicPropertyRegistry registry){
        registry.add("spring.data.mongodb.uri", mongo::getConnectionString);
    }

    @Test
    public void writeAManufacturer(){
        Manufacturer manufacturer = new Manufacturer("AAAA", "Abarth");
        manufacturerService.saveManufacturer(manufacturer);
    }
}
