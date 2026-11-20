package com.example.equipment_tracker;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.equipment_tracker.dto.EquipmentRequest;
import com.example.equipment_tracker.dto.EquipmentResponse;
import com.example.equipment_tracker.entity.Equipment;
import com.example.equipment_tracker.repository.EquipmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class EquipmentApiIntegrationTests {
    
    @LocalServerPort
    int port;

    @Autowired
    EquipmentRepository equipmentRepository;

    private RestClient client;

    @BeforeEach
    void setUp() {
        // Safe only because the test profile points at equipment_tracker_test.
        equipmentRepository.deleteAll();
        client = RestClient.create("http://localhost:" + port);
    }

    @Test
    void createsEquipmentAndPersistsIt() {
        var response = client.post().uri("/api/equipment")
            .contentType(MediaType.APPLICATION_JSON)
            .body(new EquipmentRequest("Canon EOS R6", "Camera", "CAM-001"))
            .retrieve()
            .toEntity(EquipmentResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo("Canon EOS R6");
        assertThat(equipmentRepository.count()).isEqualTo(1);
    }

    @Test
    void rejectsInvalidInputWithBadRequest() {
        HttpStatusCode status = client.post().uri("/api/equipment")
            .contentType(MediaType.APPLICATION_JSON)
            .body(new EquipmentRequest(" ", "", null))
            .exchange((request, response) -> response.getStatusCode());
        
        assertThat(status).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(equipmentRepository.count()).isZero();
    }

    @Test
    void updatesExistingEquipment() {
        Equipment saved = equipmentRepository.save(new Equipment("Dell U2723QE", "Monitor", null));

        var response = client.put().uri("/api/equipment/{id}", saved.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .body(new EquipmentRequest("Dell U2723QE 27in", "Monitor", "MON-7"))
            .retrieve()
            .toEntity(EquipmentResponse.class);

        assertThat(response.getBody().serialNumber()).isEqualTo("MON-7");
        assertThat(equipmentRepository.findById(saved.getId()).orElseThrow().getName())
            .isEqualTo("Dell U2723QE 27in");
    }

    @Test
    void returnsNotFoundForMissingEquipment() {
        HttpStatusCode status = client.get().uri("/api/equipment/{id}", 999_999L)
            .exchange((request, response) -> response.getStatusCode());
        
        assertThat(status).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
