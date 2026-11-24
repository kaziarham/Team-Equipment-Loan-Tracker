package com.example.equipment_tracker.repository;

import com.example.equipment_tracker.entity.Loan;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    
    boolean existsByEquipment_IdAndReturnedDateIsNull(long equipmentId);

    Optional<Loan> findByEquipment_IdAndReturnedDateIsNull(long equipmentId);

    List<Loan> findAllByReturnedDateIsNull();
}
