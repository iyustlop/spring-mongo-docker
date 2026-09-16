package com.olivar.springbootmongodbdocker.service;

import com.olivar.springbootmongodbdocker.dao.ManufacturerRepo;
import com.olivar.springbootmongodbdocker.domain.model.Manufacturer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ManufacturerService {
    
    @Autowired
    private ManufacturerRepo repository;

    public Manufacturer saveManufacturer(Manufacturer manufacturer){
        log.info(manufacturer.getName());
        return repository.save(manufacturer);
    }

    public List<Manufacturer> getAllManufacturer(){
        return repository.findAll();
    }

    public Manufacturer getManufacturer(String code){
        return repository.findByCode(code);
    }

    public Manufacturer updateManufacturer(String id, Manufacturer manufacturer) {
        Manufacturer manufacturer1 = repository.findById(id).get();
        manufacturer1.setCode(manufacturer.getCode());
        manufacturer1.setName(manufacturer.getName());
        return repository.save(manufacturer1);
    }
}
