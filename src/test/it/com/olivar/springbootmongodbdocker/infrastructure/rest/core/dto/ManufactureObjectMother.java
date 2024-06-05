package com.olivar.springbootmongodbdocker.infrastructure.rest.core.dto;

import com.olivar.springbootmongodbdocker.infrastructure.inboud.rest.core.dto.ManufacturerRequest;
import net.datafaker.Faker;

public class ManufactureObjectMother {
    public static ManufacturerRequest randomManufacturerRequest(){
        Faker faker = new Faker();
        return new ManufacturerRequest(
                faker.numerify("Na##"),
                faker.numerify("Co##"));
    }
}
