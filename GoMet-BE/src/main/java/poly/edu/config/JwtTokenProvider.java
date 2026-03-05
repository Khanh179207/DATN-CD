package poly.edu.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;

/**
 * Handles JWT signing and verification.
 *
 * Access token payload contains:
 *  - sub  : accountId (String)
 *  - email: account email
 *  - roles: comma-separated roles
 *  - iat / exp
 *
 * Tokens are signed with HS256 using a 512-bit key.
 */
@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey signingKey;
    private final long accessTokenTtlSeconds;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secretHex,
            @Value("${jwt.access-token-ttl-seconds:900}") long accessTokenTtlSeconds) {

        byte[] keyBytes = HexFormat.of().parseHex(secretHex);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenTtlSeconds = accessTokenTtlSeconds;
    }

    // ─── Issue ────────────────────────────────────────────────────────────────

    public String issueAccessToken(Integer accountId, String email, String roles) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(accountId))
                .id(UUID.randomUUID().toString())
                .claim("email", email)
                .claim("roles", roles)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(accessTokenTtlSeconds)))
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();
    }

    // ─── Validate + Parse ─────────────────────────────────────────────────────

    public Optional<Claims> validateAndParse(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Optional.of(claims);
        } catch (ExpiredJwtException e) {
            log.debug("JWT expired: {}", e.getMessage());
        } catch (JwtException e) {
            log.debug("JWT invalid: {}", e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<Integer> extractAccountId(String token) {
        return validateAndParse(token)
                .map(c -> Integer.parseInt(c.getSubject()));
    }

    public Optional<String> extractEmail(String token) {
        return validateAndParse(token)
                .map(c -> c.get("email", String.class));
    }

    public boolean isTokenExpired(String token) {
        return validateAndParse(token).isEmpty();
    }
}
