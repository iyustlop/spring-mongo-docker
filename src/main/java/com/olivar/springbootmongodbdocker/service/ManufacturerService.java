package com.olivar.springbootmongodbdocker.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.olivar.springbootmongodbdocker.dao.ManufacturerRepo;
import com.olivar.springbootmongodbdocker.domain.model.Manufacturer;

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
}
