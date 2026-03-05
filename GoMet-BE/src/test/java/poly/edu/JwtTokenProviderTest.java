package poly.edu;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import poly.edu.config.JwtTokenProvider;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link JwtTokenProvider}: token issuance, validation, expiry, and claim extraction.
 */
class JwtTokenProviderTest {

    /** 512-bit (128 hex chars) test key — enough entropy for HS256. */
    private static final String TEST_SECRET =
            "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef" +
            "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef";

    private JwtTokenProvider provider;

    @BeforeEach
    void setUp() {
        // 900-second (15 min) TTL for normal tests
        provider = new JwtTokenProvider(TEST_SECRET, 900L);
    }

    // ─── Issue & Parse ────────────────────────────────────────────────────────

    @Test
    @DisplayName("issueAccessToken returns a non-blank JWT string")
    void issueAccessToken_returnsNonBlankJwt() {
        String token = provider.issueAccessToken(42, "user@example.com", "USER");
        assertThat(token).isNotBlank();
        assertThat(token.split("\\.")).hasSize(3); // header.payload.signature
    }

    @Test
    @DisplayName("validateAndParse returns claims for a fresh token")
    void validateAndParse_validToken_returnsClaims() {
        String token = provider.issueAccessToken(7, "alice@example.com", "USER");
        var claims = provider.validateAndParse(token);
        assertThat(claims).isPresent();
        assertThat(claims.get().getSubject()).isEqualTo("7");
        assertThat(claims.get().get("email", String.class)).isEqualTo("alice@example.com");
        assertThat(claims.get().get("roles", String.class)).isEqualTo("USER");
    }

    @Test
    @DisplayName("validateAndParse returns empty for a tampered token")
    void validateAndParse_tamperedToken_returnsEmpty() {
        String token = provider.issueAccessToken(1, "x@y.com", "USER");
        String tampered = token.substring(0, token.lastIndexOf('.') + 1) + "INVALIDSIG";
        assertThat(provider.validateAndParse(tampered)).isEmpty();
    }

    @Test
    @DisplayName("validateAndParse returns empty for a randomly malformed string")
    void validateAndParse_garbage_returnsEmpty() {
        assertThat(provider.validateAndParse("not-a-jwt-at-all")).isEmpty();
    }

    // ─── Expiry ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("isTokenExpired returns false for a fresh token")
    void isTokenExpired_freshToken_returnsFalse() {
        String token = provider.issueAccessToken(3, "b@c.com", "USER");
        assertThat(provider.isTokenExpired(token)).isFalse();
    }

    @Test
    @DisplayName("An expired token (TTL=0) is not accepted by validateAndParse")
    void expiredToken_notAccepted() throws InterruptedException {
        JwtTokenProvider shortLived = new JwtTokenProvider(TEST_SECRET, 0L);
        String token = shortLived.issueAccessToken(5, "x@y.com", "USER");
        // Sleep 1 second to ensure TTL has elapsed
        Thread.sleep(1_100);
        assertThat(shortLived.validateAndParse(token)).isEmpty();
        assertThat(shortLived.isTokenExpired(token)).isTrue();
    }

    // ─── Extractors ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("extractAccountId returns the correct id embedded in the token")
    void extractAccountId_returnsCorrectId() {
        String token = provider.issueAccessToken(99, "admin@example.com", "ADMIN");
        Optional<Integer> id = provider.extractAccountId(token);
        assertThat(id).hasValue(99);
    }

    @Test
    @DisplayName("extractEmail returns the correct email embedded in the token")
    void extractEmail_returnsCorrectEmail() {
        String token = provider.issueAccessToken(10, "test@gomet.vn", "USER");
        assertThat(provider.extractEmail(token)).hasValue("test@gomet.vn");
    }

    @Test
    @DisplayName("extractAccountId returns empty for an invalid token")
    void extractAccountId_invalidToken_returnsEmpty() {
        assertThat(provider.extractAccountId("garbage")).isEmpty();
    }

    // ─── Token uniqueness ─────────────────────────────────────────────────────

    @Test
    @DisplayName("Two tokens for the same user are not equal (different iat/jti)")
    void twoTokens_notEqual() throws InterruptedException {
        String t1 = provider.issueAccessToken(1, "a@b.com", "USER");
        Thread.sleep(10); // ensure different iat
        String t2 = provider.issueAccessToken(1, "a@b.com", "USER");
        assertThat(t1).isNotEqualTo(t2);
    }
}
