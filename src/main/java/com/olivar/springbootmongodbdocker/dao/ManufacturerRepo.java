package com.olivar.springbootmongodbdocker.dao;

import com.olivar.springbootmongodbdocker.model.Manufacturer;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ManufacturerRepo extends MongoRepository<Manufacturer,String> {
    Manufacturer findByCode(String code);
}
