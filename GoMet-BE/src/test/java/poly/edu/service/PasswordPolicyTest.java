package poly.edu.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link PasswordPolicyService}.
 *
 * Rules under test (all must pass simultaneously):
 *  1. Minimum 12 characters
 *  2. At least one uppercase letter
 *  3. At least one lowercase letter
 *  4. At least one digit
 *  5. At least one special character
 *  6. Not in the common-passwords list
 *
 * Same package as {@code PasswordPolicyService} so we can call the package-private
 * {@code loadCommonPasswords()} method directly.
 */
class PasswordPolicyTest {

    private PasswordPolicyService policy;

    @BeforeEach
    void setUp() {
        policy = new PasswordPolicyService();
        // Manually trigger @PostConstruct to load common-passwords.txt from classpath
        policy.loadCommonPasswords();
    }

    // ─── Null / Too-Short ─────────────────────────────────────────────────────

    @Test
    @DisplayName("null password throws violation")
    void nullPassword_throws() {
        assertThatThrownBy(() -> policy.validate(null))
                .isInstanceOf(PasswordPolicyService.PasswordPolicyViolationException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "Short1!", "Abc1@xyz"}) // all < 12 chars
    @DisplayName("password shorter than 12 characters throws violation")
    void tooShortPassword_throws(String pwd) {
        assertThatThrownBy(() -> policy.validate(pwd))
                .isInstanceOf(PasswordPolicyService.PasswordPolicyViolationException.class)
                .hasMessageContaining("12");
    }

    // ─── Missing Character Classes ────────────────────────────────────────────

    @Test
    @DisplayName("password missing uppercase throws violation")
    void missingUppercase_throws() {
        assertThatThrownBy(() -> policy.validate("alllowercase1!"))
                .isInstanceOf(PasswordPolicyService.PasswordPolicyViolationException.class)
                .hasMessageMatching("(?i).*uppercase.*");
    }

    @Test
    @DisplayName("password missing lowercase throws violation")
    void missingLowercase_throws() {
        assertThatThrownBy(() -> policy.validate("ALLUPPERCASE1!"))
                .isInstanceOf(PasswordPolicyService.PasswordPolicyViolationException.class)
                .hasMessageMatching("(?i).*lowercase.*");
    }

    @Test
    @DisplayName("password missing digit throws violation")
    void missingDigit_throws() {
        assertThatThrownBy(() -> policy.validate("NoDigitsHere!@"))
                .isInstanceOf(PasswordPolicyService.PasswordPolicyViolationException.class)
                .hasMessageMatching("(?i).*number.*");
    }

    @Test
    @DisplayName("password missing special character throws violation")
    void missingSpecial_throws() {
        assertThatThrownBy(() -> policy.validate("OnlyAlphaNum1234"))
                .isInstanceOf(PasswordPolicyService.PasswordPolicyViolationException.class)
                .hasMessageMatching("(?i).*special.*");
    }

    // ─── Valid Passwords ──────────────────────────────────────────────────────

    @ParameterizedTest
    @ValueSource(strings = {
        "GoMet@Secure1!",
        "C0mplex_P@ss!2024",
        "Tr0ub4dor&3Extra",
        "V@lid-P4ssw0rd!!X"
    })
    @DisplayName("strong passwords pass all five rules")
    void strongPassword_passes(String pwd) {
        assertThatNoException().isThrownBy(() -> policy.validate(pwd));
        assertThat(policy.isValid(pwd)).isTrue();
    }

    // ─── isValid wrapper ──────────────────────────────────────────────────────

    @Test
    @DisplayName("isValid returns false for a weak password — no exception propagated")
    void isValid_weakPassword_returnsFalse() {
        assertThat(policy.isValid("weak")).isFalse();
    }

    @Test
    @DisplayName("isValid returns true for a strong password")
    void isValid_strongPassword_returnsTrue() {
        assertThat(policy.isValid("GoMet@Secure1!")).isTrue();
    }

    // ─── Boundary ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("exactly 12 character strong password is accepted")
    void exactly12Chars_passes() {
        // 12 chars: 2 upper, 2 lower, 2 digit, 2 special + fill
        assertThatNoException().isThrownBy(() -> policy.validate("GoMe@1!Xyzab"));
    }

    @Test
    @DisplayName("11 character strong password is rejected")
    void exactly11Chars_rejected() {
        assertThatThrownBy(() -> policy.validate("GoMe@1!Xyz!"))
                .isInstanceOf(PasswordPolicyService.PasswordPolicyViolationException.class);
    }
}
