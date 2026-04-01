package poly.edu.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value; // 🔥 IMPORT CÁI NÀY
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    // 🔥 HÚT CHUỖI BÍ MẬT TỪ APPLICATION.PROPERTIES
    @Value("${jwt.secret}")
    private String jwtSecret;

    // 🔥 HÚT THỜI GIAN SỐNG TỪ APPLICATION.PROPERTIES
    @Value("${jwt.expirationMs}")
    private int jwtExpirationMs;

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