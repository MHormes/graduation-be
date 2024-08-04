package nl.quadsolutions.houranalysis.controller;


import jakarta.security.auth.message.AuthException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.quadsolutions.houranalysis.config.security.JwtUtil;
import nl.quadsolutions.houranalysis.controller.dto.request.AuthenticationRequest;
import nl.quadsolutions.houranalysis.controller.dto.request.RefreshJWTRequest;
import nl.quadsolutions.houranalysis.controller.dto.response.AuthenticationResponse;
import nl.quadsolutions.houranalysis.service.CustomUserDetailService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
@Slf4j
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;

    private final CustomUserDetailService userDetailsService;

    private final JwtUtil jwtUtil;

    /**
     * Login a user on the API
     * @param authenticationRequest The username and password of the user
     * @return An authenticationResponse, containing the access and refresh token
     */
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        log.info("Logging in user");
        try {
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
            );

            final UserDetails userDetails = (UserDetails) authenticate.getPrincipal();
            final String accessToken = jwtUtil.generateAccessToken(userDetails.getUsername());
            final String refreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername());

            return ResponseEntity.ok(new AuthenticationResponse(accessToken, refreshToken));
        } catch (AuthenticationException ex) {
            throw new AuthException("Incorrect username or password", ex);
        }
    }

    /**
     * Refresh the access token of a user
     * @param refreshRequest The refresh token of the user
     * @return An authenticationResponse, containing the new access and refresh token
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refreshAuthenticationToken(@RequestBody RefreshJWTRequest refreshRequest) throws Exception {
        log.info("Refreshing token");
        try {
            String refreshToken = refreshRequest.getRefreshToken();
            String username = jwtUtil.getUsernameFromToken(refreshToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            // Check if the refresh token is valid
            if (Boolean.FALSE.equals(jwtUtil.validateToken(refreshToken, userDetails))) {
                throw new AuthException("Invalid refresh token");
            }

            final String newAccessToken = jwtUtil.generateAccessToken(username);
            return ResponseEntity.ok(new AuthenticationResponse(newAccessToken, refreshToken));
        } catch (Exception e) {
            throw new AuthException("Refresh token is expired or invalid", e);
        }
    }



}
