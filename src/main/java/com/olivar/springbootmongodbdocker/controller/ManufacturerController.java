package com.olivar.springbootmongodbdocker.controller;

import com.olivar.springbootmongodbdocker.dao.ManufacturerRepo;
import com.olivar.springbootmongodbdocker.model.Manufacturer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/manufacturer")
public class ManufacturerController {

    @Autowired
    private ManufacturerRepo repository;

    @PostMapping
    public Manufacturer saveManucfacturer(@RequestBody Manufacturer manufacturer){
        return repository.save(manufacturer);
    }

    @GetMapping
    public List<Manufacturer> getManufacturer(){
        return repository.findAll();
    }
}
