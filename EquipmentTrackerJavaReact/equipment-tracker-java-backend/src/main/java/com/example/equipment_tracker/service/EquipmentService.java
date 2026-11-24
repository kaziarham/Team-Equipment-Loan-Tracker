package com.example.equipment_tracker.service;

import com.example.equipment_tracker.dto.EquipmentAvailability;
import com.example.equipment_tracker.dto.EquipmentRequest;
import com.example.equipment_tracker.dto.EquipmentResponse;
import com.example.equipment_tracker.dto.LoanStatus;
import com.example.equipment_tracker.entity.Equipment;
import com.example.equipment_tracker.entity.Loan;
import com.example.equipment_tracker.repository.EquipmentRepository;
import com.example.equipment_tracker.repository.LoanRepository;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service 
public class EquipmentService {
    
    private final EquipmentRepository equipmentRepository;
    private final LoanRepository loanRepository;
    private final Clock clock;

    public EquipmentService(EquipmentRepository equipmentRepository, LoanRepository loanRepository, Clock clock) {
        this.equipmentRepository = equipmentRepository;
        this.loanRepository = loanRepository;
        this.clock = clock;
    }

    public List<EquipmentResponse> findAll(EquipmentAvailability availability) {
        LocalDate today = LocalDate.now(clock);
        Map<Long, Loan> activeLoans = loanRepository.findAllByReturnedDateIsNull().stream()
            .collect(Collectors.toMap(loan -> loan.getEquipment().getId(), loan -> loan));

        return equipmentRepository.findAllByOrderByIdAsc().stream()
            .map(equipment -> toResponse(equipment, activeLoans.get(equipment.getId()), today))
            .filter(response -> availability == null || response.availability() == availability)
            .toList();
    }

    public EquipmentResponse findById(long id) {
        return toResponse(getOrThrow(id), activeLoanFor(id), LocalDate.now(clock));
    }

    @Transactional
    public EquipmentResponse create(EquipmentRequest request) {
        Equipment equipment = new Equipment(request.name().trim(), request.category().trim(), blankToNull(request.serialNumber()));
        return toResponse(equipmentRepository.save(equipment), null, LocalDate.now(clock));
    }

    @Transactional
    public EquipmentResponse update(long id, EquipmentRequest request) {
        Equipment equipment = getOrThrow(id);
        equipment.update(request.name().trim(), request.category().trim(), blankToNull(request.serialNumber()));
        return toResponse(equipment, activeLoanFor(id), LocalDate.now(clock));
    }

    private Equipment getOrThrow(long id) {
        return equipmentRepository.findById(id).orElseThrow(() -> new EquipmentNotFoundException(id));
    }

    private Loan activeLoanFor(long equipmentId) {
        return loanRepository.findByEquipment_IdAndReturnedDateIsNull(equipmentId).orElse(null);
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private static EquipmentAvailability availabilityOf(Loan activeLoan, LocalDate today) {
        if (activeLoan == null) {
            return EquipmentAvailability.AVAILABLE;
        }

        return LoanStatus.of(activeLoan, today) == LoanStatus.OVERDUE
            ? EquipmentAvailability.OVERDUE
            : EquipmentAvailability.ON_LOAN;
    }

    private static EquipmentResponse toResponse(Equipment equipment, Loan activeLoan, LocalDate today) {
        return new EquipmentResponse(
            equipment.getId(), 
            equipment.getName(), 
            equipment.getCategory(), 
            equipment.getSerialNumber(), 
            availabilityOf(activeLoan, today), 
            activeLoan == null ? null : activeLoan.getId(),
            activeLoan == null ? null : activeLoan.getBorrowerName(),
            activeLoan == null ? null : activeLoan.getDueDate()
        );
    }
}
