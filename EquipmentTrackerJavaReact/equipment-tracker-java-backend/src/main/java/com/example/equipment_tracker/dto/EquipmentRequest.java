package com.example.equipment_tracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EquipmentRequest(
    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must be 255 characters or fewer")
    String name,

    @NotBlank(message = "Category is required")
    @Size(max = 100, message = "Category must be 100 characters or fewer")
    String category,

    @Size(max = 255, message = "Serial number must be 255 characters or fewer")
    String serialNumber
) {
    
}
