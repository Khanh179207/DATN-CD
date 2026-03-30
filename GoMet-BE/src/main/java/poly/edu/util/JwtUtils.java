package poly.edu.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    // Khóa bí mật để ký JWT (Đổi chuỗi này thành một chuỗi cực kỳ bảo mật và dài)
    // Thực tế người ta hay cấu hình trong application.properties, nhưng nay mình hardcode cho nhanh
    private final String jwtSecret = "GoMetSuperSecretKeyForJsonWebTokenWhichNeedsToBeVeryLongAndSecure2026";

    // Thời gian sống của Token: 7 ngày (Tính bằng milliseconds)
    private final int jwtExpirationMs = 7 * 24 * 60 * 60 * 1000;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // Hàm 1: Sinh ra Token khi User đăng nhập thành công
    public String generateJwtToken(String email, Integer accountId, String role) {
        return Jwts.builder()
                .setSubject(email) // Thông tin chính (thường dùng email hoặc username)
                .claim("accountId", accountId) // Gắn thêm ID vào token
                .claim("role", role) // Gắn thêm Quyền vào token
                .setIssuedAt(new Date()) // Thời gian phát hành
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs)) // Hạn sử dụng
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Ký tên đóng dấu
                .compact();
    }

    // Hàm 2: Lấy Email (Subject) từ Token do Frontend gửi lên
    public String getEmailFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    // Hàm 3: Kiểm tra xem Token có phải đồ thật không, hay bị hết hạn/chế cháo
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(authToken);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            System.err.println("Chữ ký JWT không hợp lệ: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            System.err.println("JWT đã hết hạn: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.err.println("JWT không được hỗ trợ: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("JWT claims bị trống: " + e.getMessage());
        }
        return false;
    }
}