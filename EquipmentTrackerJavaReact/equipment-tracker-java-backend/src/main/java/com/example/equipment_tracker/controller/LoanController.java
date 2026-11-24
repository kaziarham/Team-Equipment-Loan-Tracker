package com.example.equipment_tracker.controller;

import com.example.equipment_tracker.dto.LoanRequest;
import com.example.equipment_tracker.dto.LoanResponse;
import com.example.equipment_tracker.service.LoanService;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class LoanController {
    
    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping("/equipment/{equipmentId}/loans")
    public ResponseEntity<LoanResponse> borrow(@PathVariable long equipmentId, @Valid @RequestBody LoanRequest request) {
        LoanResponse created = loanService.borrow(equipmentId, request);
        return ResponseEntity.created(URI.create("/api/loans/" + created.id())).body(created);
    }
    
    @PostMapping("/loans/{id}/return")
    public LoanResponse returnLoan(@PathVariable long id) {
        return loanService.returnLoan(id);
    }
}
