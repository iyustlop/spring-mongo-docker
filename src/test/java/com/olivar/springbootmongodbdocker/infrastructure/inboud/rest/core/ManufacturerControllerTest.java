package com.olivar.springbootmongodbdocker.infrastructure.inboud.rest.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.olivar.springbootmongodbdocker.domain.model.Manufacturer;
import com.olivar.springbootmongodbdocker.service.ManufacturerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ManufacturerControllerTest {

    @Mock
    private ManufacturerService service;

    @InjectMocks
    private ManufacturerController controller;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void saveManufacturerReturnsSavedManufacturer() throws Exception {
        Manufacturer manufacturer = new Manufacturer("Ferrari", "F01");
        when(service.saveManufacturer(manufacturer)).thenReturn(manufacturer);

        mockMvc.perform(post("/manufacturer")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(manufacturer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ferrari"))
                .andExpect(jsonPath("$.code").value("F01"));

        verify(service).saveManufacturer(manufacturer);
    }

    @Test
    void getManufacturerReturnsAllManufacturers() throws Exception {
        when(service.getAllManufacturer()).thenReturn(List.of(
                new Manufacturer("Ferrari", "F01"),
                new Manufacturer("Lamborghini", "L01")
        ));

        mockMvc.perform(get("/manufacturer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Ferrari"))
                .andExpect(jsonPath("$[1].code").value("L01"));

        verify(service).getAllManufacturer();
    }
}