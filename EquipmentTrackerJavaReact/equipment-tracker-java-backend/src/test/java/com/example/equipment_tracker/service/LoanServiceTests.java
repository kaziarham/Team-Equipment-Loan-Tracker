package com.example.equipment_tracker.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.equipment_tracker.dto.LoanRequest;
import com.example.equipment_tracker.dto.LoanResponse;
import com.example.equipment_tracker.dto.LoanStatus;
import com.example.equipment_tracker.entity.Equipment;
import com.example.equipment_tracker.entity.Loan;
import com.example.equipment_tracker.repository.EquipmentRepository;
import com.example.equipment_tracker.repository.LoanRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Unit tests for the LoanService class.
 */
class LoanServiceTests {
    
    private static final LocalDate TODAY = LocalDate.of(2026, 11, 24);
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-11-24T00:00:00Z"), ZoneOffset.UTC);

    private final LoanRepository loanRepository = mock(LoanRepository.class);
    private final EquipmentRepository equipmentRepository = mock(EquipmentRepository.class);
    private final LoanService service = new LoanService(loanRepository, equipmentRepository, CLOCK);

    @Test
    void borrowRecordsLoanStartingToday() {
        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(withId(new Equipment("Camera", "Camera", null), 1L)));
        when(loanRepository.saveAndFlush(any(Loan.class))).thenAnswer(invocation -> withId(invocation.getArgument(0), 10L));

        LoanResponse response = service.borrow(1L, new LoanRequest("  Ana Pereira  ", TODAY.plusDays(7)));

        assertThat(response.loanDate()).isEqualTo(TODAY);
        assertThat(response.borrowerName()).isEqualTo("Ana Pereira");
        assertThat(response.status()).isEqualTo(LoanStatus.ACTIVE);
    }

    @Test
    void borrowRejectsDueDateInThePast() {
        assertThatThrownBy(() -> service.borrow(1L, new LoanRequest("Ana", TODAY.minusDays(1))))
            .isInstanceOf(InvalidLoanException.class);
        verify(loanRepository, never()).saveAndFlush(any(Loan.class));
    }

    @Test
    void borrowRejectsEquipmentThatIsAlreadyOnLoan() {
        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(withId(new Equipment("Camera", "Camera", null), 1L)));
        when(loanRepository.existsByEquipment_IdAndReturnedDateIsNull(1L)).thenReturn(true);

        assertThatThrownBy(() -> service.borrow(1L, new LoanRequest("Ben Okafor", TODAY.plusDays(3))))
            .isInstanceOf(LoanConflictException.class);
        verify(loanRepository, never()).saveAndFlush(any(Loan.class));
    }

    @Test
    void borrowMapsActiveLoansConstraintViolationToConflict() {
        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(withId(new Equipment("Camera", "Camera", null), 1L)));
        when(loanRepository.saveAndFlush(any(Loan.class))).thenThrow(new DataIntegrityViolationException("insert failed", 
            new RuntimeException("duplicate key value violates unique constraint \"loan_one_active_per_equipment\"")));
        
        assertThatThrownBy(() -> service.borrow(1L, new LoanRequest("Ben Okafor", TODAY.plusDays(3))))
            .isInstanceOf(LoanConflictException.class);
    }

    @Test
    void returnMarksLoanReturnedToday() {
        Loan loan = withId(new Loan(withId(new Equipment("Camera", "Camera", null), 1L), "Ana", TODAY.minusDays(2), TODAY.plusDays(5)), 10L);
        when(loanRepository.findById(10L)).thenReturn(Optional.of(loan));

        LoanResponse response = service.returnLoan(10L);

        assertThat(response.returnedDate()).isEqualTo(TODAY);
        assertThat(response.status()).isEqualTo(LoanStatus.RETURNED);
    }

    @Test
    void returnRejectsLoanThatWasAlreadyReturned() {
        Loan loan = withId(new Loan(withId(new Equipment("Camera", "Camera", null), 1L), "Ana", TODAY.minusDays(5), TODAY.plusDays(1)), 10L);
        loan.markReturned(TODAY.minusDays(1));
        when(loanRepository.findById(10L)).thenReturn(Optional.of(loan));

        assertThatThrownBy(() -> service.returnLoan(10L))
            .isInstanceOf(LoanConflictException.class);
    }

    @Test
    void loanDueBeforeTodayIsOverdueButLoanDueTodayIsNot() {
        Equipment camera = new Equipment("Camera", "Camera", null);
        Loan overdue = new Loan(camera, "Ana", TODAY.minusDays(10), TODAY.minusDays(1));
        Loan dueToday = new Loan(camera, "Ben", TODAY.minusDays(3), TODAY);

        assertThat(LoanStatus.of(overdue, TODAY)).isEqualTo(LoanStatus.OVERDUE);
        assertThat(LoanStatus.of(dueToday, TODAY)).isEqualTo(LoanStatus.ACTIVE);
    }

    private static <T> T withId(T entity, Long id) {
        ReflectionTestUtils.setField(entity, "id", id);
        return entity;
    }
}
