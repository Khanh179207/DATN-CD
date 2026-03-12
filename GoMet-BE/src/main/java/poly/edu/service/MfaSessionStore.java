package poly.edu.service;

import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Short-lived in-memory store for pending MFA challenge sessions.
 *
 * When a user with MFA enabled submits their password, we:
 *  1. Issue a mfaSessionToken (UUID)
 *  2. Store: userId + expiry in this map
 *  3. Return requiresMfa=true + the token to the frontend
 *
 * The frontend then POSTs the TOTP code + mfaSessionToken to /auth/mfa/verify.
 *
 * TTL: 5 minutes.  No persistence needed — loss on restart just means the user
 * re-enters their password.
 */
@Component
public class MfaSessionStore {

    private static final long TTL_SECONDS = 300; // 5 minutes
    private static final int MAX_ATTEMPTS = 5;

    private final Map<String, PendingMfa> store = new ConcurrentHashMap<>();

    public String create(Integer accountId) {
        String token = UUID.randomUUID().toString();
        store.put(token, new PendingMfa(accountId, Instant.now().plusSeconds(TTL_SECONDS), MAX_ATTEMPTS));
        return token;
    }

    /** Returns accountId only when token exists and is not expired; does not consume token. */
    public Optional<Integer> getAccountIdIfValid(String mfaSessionToken) {
        PendingMfa pending = store.get(mfaSessionToken);
        if (pending == null || pending.expiresAt().isBefore(Instant.now())) {
            store.remove(mfaSessionToken);
            return Optional.empty();
        }
        return Optional.of(pending.accountId());
    }

    /** Consumes token only after MFA succeeded. */
    public Optional<Integer> consumeOnSuccess(String mfaSessionToken) {
        PendingMfa pending = store.remove(mfaSessionToken);
        if (pending == null || pending.expiresAt().isBefore(Instant.now())) {
            return Optional.empty();
        }
        return Optional.of(pending.accountId());
    }

    /** Registers a failed challenge. Returns remaining attempts; 0 means expired/locked. */
    public int registerFailure(String mfaSessionToken) {
        PendingMfa pending = store.get(mfaSessionToken);
        if (pending == null || pending.expiresAt().isBefore(Instant.now())) {
            store.remove(mfaSessionToken);
            return 0;
        }

        int nextRemaining = pending.remainingAttempts() - 1;
        if (nextRemaining <= 0) {
            store.remove(mfaSessionToken);
            return 0;
        }

        store.put(mfaSessionToken,
                new PendingMfa(pending.accountId(), pending.expiresAt(), nextRemaining));
        return nextRemaining;
    }

    /** Periodically remove expired sessions so the in-memory map cannot grow unbounded. */
    @Scheduled(fixedDelayString = "${security.mfa-session-cleanup-ms:60000}")
    public void cleanupExpiredSessions() {
        Instant now = Instant.now();
        store.entrySet().removeIf(entry -> entry.getValue().expiresAt().isBefore(now));
    }

    /** Exposed for diagnostics/monitoring in dev environments. */
    public int activeSessionCount() {
        return store.size();
    }

    public record PendingMfa(Integer accountId, Instant expiresAt, int remainingAttempts) {}
}
