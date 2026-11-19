package com.example.equipment_tracker.service;

import com.example.equipment_tracker.dto.EquipmentResponse;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Service;

@Service 
public class EquipmentService {
    
    private final List<EquipmentResponse> items = new CopyOnWriteArrayList<>(List.of(
        new EquipmentResponse(1L, "Macbook Pro 14", "Laptop", "C02XY1234"),
        new EquipmentResponse(2L, "Dell U2723QE", "Monitor", null),
        new EquipmentResponse(3L, "Sony WH-1000XM5", "Headset", "SN-0042")
    ));

    public List<EquipmentResponse> findAll() {
        return List.copyOf(items);
    }
}
