package poly.edu.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

/**
 * AES-256-GCM encryption for TOTP secrets stored in the database.
 *
 * Format of the stored ciphertext (Base64):
 *   [ 12-byte IV ][ ciphertext + 16-byte GCM tag ]
 *
 * Thread-safe: no mutable state after construction.
 */
@Component
public class CryptoUtil {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LEN   = 12;  // 96-bit IV (NIST recommended)
    private static final int GCM_TAG_BITS  = 128; // 128-bit authentication tag

    private final SecretKeySpec secretKey;
    private final SecureRandom  secureRandom = new SecureRandom();

    public CryptoUtil(@Value("${mfa.encryption-key}") String keyHex) {
        byte[] keyBytes = HexFormat.of().parseHex(keyHex);
        if (keyBytes.length != 32) {
            throw new IllegalArgumentException("mfa.encryption-key must be exactly 64 hex chars (32 bytes)");
        }
        this.secretKey = new SecretKeySpec(keyBytes, "AES");
    }

    /** Encrypt plaintext using AES-256-GCM. Returns Base64(IV + ciphertext). */
    public String encrypt(String plaintext) {
        try {
            byte[] iv = new byte[GCM_IV_LEN];
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(GCM_TAG_BITS, iv));

            byte[] cipherBytes = cipher.doFinal(plaintext.getBytes());

            // Prepend IV
            byte[] combined = new byte[iv.length + cipherBytes.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(cipherBytes, 0, combined, iv.length, cipherBytes.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    /** Decrypt Base64(IV + ciphertext) back to plaintext. */
    public String decrypt(String encryptedBase64) {
        try {
            byte[] combined = Base64.getDecoder().decode(encryptedBase64);

            byte[] iv = new byte[GCM_IV_LEN];
            System.arraycopy(combined, 0, iv, 0, GCM_IV_LEN);

            byte[] cipherBytes = new byte[combined.length - GCM_IV_LEN];
            System.arraycopy(combined, GCM_IV_LEN, cipherBytes, 0, cipherBytes.length);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(GCM_TAG_BITS, iv));

            return new String(cipher.doFinal(cipherBytes));
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
        }
    }
}
