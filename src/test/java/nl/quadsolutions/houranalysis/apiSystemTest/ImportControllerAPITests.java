package nl.quadsolutions.houranalysis.apiSystemTest;

import nl.quadsolutions.houranalysis.controller.dto.request.AuthenticationRequest;
import nl.quadsolutions.houranalysis.controller.dto.response.AuthenticationResponse;
import nl.quadsolutions.houranalysis.controller.dto.response.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ImportControllerAPITests {

    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient webTestClient;


    @Autowired
    private JdbcTemplate jdbcTemplate;

    //helper function for tests
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

    public String convertToCSV(String[] data) {
        return String.join(",", data);
    }

    @BeforeEach
    void setUp() {
        String authToken = obtainAccessToken();
        webTestClient = webTestClient.mutate()
                .baseUrl("http://localhost:" + port + "/api")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
                .build();

        // Clear the database
        jdbcTemplate.execute("DELETE FROM HOUR_ENTRY");
    }


    @Test
    void importDataTest() {
        List<String[]> data = new ArrayList<>();
        data.add(new String[]{"Medewerker", "pers_num", "project_naam", "activiteit_naam", "datum", "uur", "opmerkingen", "soortuur", "bijgewerkt_tot", "acc_datum", "projectmanager", "Goedgekeurd door", "Goedgekeurd op"});

        data.add(new String[]{"Andries Evers", "2207035", "DICTU", "Projecturen", "15-Dec-23", "8.00", "", "time", "15-Dec-23", "15-Dec-23", "", "Finance Quad", "15-Dec-23"});
        data.add(new String[]{"Alessandro Pedano", "2109018", "BLD - Alessandro Pedano", "Vakantie", "17-Dec-23", "8.00", "", "time", "17-Dec-23", "17-Dec-23", "", "Finance Quad", "17-Dec-23"});
        data.add(new String[]{"Bart van Mierlo", "2110024", "BLD - Bart van Mierlo", "Projecturen", "15-Dec-23", "8.00", "", "time", "15-Dec-23", "15-Dec-23", "", "Finance Quad", "15-Dec-23"});

        File csvOutputFile = new File("test.csv");
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            data.stream().map(this::convertToCSV).forEach(pw::println);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        byte[] content;
        try {
            content = Files.readAllBytes(csvOutputFile.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        MockMultipartFile file = new MockMultipartFile("test.csv", "test.csv", "text/csv", content);


        webTestClient.post()
                .uri("/import")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData("file", file.getResource()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(response -> assertEquals("CSV file imported", response));
    }

    @Test
    void importDataEmptyFileTest() {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file.csv", "empty.csv", "text/csv", new byte[0]
        );

        webTestClient.post()
                .uri("/import")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData("file", emptyFile.getResource()))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(ErrorResponse.class)
                .value(response -> {
                    assertNotNull(response);
                    assertEquals("File is empty", response.getMessage());
                });
    }

    @Test
    void importDataNonCsvFileTest() {
        MockMultipartFile nonCsvFile = new MockMultipartFile(
                "file", "test.txt", "text/plain", "This is a text file.".getBytes()
        );

        webTestClient.post()
                .uri("/import")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData("file", nonCsvFile.getResource()))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(ErrorResponse.class)
                .value(response -> {
                    assertNotNull(response);
                    assertEquals("File type not supported", response.getMessage());

                });
    }

    @Test
    void importDataDatesNullTest() {
        List<String[]> data = new ArrayList<>();
        data.add(new String[]{"Medewerker", "pers_num", "project_naam", "activiteit_naam", "datum", "uur", "opmerkingen", "soortuur", "bijgewerkt_tot", "acc_datum", "projectmanager", "Goedgekeurd door", "Goedgekeurd op"});

        data.add(new String[]{"Andries Evers", "2207035", "DICTU", "Projecturen", null, "8.00", "", "time", " ", "", "", "Finance Quad", "15-Dec-23"});

        File csvOutputFile = new File("test.csv");
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            data.stream().map(this::convertToCSV).forEach(pw::println);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        byte[] content;
        try {
            content = Files.readAllBytes(csvOutputFile.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        MockMultipartFile file = new MockMultipartFile("test.csv", "test.csv", "text/csv", content);


        webTestClient.post()
                .uri("/import")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData("file", file.getResource()))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(ErrorResponse.class)
                .value(response -> {
                    assertNotNull(response);
                    assertEquals("Handler dispatch failed: java.lang.AssertionError: Unable to parse date: null", response.getMessage());
                });
    }
}
