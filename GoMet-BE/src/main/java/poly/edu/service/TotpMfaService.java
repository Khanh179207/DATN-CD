package poly.edu.service;

import org.springframework.stereotype.Service;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.nio.ByteBuffer;

@Service
public class TotpMfaService {
    private static final String APP_NAME = "GoMet";

    public String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        return encodeBase32(bytes);
    }

    public String getQrCodeUrl(String email, String secret) {
        return String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s", APP_NAME, email, secret, APP_NAME);
    }

    public boolean verifyCode(String secret, String code) {
        if (secret == null || code == null || code.length() != 6) return false;
        try {
            long timeWindow = System.currentTimeMillis() / 30000;
            for (int i = -1; i <= 1; i++) { // Chấp nhận độ trễ 30s trước/sau do lệch đồng hồ
                String hash = generateTotp(secret, timeWindow + i);
                if (hash.equals(code)) return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    private String generateTotp(String secret, long time) throws Exception {
        byte[] decodedKey = decodeBase32(secret);
        byte[] data = ByteBuffer.allocate(8).putLong(time).array();
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(new SecretKeySpec(decodedKey, "HmacSHA1"));
        byte[] hash = mac.doFinal(data);
        int offset = hash[hash.length - 1] & 0xF;
        long truncatedHash = 0;
        for (int i = 0; i < 4; ++i) {
            truncatedHash <<= 8;
            truncatedHash |= (hash[offset + i] & 0xFF);
        }
        truncatedHash &= 0x7FFFFFFF;
        truncatedHash %= 1000000;
        return String.format("%06d", truncatedHash);
    }

    private String encodeBase32(byte[] data) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
        StringBuilder base32 = new StringBuilder();
        int i = 0, index = 0, digit = 0;
        int currByte, nextByte;
        while (i < data.length) {
            currByte = (data[i] >= 0) ? data[i] : (data[i] + 256);
            if (index > 3) {
                if ((i + 1) < data.length) nextByte = (data[i + 1] >= 0) ? data[i + 1] : (data[i + 1] + 256);
                else nextByte = 0;
                digit = currByte & (0xFF >> index);
                index = (index + 5) % 8;
                digit <<= index;
                digit |= nextByte >> (8 - index);
                i++;
            } else {
                digit = (currByte >> (8 - (index + 5))) & 0x1F;
                index = (index + 5) % 8;
                if (index == 0) i++;
            }
            base32.append(chars.charAt(digit));
        }
        return base32.toString();
    }

    private byte[] decodeBase32(String base32) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
        base32 = base32.toUpperCase().replaceAll("[=]", "");
        byte[] bytes = new byte[base32.length() * 5 / 8];
        int buffer = 0, bitsLeft = 0, count = 0;
        for (char c : base32.toCharArray()) {
            buffer <<= 5;
            buffer |= chars.indexOf(c);
            bitsLeft += 5;
            if (bitsLeft >= 8) {
                bytes[count++] = (byte) (buffer >> (bitsLeft - 8));
                bitsLeft -= 8;
            }
        }
        return bytes;
    }
}