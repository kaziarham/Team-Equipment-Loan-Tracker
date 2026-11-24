package com.example.equipment_tracker.controller;

import com.example.equipment_tracker.dto.EquipmentAvailability;
import com.example.equipment_tracker.dto.EquipmentRequest;
import com.example.equipment_tracker.dto.EquipmentResponse;
import com.example.equipment_tracker.service.EquipmentService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/equipment")
public class EquipmentController {
    
    private final EquipmentService equipmentService;

    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @GetMapping
    public List<EquipmentResponse> list(@RequestParam(required = false) EquipmentAvailability availability) {
        return equipmentService.findAll(availability);
    }

    @GetMapping("/{id}")
    public EquipmentResponse get(@PathVariable long id) {
        return equipmentService.findById(id);
    }
    
    @PostMapping
    public ResponseEntity<EquipmentResponse> create(@Valid @RequestBody EquipmentRequest request) {
        EquipmentResponse created = equipmentService.create(request);
        return ResponseEntity.created(URI.create("/api/equipment/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    public EquipmentResponse update(@PathVariable long id, @Valid @RequestBody EquipmentRequest request) {
        return equipmentService.update(id, request);
    }
}
