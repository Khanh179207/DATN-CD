package poly.edu.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

/**
 * Phase 6 — Strong Password Policy (Feature 6 of the security spec).
 *
 * Rules:
 *  - Minimum 12 characters
 *  - At least 1 uppercase letter
 *  - At least 1 lowercase letter
 *  - At least 1 digit
 *  - At least 1 special character
 *  - Not in the common-passwords.txt list
 *
 * Throws {@link PasswordPolicyViolationException} with a descriptive message on failure.
 */
@Slf4j
@Service
public class PasswordPolicyService {

    private static final int MIN_LENGTH = 12;

    private final Set<String> commonPasswords = new HashSet<>();

    @PostConstruct
    void loadCommonPasswords() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                getClass().getResourceAsStream("/common-passwords.txt")))) {
            if (reader == null) {
                log.warn("common-passwords.txt not found — common password check disabled");
                return;
            }
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim().toLowerCase();
                if (!trimmed.isEmpty() && !trimmed.startsWith("#")) {
                    commonPasswords.add(trimmed);
                }
            }
            log.info("Loaded {} common passwords", commonPasswords.size());
        } catch (Exception e) {
            log.warn("Failed to load common-passwords.txt: {}", e.getMessage());
        }
    }

    // ─── Validation ───────────────────────────────────────────────────────────

    /**
     * Validates the password against the policy.
     * @throws PasswordPolicyViolationException if any rule is violated.
     */
    public void validate(String password) {
        if (password == null || password.length() < MIN_LENGTH) {
            throw new PasswordPolicyViolationException(
                "Password must be at least " + MIN_LENGTH + " characters long.");
        }
        if (!password.chars().anyMatch(Character::isUpperCase)) {
            throw new PasswordPolicyViolationException(
                "Password must contain at least one uppercase letter.");
        }
        if (!password.chars().anyMatch(Character::isLowerCase)) {
            throw new PasswordPolicyViolationException(
                "Password must contain at least one lowercase letter.");
        }
        if (!password.chars().anyMatch(Character::isDigit)) {
            throw new PasswordPolicyViolationException(
                "Password must contain at least one number.");
        }
        if (!password.chars().anyMatch(c -> !Character.isLetterOrDigit(c))) {
            throw new PasswordPolicyViolationException(
                "Password must contain at least one special character (e.g. @, #, !, $).");
        }
        if (commonPasswords.contains(password.toLowerCase())) {
            throw new PasswordPolicyViolationException(
                "This password is too common. Please choose a more unique password.");
        }
    }

    /** Returns true if valid, false otherwise (no exception). */
    public boolean isValid(String password) {
        try {
            validate(password);
            return true;
        } catch (PasswordPolicyViolationException e) {
            return false;
        }
    }

    // ─── Exception ────────────────────────────────────────────────────────────

    public static class PasswordPolicyViolationException extends RuntimeException {
        public PasswordPolicyViolationException(String message) {
            super(message);
        }
    }
}
