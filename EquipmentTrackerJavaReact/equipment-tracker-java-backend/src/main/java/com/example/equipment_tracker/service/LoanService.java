package com.example.equipment_tracker.service;

import com.example.equipment_tracker.dto.LoanRequest;
import com.example.equipment_tracker.dto.LoanResponse;
import com.example.equipment_tracker.dto.LoanStatus;
import com.example.equipment_tracker.entity.Equipment;
import com.example.equipment_tracker.entity.Loan;
import com.example.equipment_tracker.repository.EquipmentRepository;
import com.example.equipment_tracker.repository.LoanRepository;
import java.time.Clock;
import java.time.LocalDate;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoanService {
    
    private static final String ACTIVE_LOAN_CONSTRAINT = "loan_one_active_per_equipment";

    private final LoanRepository loanRepository;
    private final EquipmentRepository equipmentRepository;
    private final Clock clock;

    public LoanService(LoanRepository loanRepository, EquipmentRepository equipmentRepository, Clock clock) {
        this.loanRepository = loanRepository;
        this.equipmentRepository = equipmentRepository;
        this.clock = clock;
    }

    @Transactional
    public LoanResponse borrow(long equipmentId, LoanRequest request) {
        LocalDate today = LocalDate.now(clock);
        if (request.dueDate().isBefore(today)) {
            throw new InvalidLoanException("Due date cannot be in the past");
        }

        Equipment equipment = equipmentRepository.findById(equipmentId)
            .orElseThrow(() -> new EquipmentNotFoundException(equipmentId));
        if (loanRepository.existsByEquipment_IdAndReturnedDateIsNull(equipmentId)) {
            throw new LoanConflictException("Equipment " + equipmentId + " is already on loan");
        }

        try {
            Loan loan = loanRepository.saveAndFlush(new Loan(equipment, request.borrowerName().trim(), today, request.dueDate()));
            return toResponse(loan, today);
        } catch (DataIntegrityViolationException ex) {
            // Backstop for concurrent requests that pass the check above at the same time.
            if (violatesActiveLoanRule(ex)) {
                throw new LoanConflictException("Equipment " + equipmentId + " is already on loan");
            }
            throw ex;
        }
    }

    @Transactional
    public LoanResponse returnLoan(long loanId) {
        Loan loan = loanRepository.findById(loanId).orElseThrow(() -> new LoanNotFoundException(loanId));
        if (loan.getReturnedDate() != null) {
            throw new LoanConflictException("Loan " + loanId + " has already been returned");
        }

        LocalDate today = LocalDate.now(clock);
        loan.markReturned(today);
        return toResponse(loan, today);
    }

    private static boolean violatesActiveLoanRule(DataIntegrityViolationException ex) {
        for (Throwable cause = ex; cause != null; cause = cause.getCause()) {
            String message = cause.getMessage();

            if (message != null && message.contains(ACTIVE_LOAN_CONSTRAINT)) {
                return true;
            }
        }
        
        return false;
    }

    private static LoanResponse toResponse(Loan loan, LocalDate today) {
        return new LoanResponse(
            loan.getId(),
            loan.getEquipment().getId(),
            loan.getBorrowerName(),
            loan.getLoanDate(),
            loan.getDueDate(),
            loan.getReturnedDate(),
            LoanStatus.of(loan, today)
        );
    }
}
