package com.olivar.springbootmongodbdocker.infrastructure.inboud.rest.core.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManufacturerRequest {

    @JsonProperty(required = true)
    String name;

    @JsonProperty(required = true)
    String code;

}
