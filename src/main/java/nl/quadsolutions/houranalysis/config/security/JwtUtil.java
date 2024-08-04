package nl.quadsolutions.houranalysis.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
@Slf4j
public class JwtUtil {


    @Value("${jwt.expiration}")
    private Long accessExpiration;

    @Value("${jwt.refreshExpiration}")
    private Long refreshExpiration;

    @Value("${jwt.secret}")
    private String base64Secret;
    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(base64Secret));
    }

    // Generates an access JWT token
    public String generateAccessToken(String username) {
        log.debug("Generating access token");
        return generateToken(username, accessExpiration);
    }

    // Generates a refresh JWT token
    public String generateRefreshToken(String username) {
        log.debug("Generating refresh token");
        return generateToken(username, refreshExpiration);
    }


    // Generates an JWT token
    public String generateToken(String username, Long expirationTime) {
        log.debug("Generating token");
        return Jwts.builder()
                .subject(username)
                .issuer("http://localhost:8080")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey)
                .compact();
    }

    // Validates the JWT token
    public Boolean validateToken(String token, UserDetails userDetails) {
        log.debug("Validating token");
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // Retrieves username from JWT token
    public String getUsernameFromToken(String token) {
        log.debug("Retrieving username from token");
        return getClaimsFromToken(token).getSubject();
    }

    // Checks if the token has expired
    private Boolean isTokenExpired(String token) {
        log.debug("Checking if token is expired");
        return getClaimsFromToken(token).getExpiration().before(new Date());
    }

    // Retrieves claims from JWT token
    private Claims getClaimsFromToken(String token) {
        log.debug("Retrieving claims from token");
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
