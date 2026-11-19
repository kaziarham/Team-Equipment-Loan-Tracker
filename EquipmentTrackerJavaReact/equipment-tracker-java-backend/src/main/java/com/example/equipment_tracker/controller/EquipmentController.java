package com.example.equipment_tracker.controller;

import com.example.equipment_tracker.dto.EquipmentResponse;
import com.example.equipment_tracker.service.EquipmentService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/equipment")
public class EquipmentController {
    
    private final EquipmentService equipmentService;

    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @GetMapping
    public List<EquipmentResponse> list() {
        return equipmentService.findAll();
    }
}
