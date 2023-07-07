package com.olivar.springbootmongodbdocker.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.olivar.springbootmongodbdocker.dao.ManufacturerRepo;
import com.olivar.springbootmongodbdocker.model.Manufacturer;

@Service
public class ManufacturerService {
    
    @Autowired
    private ManufacturerRepo repository;

    public Manufacturer saveManufacturer(Manufacturer manufacturer){
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
