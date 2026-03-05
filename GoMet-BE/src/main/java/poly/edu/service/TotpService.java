package poly.edu.service;

import dev.samstevens.totp.code.*;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.util.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poly.edu.config.CryptoUtil;
import poly.edu.dao.AccountDAO;
import poly.edu.dao.BackupCodeDAO;
import poly.edu.dto.TotpSetupResponseDTO;
import poly.edu.entity.Account;
import poly.edu.entity.BackupCode;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Phase 4 — TOTP / 2FA implementation.
 *
 * - Uses dev.samstevens.totp library (Google Authenticator compatible, RFC 6238)
 * - TOTP secret is encrypted at rest with AES-256-GCM via CryptoUtil
 * - 10 single-use backup codes generated on enable, stored as SHA-256 hashes
 * - ±1 step window for clock drift tolerance
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TotpService {

    @Value("${mfa.issuer:GoMet}")
    private String issuer;

    private final CryptoUtil    cryptoUtil;
    private final AccountDAO    accountDAO;
    private final BackupCodeDAO backupCodeDAO;

    private static final int BACKUP_CODE_COUNT = 10;

    // ─── Setup ────────────────────────────────────────────────────────────────

    /**
     * Generates a new TOTP secret + QR code for the setup screen.
     * Does NOT persist the secret yet — call enableMfa() after the user verifies the code.
     */
    public TotpSetupResponseDTO setup(Account account) throws QrGenerationException {
        String secret = new DefaultSecretGenerator(32).generate();

        QrData qrData = new QrData.Builder()
                .label(account.getEmail())
                .secret(secret)
                .issuer(issuer)
                .algorithm(HashingAlgorithm.SHA1)
                .digits(6)
                .period(30)
                .build();

        ZxingPngQrGenerator qrGenerator = new ZxingPngQrGenerator();
        byte[] qrImageBytes = qrGenerator.generate(qrData);
        String qrBase64 = Utils.getDataUriForImage(qrImageBytes, qrGenerator.getImageMimeType());

        // Generate backup codes for display (not saved yet)
        List<String> backupCodes = generateRawBackupCodes();

        return TotpSetupResponseDTO.builder()
                .secret(secret)
                .otpAuthUri(qrData.getUri())
                .qrCodeBase64(qrBase64)
                .backupCodes(backupCodes)
                .build();
    }

    /**
     * Verifies the user's first TOTP code, then persists the encrypted secret and
     * backup codes.  Returns true on success.
     */
    @Transactional
    public boolean enableMfa(Account account, String plainSecret, String totpCode,
                              List<String> rawBackupCodes) {
        if (!verifyTotp(plainSecret, totpCode)) {
            return false;
        }

        String encrypted = cryptoUtil.encrypt(plainSecret);

        // Wipe any old backup codes first
        backupCodeDAO.deleteAllForUser(account.getAccountID());

        // Save new backup codes as hashes
        for (String raw : rawBackupCodes) {
            BackupCode bc = BackupCode.builder()
                    .userId(account.getAccountID())
                    .codeHash(sha256(raw))
                    .createdAt(Instant.now())
                    .build();
            backupCodeDAO.save(bc);
        }

        account.setMfaSecretEncrypted(encrypted);
        account.setMfaSecretCreatedAt(Instant.now());
        account.setMfaEnabled(1);
        accountDAO.save(account);

        log.info("MFA enabled for accountId={}", account.getAccountID());
        return true;
    }

    /**
     * Disables MFA and wipes all backup codes.
     */
    @Transactional
    public void disableMfa(Account account) {
        account.setMfaEnabled(0);
        account.setMfaSecretEncrypted(null);
        account.setMfaSecretCreatedAt(null);
        accountDAO.save(account);
        backupCodeDAO.deleteAllForUser(account.getAccountID());
        log.info("MFA disabled for accountId={}", account.getAccountID());
    }

    // ─── Verify ───────────────────────────────────────────────────────────────

    /**
     * Verifies a TOTP code for an account that has MFA enabled.
     * Uses ±1 step window to tolerate minor clock drift.
     */
    public boolean verifyForAccount(Account account, String totpCode) {
        if (!account.isMfaEnabled() || account.getMfaSecretEncrypted() == null) return false;
        String plainSecret = cryptoUtil.decrypt(account.getMfaSecretEncrypted());
        return verifyTotp(plainSecret, totpCode);
    }

    /**
     * Verifies a backup code for an account.
     * Marks the code as used (single-use).
     */
    @Transactional
    public boolean verifyBackupCode(Account account, String rawCode) {
        String hash = sha256(rawCode.toUpperCase().replaceAll("[^A-Z0-9]", ""));
        Optional<BackupCode> codeOpt = backupCodeDAO.findUnusedByUserAndHash(account.getAccountID(), hash);
        if (codeOpt.isEmpty()) return false;

        BackupCode code = codeOpt.get();
        code.setUsedAt(Instant.now());
        backupCodeDAO.save(code);
        log.info("Backup code used for accountId={}", account.getAccountID());
        return true;
    }

    /** How many backup codes remain unused. */
    public int countRemainingBackupCodes(Account account) {
        return backupCodeDAO.countUnusedByUser(account.getAccountID());
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private boolean verifyTotp(String secret, String code) {
        DefaultCodeGenerator codeGen = new DefaultCodeGenerator(HashingAlgorithm.SHA1, 6);
        DefaultCodeVerifier verifier = new DefaultCodeVerifier(codeGen, new SystemTimeProvider());
        verifier.setAllowedTimePeriodDiscrepancy(1); // ±1 window
        return verifier.isValidCode(secret, code);
    }

    private List<String> generateRawBackupCodes() {
        SecureRandom rng = new SecureRandom();
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // unambiguous chars
        List<String> codes = new ArrayList<>(BACKUP_CODE_COUNT);
        for (int i = 0; i < BACKUP_CODE_COUNT; i++) {
            StringBuilder sb = new StringBuilder(8);
            for (int j = 0; j < 8; j++) {
                sb.append(chars.charAt(rng.nextInt(chars.length())));
            }
            codes.add(sb.toString());
        }
        return codes;
    }

    private static String sha256(String input) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            return java.util.HexFormat.of()
                    .formatHex(md.digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 unavailable", e);
        }
    }
}
