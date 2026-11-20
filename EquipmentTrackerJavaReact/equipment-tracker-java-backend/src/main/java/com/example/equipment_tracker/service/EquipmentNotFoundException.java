package com.example.equipment_tracker.service;

public class EquipmentNotFoundException extends RuntimeException {
    
    public EquipmentNotFoundException(long id) {
        super("Equipment " + id + " was not found");
    }
}
