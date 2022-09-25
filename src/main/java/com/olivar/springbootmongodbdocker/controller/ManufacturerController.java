package com.olivar.springbootmongodbdocker.controller;

import com.olivar.springbootmongodbdocker.model.Manufacturer;
import com.olivar.springbootmongodbdocker.service.ManufacturerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/manufacturer")
public class ManufacturerController {

    @Autowired
    private ManufacturerService service;

    @PostMapping
    public Manufacturer saveManucfacturer(@RequestBody Manufacturer manufacturer){
        return service.saveManufacturer(manufacturer);
    }

    @GetMapping
    public List<Manufacturer> getManufacturer(){
        return service.getAllManufacturer();
    }
}
