package poly.edu.service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

/**
 * In-memory OTP store with expiry (10 minutes).
 * Stores both the OTP code and the pending registration data.
 */
@Component
public class OtpStore {

    private static final long TTL_SECONDS = 600; // 10 minutes

    public static record Entry(String otp, String username, String password, Instant expiresAt) {}

    private final Map<String, Entry> store = new ConcurrentHashMap<>();

    /** Save OTP for a given email together with username/password pending account creation. */
    public void save(String email, String otp, String username, String password) {
        store.put(email.toLowerCase(), new Entry(otp, username, password, Instant.now().plusSeconds(TTL_SECONDS)));
    }

    /** Verify the OTP code. Returns true if valid and not expired. */
    public boolean verify(String email, String otp) {
        Entry e = store.get(email.toLowerCase());
        if (e == null) return false;
        if (Instant.now().isAfter(e.expiresAt())) {
            store.remove(email.toLowerCase());
            return false;
        }
        return e.otp().equals(otp.trim());
    }

    /** Get the pending registration data (returns null if OTP not yet verified / expired). */
    public Entry getPending(String email) {
        return store.get(email.toLowerCase());
    }

    /** Remove entry after successful verification. */
    public void remove(String email) {
        store.remove(email.toLowerCase());
    }
}
