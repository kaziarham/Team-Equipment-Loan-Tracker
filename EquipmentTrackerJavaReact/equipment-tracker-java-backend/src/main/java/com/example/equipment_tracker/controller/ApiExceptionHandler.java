package com.example.equipment_tracker.controller;

import com.example.equipment_tracker.service.EquipmentNotFoundException;
import com.example.equipment_tracker.service.InvalidLoanException;
import com.example.equipment_tracker.service.LoanConflictException;
import com.example.equipment_tracker.service.LoanNotFoundException;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    
    @ExceptionHandler(EquipmentNotFoundException.class)
    ProblemDetail handleEquipmentNotFound(EquipmentNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(LoanNotFoundException.class) 
    ProblemDetail handleLoanNotFound(LoanNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(LoanConflictException.class)
    ProblemDetail handleLoanConflict(LoanConflictException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(InvalidLoanException.class)
    ProblemDetail handleInvalidLoan(InvalidLoanException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                    error -> error.getField(),
                    error -> Objects.requireNonNullElse(error.getDefaultMessage(), "Invalid value"),
                    (first, second) -> first));
        problem.setProperty("errors", errors);
        return problem;
    }
}
