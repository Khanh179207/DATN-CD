package poly.edu.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/** Response when starting TOTP setup (/auth/mfa/setup). */
@Data
@Builder
public class TotpSetupResponseDTO {
    /** Base32 secret — client must store this or scan the QR code. */
    private String secret;
    /** otpauth:// URI for QR code rendering. */
    private String otpAuthUri;
    /** Base64 PNG QR code image (data URI safe). */
    private String qrCodeBase64;
    /** 10 single-use backup codes. Display once then discard. */
    private List<String> backupCodes;
}
