package poly.edu.entity;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

public class OtpStore {

    public static class OtpData {
        public String otp;
        public LocalDateTime expiredAt;

        public OtpData(String otp, LocalDateTime expiredAt) {
            this.otp = otp;
            this.expiredAt = expiredAt;
        }
    }

    private static final ConcurrentHashMap<String, OtpData> OTP_MAP = new ConcurrentHashMap<>();

    public static void save(String email, String otp) {
        OTP_MAP.put(email, new OtpData(
                otp,
                LocalDateTime.now().plusMinutes(5)
        ));
    }

    public static OtpData get(String email) {
        return OTP_MAP.get(email);
    }

    public static void remove(String email) {
        OTP_MAP.remove(email);
    }
}