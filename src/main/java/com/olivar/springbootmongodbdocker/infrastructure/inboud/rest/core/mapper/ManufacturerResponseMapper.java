package com.olivar.springbootmongodbdocker.infrastructure.inboud.rest.core.mapper;

import com.olivar.springbootmongodbdocker.domain.model.Manufacturer;
import com.olivar.springbootmongodbdocker.infrastructure.inboud.rest.core.dto.ManufacturerResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface ManufacturerResponseMapper {
    //@Mapping(target = "", source = "")
    ManufacturerResponse manufacturerToManufacturerResponse(Manufacturer manufacturer);
}
