package com.example.equipment_tracker.service;

public class InvalidLoanException extends RuntimeException {
    
    public InvalidLoanException(String message) {
        super(message);
    }
}
