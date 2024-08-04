package nl.quadsolutions.houranalysis.apiSystemTest;

import nl.quadsolutions.houranalysis.controller.dto.mappers.HourEntryMapper;
import nl.quadsolutions.houranalysis.controller.dto.request.AuthenticationRequest;
import nl.quadsolutions.houranalysis.controller.dto.response.AuthenticationResponse;
import nl.quadsolutions.houranalysis.model.Employee;
import nl.quadsolutions.houranalysis.model.HourEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HourEntryControllerAPITests {

    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient webTestClient;

    private String obtainAccessToken() {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest("admin", "admin");

        return Objects.requireNonNull(webTestClient.post()
                        .uri("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(authenticationRequest)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody(AuthenticationResponse.class)
                        .returnResult()
                        .getResponseBody())
                .getAccessToken();
    }

    @BeforeEach
    void setUp() {
        String authToken = obtainAccessToken();
        webTestClient = webTestClient.mutate()
                .baseUrl("http://localhost:" + port + "/api")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
                .build();
    }

    @Test
    void mapperReturnsNullOnEmptyTest() {
        assertNull(HourEntryMapper.mapToHourEntryResponse(null));
    }

    @Test
    void mapperReturnsResponseOnValidTest() {
        Employee employee = new Employee(124578954, "John Doe", new ArrayList<>());
        HourEntry entry = new HourEntry(UUID.randomUUID(), employee, "BLD - John Doe", "Projecturen", LocalDate.of(2023, 1, 16), 8, "", "time", LocalDate.of(2023, 1, 16), LocalDate.of(2023, 1, 16), LocalDate.of(2023, 1, 16));
        assertNotNull(HourEntryMapper.mapToHourEntryResponse(entry));
    }
}
