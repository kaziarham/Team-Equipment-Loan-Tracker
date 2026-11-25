package com.example.equipment_tracker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.equipment_tracker.dto.EquipmentAvailability;
import com.example.equipment_tracker.dto.EquipmentResponse;
import com.example.equipment_tracker.dto.LoanRequest;
import com.example.equipment_tracker.dto.LoanResponse;
import com.example.equipment_tracker.dto.LoanStatus;
import com.example.equipment_tracker.entity.Equipment;
import com.example.equipment_tracker.entity.Loan;
import com.example.equipment_tracker.repository.EquipmentRepository;
import com.example.equipment_tracker.repository.LoanRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;

/**
 * Integration tests for the Loan API.
 * This is for loans against the PostgreSQL test database.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class LoanApiIntegrationTests {
    
    @LocalServerPort
    int port;

    @Autowired
    EquipmentRepository equipmentRepository;

    @Autowired
    LoanRepository loanRepository;

    private RestClient client;
    private long equipmentId;

    @BeforeEach
    void setUp() {
        // Safe only because the test profile points at equipment_tracker_test.
        loanRepository.deleteAll();
        equipmentRepository.deleteAll();
        equipmentId = equipmentRepository.save(new Equipment("Lenovo ThinkPad X1", "Laptop", null)).getId();
        client = RestClient.create("http://localhost:" + port);
    }

    @Test
    void borrowingRecordsLoanAndRejectsSecondActiveLoan() {
        LocalDate due = LocalDate.now().plusDays(7);

        var first = client.post().uri("/api/equipment/{id}/loans", equipmentId)
            .contentType(MediaType.APPLICATION_JSON)
            .body(new LoanRequest("Ana Pereira", due))
            .retrieve()
            .toEntity(LoanResponse.class);
        
        assertThat(first.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(first.getBody().status()).isEqualTo(LoanStatus.ACTIVE);

        HttpStatusCode second = client.post().uri("/api/equipment/{id}/loans", equipmentId)
            .contentType(MediaType.APPLICATION_JSON)
            .body(new LoanRequest("Ben Okafor", due))
            .exchange((request, response) -> response.getStatusCode());
        
        assertThat(second).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void returningLoanMakesEquipmentAvailableAgain() {
        LoanResponse loan = client.post().uri("/api/equipment/{id}/loans", equipmentId)
            .contentType(MediaType.APPLICATION_JSON)
            .body(new LoanRequest("Ana Pereira", LocalDate.now().plusDays(7)))
            .retrieve()
            .body(LoanResponse.class);
        
        LoanResponse returned = client.post().uri("/api/loans/{id}/return", loan.id())
            .retrieve()
            .body(LoanResponse.class);
        
        assertThat(returned.returnedDate()).isEqualTo(LocalDate.now());
        EquipmentResponse item = client.get().uri("/api/equipment/{id}", equipmentId)
            .retrieve()
            .body(EquipmentResponse.class);
        assertThat(item.availability()).isEqualTo(EquipmentAvailability.AVAILABLE);
    }

    @Test
    void returningUnknownLoanReturnsNotFound() {
        HttpStatusCode status = client.post().uri("/api/loans/{id}/return", 999_999L)
            .exchange((request, response) -> response.getStatusCode());

        assertThat(status).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void returningLoanTwiceReturnsConflict() {
        LoanResponse loan = client.post().uri("/api/equipment/{id}/loans", equipmentId)
            .contentType(MediaType.APPLICATION_JSON)
            .body(new LoanRequest("Ana Pereira", LocalDate.now().plusDays(7)))
            .retrieve()
            .body(LoanResponse.class);
        client.post().uri("/api/loans/{id}/return", loan.id()).retrieve().body(LoanResponse.class);

        HttpStatusCode second = client.post().uri("/api/loans/{id}/return", loan.id())
            .exchange((request, response) -> response.getStatusCode());

        assertThat(second).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void rejectsDueDateInThePast() {
        HttpStatusCode status = client.post().uri("/api/equipment/{id}/loans", equipmentId)
            .contentType(MediaType.APPLICATION_JSON)
            .body(new LoanRequest("Ana Pereira", LocalDate.now().minusDays(1)))
            .exchange((request, response) -> response.getStatusCode());
        
        assertThat(status).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void overdueFilterReturnsItemsWithPastDueLoans() {
        Equipment equipment = equipmentRepository.findById(equipmentId).orElseThrow();
        Loan overdueLoan = loanRepository.saveAndFlush(new Loan(equipment, "Ana Pereira", LocalDate.now().minusDays(10), LocalDate.now().minusDays(1)));

        List<EquipmentResponse> overdue = client.get().uri("/api/equipment?availability={availability}", "OVERDUE")
            .retrieve()
            .body(new ParameterizedTypeReference<List<EquipmentResponse>>() {});

        assertThat(overdue).hasSize(1);
        assertThat(overdue.get(0).availability()).isEqualTo(EquipmentAvailability.OVERDUE);
        assertThat(overdue.get(0).activeLoanId()).isEqualTo(overdueLoan.getId());
    }

    @Test
    void databaseRejectsTwoActiveLoansForSameEquipment() {
        Equipment equipment = equipmentRepository.findById(equipmentId).orElseThrow();
        loanRepository.saveAndFlush(new Loan(equipment, "Ana Pereira", LocalDate.now(), LocalDate.now().plusDays(7)));

        assertThatThrownBy(() -> loanRepository.saveAndFlush(new Loan(equipment, "Ben Okafor", LocalDate.now(), LocalDate.now().plusDays(7))))
            .isInstanceOf(DataIntegrityViolationException.class);
    }
}
