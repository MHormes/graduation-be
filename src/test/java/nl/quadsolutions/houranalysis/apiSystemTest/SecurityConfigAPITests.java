package nl.quadsolutions.houranalysis.apiSystemTest;

import nl.quadsolutions.houranalysis.config.security.JwtUtil;
import nl.quadsolutions.houranalysis.controller.dto.request.RefreshJWTRequest;
import nl.quadsolutions.houranalysis.controller.dto.response.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("short")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class SecurityConfigAPITests {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void refreshAccessTokenExpiredRefreshTokenTest() {
        String refreshToken = jwtUtil.generateRefreshToken("admin");
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

    @Test
    void refreshAccessTokenInvalidUsernameAndExpiredRefreshTokenTest() {
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

    @Test
    void requestWithoutAuthHeaderTest() {
        webTestClient.get()
                .uri("/api/hours")
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void requestNoBearerPrefixInAuthHeaderTest() {
        webTestClient.get()
                .uri("/api/hours")
                .header(HttpHeaders.AUTHORIZATION, "tokenNotBearer")
                .exchange()
                .expectStatus().isForbidden();
    }
}
