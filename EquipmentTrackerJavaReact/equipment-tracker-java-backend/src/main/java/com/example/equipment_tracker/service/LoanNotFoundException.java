package com.example.equipment_tracker.service;

public class LoanNotFoundException extends RuntimeException {
    
    public LoanNotFoundException(long id) {
        super("Loan " + id + " was not found");
    }
}
