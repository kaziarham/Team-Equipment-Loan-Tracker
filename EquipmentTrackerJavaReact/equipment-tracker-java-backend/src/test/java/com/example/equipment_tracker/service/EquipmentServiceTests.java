package com.example.equipment_tracker.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.equipment_tracker.dto.EquipmentAvailability;
import com.example.equipment_tracker.dto.EquipmentRequest;
import com.example.equipment_tracker.dto.EquipmentResponse;
import com.example.equipment_tracker.entity.Equipment;
import com.example.equipment_tracker.entity.Loan;
import com.example.equipment_tracker.repository.EquipmentRepository;
import com.example.equipment_tracker.repository.LoanRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Unit tests for the EquipmentService class.
 */
class EquipmentServiceTests {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-11-24T10:00:00Z"), ZoneOffset.UTC);
    
    @Test
    void createTrimsFieldsAndStoresBlankSerialNumberAsNull() {
        EquipmentRepository equipmentRepository = mock(EquipmentRepository.class);
        when(equipmentRepository.save(any(Equipment.class))).thenAnswer(invocation -> withId(invocation.getArgument(0), 1L));
        EquipmentService service = new EquipmentService(equipmentRepository, mock(LoanRepository.class), CLOCK);

        EquipmentResponse response = service.create(new EquipmentRequest("  Canon EOS R6  ", "  Camera  ", "  "));

        ArgumentCaptor<Equipment> captor = ArgumentCaptor.forClass(Equipment.class);
        verify(equipmentRepository).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("Canon EOS R6");
        assertThat(captor.getValue().getCategory()).isEqualTo("Camera");
        assertThat(captor.getValue().getSerialNumber()).isNull();
        assertThat(response.serialNumber()).isNull();
    }

    @Test
    void updateChangesExistingEquipment() {
        EquipmentRepository equipmentRepository = mock(EquipmentRepository.class);
        Equipment existing = withId(new Equipment("Dell U2723QE", "Monitor", null), 1L);
        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(existing));
        EquipmentService service = new EquipmentService(equipmentRepository, mock(LoanRepository.class), CLOCK);

        EquipmentResponse response = service.update(1L, new EquipmentRequest("  Dell U2723QE 27in  ", "Monitor", "  MON-7  "));

        assertThat(existing.getName()).isEqualTo("Dell U2723QE 27in");
        assertThat(response.serialNumber()).isEqualTo("MON-7");
    }

    @Test
    void updateThrowsWhenEquipmentDoesNotExist() {
        EquipmentRepository equipmentRepository = mock(EquipmentRepository.class);
        when(equipmentRepository.findById(42L)).thenReturn(Optional.empty());
        EquipmentService service = new EquipmentService(equipmentRepository, mock(LoanRepository.class), CLOCK);

        assertThatThrownBy(() -> service.update(42L, new EquipmentRequest("Name", "Category", null)))
            .isInstanceOf(EquipmentNotFoundException.class);
    }

    @Test
    void findAllMapsEntitiesToResponses() {
        EquipmentRepository equipmentRepository = mock(EquipmentRepository.class);
        when(equipmentRepository.findAllByOrderByIdAsc()).thenReturn(List.of(withId(new Equipment("Laptop", "Computer", "LAP-1"), 1L)));
        EquipmentService service = new EquipmentService(equipmentRepository, mock(LoanRepository.class), CLOCK);

        List<EquipmentResponse> responses = service.findAll(null);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).name()).isEqualTo("Laptop");
        assertThat(responses.get(0).serialNumber()).isEqualTo("LAP-1");
    }

    @Test
    void findAllFiltersByAvailability() {
        EquipmentRepository equipmentRepository = mock(EquipmentRepository.class);
        LoanRepository loanRepository = mock(LoanRepository.class);
        Equipment camera = withId(new Equipment("Camera", "Camera", null), 1L);
        Equipment laptop = withId(new Equipment("Laptop", "Computer", null), 2L);
        when(equipmentRepository.findAllByOrderByIdAsc()).thenReturn(List.of(camera, laptop));
        Loan overdueLoan = new Loan(camera, "Ana Pereira", LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 20));
        when(loanRepository.findAllByReturnedDateIsNull()).thenReturn(List.of(overdueLoan));
        EquipmentService service = new EquipmentService(equipmentRepository, loanRepository, CLOCK);

        List<EquipmentResponse> overdue = service.findAll(EquipmentAvailability.OVERDUE);
        List<EquipmentResponse> available = service.findAll(EquipmentAvailability.AVAILABLE);

        assertThat(overdue).hasSize(1);
        assertThat(overdue.get(0).name()).isEqualTo("Camera");
        assertThat(available).hasSize(1);
        assertThat(available.get(0).name()).isEqualTo("Laptop");
    }

    private static <T> T withId(T entity, long id) {
        ReflectionTestUtils.setField(entity, "id", id);
        return entity;
    }
}
