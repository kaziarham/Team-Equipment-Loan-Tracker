package com.example.equipment_tracker.repository;

import com.example.equipment_tracker.entity.Equipment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    
    List<Equipment> findAllByOrderByIdAsc();
}
