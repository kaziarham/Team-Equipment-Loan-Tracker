package com.example.equipment_tracker.dto;

import com.example.equipment_tracker.entity.Loan;
import java.time.LocalDate;

public enum LoanStatus {
    ACTIVE,
    OVERDUE,
    RETURNED;

    public static LoanStatus of(Loan loan, LocalDate today) {
        if (loan.getReturnedDate() != null) {
            return RETURNED;
        }

        return loan.getDueDate().isBefore(today) ? OVERDUE : ACTIVE;
    }
}
