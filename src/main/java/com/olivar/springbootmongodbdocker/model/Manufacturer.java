package com.olivar.springbootmongodbdocker.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "manufacturers")
public class Manufacturer {
    @Id
    private int id;
    private String name;
    private String code;
}
