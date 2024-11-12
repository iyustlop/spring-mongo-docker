package com.olivar.springbootmongodbdocker.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "manufacturer")
public class Manufacturer {
    private String name;
    private String code;
}
