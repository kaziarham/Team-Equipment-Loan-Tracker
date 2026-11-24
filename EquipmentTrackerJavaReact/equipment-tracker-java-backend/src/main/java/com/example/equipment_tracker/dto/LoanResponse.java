package com.example.equipment_tracker.dto;

import java.time.LocalDate;

public record LoanResponse(
    long id,
    long equipmentId,
    String borrowerName,
    LocalDate loanDate,
    LocalDate dueDate,
    LocalDate returnedDate,
    LoanStatus status
) {
    
}
