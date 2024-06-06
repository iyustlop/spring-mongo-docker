package com.olivar.springbootmongodbdocker.infrastructure.inboud.rest.core;

import com.olivar.springbootmongodbdocker.domain.model.Manufacturer;
import com.olivar.springbootmongodbdocker.infrastructure.inboud.rest.core.dto.ManufacturerResponse;
import com.olivar.springbootmongodbdocker.infrastructure.inboud.rest.core.mapper.ManufacturerResponseMapper;
import com.olivar.springbootmongodbdocker.service.ManufacturerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/manufacturer")
public class ManufacturerController {

    @Autowired
    private ManufacturerService service;
    @Autowired
    private ManufacturerResponseMapper manufacturerResponseMapper;

    @PostMapping
    public ResponseEntity<ManufacturerResponse> saveManucfacturer(@RequestBody Manufacturer manufacturer){
        return ResponseEntity.ok(
            manufacturerResponseMapper.manufacturerToManufacturerResponse(
                service.saveManufacturer(manufacturer)
            )
        );
    }

    @GetMapping
    public ResponseEntity<List<Manufacturer>> getManufacturer(){
        return ResponseEntity.ok(service.getAllManufacturer());
    }
}
