package com.example.equipment_tracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record LoanRequest(
    @NotBlank(message = "Borrower name is required")
    @Size(max = 255, message = "Borrower name must be 255 characters or fewer")
    String borrowerName,

    @NotNull(message = "Due date is required")
    LocalDate dueDate
) {

}
