package com.example.equipment_tracker.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.equipment_tracker.dto.EquipmentRequest;
import com.example.equipment_tracker.dto.EquipmentResponse;
import com.example.equipment_tracker.entity.Equipment;
import com.example.equipment_tracker.repository.EquipmentRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Unit tests for the EquipmentService class.
 */
class EquipmentServiceTests {
    
    @Test
    void createTrimsFieldsAndStoresBlankSerialNumberAsNull() {
        EquipmentRepository equipmentRepository = mock(EquipmentRepository.class);
        when(equipmentRepository.save(any(Equipment.class))).thenAnswer(invocation -> withId(invocation.getArgument(0), 1L));
        EquipmentService equipmentService = new EquipmentService(equipmentRepository);

        EquipmentResponse response = equipmentService.create(new EquipmentRequest("  Canon EOS R6  ", "  Camera  ", "  "));

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
        EquipmentService equipmentService = new EquipmentService(equipmentRepository);

        EquipmentResponse response = equipmentService.update(1L, new EquipmentRequest("  Dell U2723QE 27in  ", "Monitor", "  MON-7  "));

        assertThat(existing.getName()).isEqualTo("Dell U2723QE 27in");
        assertThat(response.serialNumber()).isEqualTo("MON-7");
    }

    @Test
    void updateThrowsWhenEquipmentDoesNotExist() {
        EquipmentRepository equipmentRepository = mock(EquipmentRepository.class);
        when(equipmentRepository.findById(42L)).thenReturn(Optional.empty());
        EquipmentService equipmentService = new EquipmentService(equipmentRepository);

        assertThatThrownBy(() -> equipmentService.update(42L, new EquipmentRequest("Name", "Category", null)))
            .isInstanceOf(EquipmentNotFoundException.class);
    }

    @Test
    void findAllMapsEntitiesToResponses() {
        EquipmentRepository equipmentRepository = mock(EquipmentRepository.class);
        when(equipmentRepository.findAllByOrderByIdAsc()).thenReturn(List.of(withId(new Equipment("Laptop", "Computer", "LAP-1"), 1L)));
        EquipmentService equipmentService = new EquipmentService(equipmentRepository);

        List<EquipmentResponse> responses = equipmentService.findAll();

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).name()).isEqualTo("Laptop");
        assertThat(responses.get(0).serialNumber()).isEqualTo("LAP-1");
    }

    private static Equipment withId(Equipment equipment, long id) {
        ReflectionTestUtils.setField(equipment, "id", id);
        return equipment;
    }
}
