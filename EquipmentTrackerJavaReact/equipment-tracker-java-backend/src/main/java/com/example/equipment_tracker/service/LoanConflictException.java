package com.example.equipment_tracker.service;

public class LoanConflictException extends RuntimeException {
    
    public LoanConflictException(String message) {
        super(message);
    }
}
