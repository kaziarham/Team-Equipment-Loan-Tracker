package com.example.equipment_tracker.service;

import com.example.equipment_tracker.dto.EquipmentRequest;
import com.example.equipment_tracker.dto.EquipmentResponse;
import com.example.equipment_tracker.entity.Equipment;
import com.example.equipment_tracker.repository.EquipmentRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service 
public class EquipmentService {
    
    private final EquipmentRepository equipmentRepository;

    public EquipmentService(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }

    public List<EquipmentResponse> findAll() {
        return equipmentRepository.findAllByOrderByIdAsc().stream()
            .map(EquipmentService::toResponse)
            .toList();
    }

    public EquipmentResponse findById(long id) {
        return toResponse(getOrThrow(id));
    }

    @Transactional
    public EquipmentResponse create(EquipmentRequest request) {
        Equipment equipment = new Equipment(request.name().trim(), request.category().trim(), blankToNull(request.serialNumber()));
        return toResponse(equipmentRepository.save(equipment));
    }

    @Transactional
    public EquipmentResponse update(long id, EquipmentRequest request) {
        Equipment equipment = getOrThrow(id);
        equipment.update(request.name().trim(), request.category().trim(), blankToNull(request.serialNumber()));
        return toResponse(equipment);
    }

    private Equipment getOrThrow(long id) {
        return equipmentRepository.findById(id).orElseThrow(() -> new EquipmentNotFoundException(id));
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private static EquipmentResponse toResponse(Equipment equipment) {
        return new EquipmentResponse(equipment.getId(), equipment.getName(), equipment.getCategory(), equipment.getSerialNumber());
    }
}
