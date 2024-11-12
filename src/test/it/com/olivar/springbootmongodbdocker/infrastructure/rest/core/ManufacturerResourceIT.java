package com.olivar.springbootmongodbdocker.infrastructure.rest.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.olivar.springbootmongodbdocker.infrastructure.rest.core.dto.ManufactureObjectMother;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
class ManufacturerResourceIT {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo");
    @Autowired
    private MockMvc mockMvc;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getConnectionString);
    }

    @BeforeAll
    static void beforeAll() {
        mongoDBContainer.start();
    }

/*    @AfterAll
    static void afterAll() {
        System.out.println("apago el docker");
        mongoDBContainer.stop();
    }*/


    @Test
    @DisplayName("Given a valid manufacturer When creates Then response with HTTP200 and the agent detail")
    void create_a_valid_agent() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/manufacturer")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(ManufactureObjectMother.randomManufacturerRequest()))
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Get the manufacturer stored in the database")
    void get_previous_manufacturer() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                    .get("/manufacturer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    private String asJsonString(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

}
