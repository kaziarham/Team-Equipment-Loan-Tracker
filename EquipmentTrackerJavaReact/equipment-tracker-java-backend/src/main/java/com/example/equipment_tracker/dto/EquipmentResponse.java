package com.example.equipment_tracker.dto;

import java.time.LocalDate;

public record EquipmentResponse(
    long id,
    String name,
    String category,
    String serialNumber,
    EquipmentAvailability availability,
    Long activeLoanId,
    String activeBorrowerName,
    LocalDate activeDueDate
) {
    
}
