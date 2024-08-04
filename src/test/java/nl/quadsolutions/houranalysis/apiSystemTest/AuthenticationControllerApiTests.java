package nl.quadsolutions.houranalysis.apiSystemTest;

import nl.quadsolutions.houranalysis.config.security.JwtUtil;
import nl.quadsolutions.houranalysis.controller.dto.request.AuthenticationRequest;
import nl.quadsolutions.houranalysis.controller.dto.request.RefreshJWTRequest;
import nl.quadsolutions.houranalysis.controller.dto.response.AuthenticationResponse;
import nl.quadsolutions.houranalysis.controller.dto.response.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class AuthenticationControllerApiTests {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void createAccessTokenTest() {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest("admin", "admin");

        webTestClient.post()
                .uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(authenticationRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AuthenticationResponse.class)
                .value(response -> {
                    assertNotNull(response);
                    assertNotNull(response.getAccessToken());
                    assertNotNull(response.getRefreshToken());
                });
    }

    @Test
    void createAccessTokenInvalidCredentialsTest() {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest("invaliduser", "wrongpassword");

        webTestClient.post()
                .uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(authenticationRequest)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody(ErrorResponse.class)
                .value(response -> {
                    assertNotNull(response);
                    assertEquals("Incorrect username or password", response.getMessage());
                });
    }

    @Test
    void refreshAccessTokenTest() {
        String username = "admin";
        String refreshToken = jwtUtil.generateRefreshToken(username);

        RefreshJWTRequest refreshRequest = new RefreshJWTRequest(refreshToken);

        webTestClient.post()
                .uri("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(refreshRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AuthenticationResponse.class)
                .value(response -> {
                    assertNotNull(response);
                    assertNotNull(response.getAccessToken());
                    assertEquals(refreshToken, response.getRefreshToken());
                });
    }

    @Test
    void refreshAccessTokenInvalidRefreshTokenTest() {
        RefreshJWTRequest refreshRequest = new RefreshJWTRequest("invalidtoken");

        webTestClient.post()
                .uri("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(refreshRequest)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody(ErrorResponse.class)
                .value(response -> {
                    assertNotNull(response);
                    assertEquals("Refresh token is expired or invalid", response.getMessage());
                });
    }

    @Test
    void refreshAccessTokenInvalidUsernameTest() {
        String username = "invaliduser";
        String refreshToken = jwtUtil.generateRefreshToken(username);

        RefreshJWTRequest refreshRequest = new RefreshJWTRequest(refreshToken);

        webTestClient.post()
                .uri("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(refreshRequest)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody(ErrorResponse.class)
                .value(response -> {
                    assertNotNull(response);
                    assertEquals("Refresh token is expired or invalid", response.getMessage());
                });
    }
}
