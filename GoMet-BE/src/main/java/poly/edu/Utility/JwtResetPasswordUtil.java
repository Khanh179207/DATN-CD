package poly.edu.Utility;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtResetPasswordUtil {

    private final Key key = Keys.hmacShaKeyFor(
            "RESET_PASSWORD_SECRET_KEY_12345678901234567890".getBytes()
    );

    private final long EXPIRATION = 10 * 60 * 1000; // 10 phút

    // Tạo token reset
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .claim("type", "RESET_PASSWORD")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Lấy email từ token
    public String getEmail(String token) {
        return parseToken(token).getBody().getSubject();
    }

    // Validate token
    public void validateToken(String token) {
        Claims claims = parseToken(token).getBody();

        if (!"RESET_PASSWORD".equals(claims.get("type"))) {
            throw new RuntimeException("Token không hợp lệ");
        }
    }

    private Jws<Claims> parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }

}
