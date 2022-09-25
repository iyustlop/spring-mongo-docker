package com.olivar.springbootmongodbdocker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.olivar.springbootmongodbdocker.dao.ManufacturerRepo;
import com.olivar.springbootmongodbdocker.model.Manufacturer;
import com.olivar.springbootmongodbdocker.service.ManufacturerService;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
class SpringbootMongodbDockerApplicationTests {

	@Autowired
	private ManufacturerService service;

	@MockBean
	private ManufacturerRepo repository;

	@Test
	public void getManufacturerTest() {
		when(repository.findAll()).thenReturn(Stream.of(new Manufacturer("Ferrari","F01")).collect(Collectors.toList()));

		assertEquals(1, service.getAllManufacturer().size());
	}

}
