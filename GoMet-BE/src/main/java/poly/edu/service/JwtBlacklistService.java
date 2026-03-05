package poly.edu.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtBlacklistService {

    private static final String PREFIX = "jwt:blacklist:";

    private final StringRedisTemplate redisTemplate;

    public void blacklist(String jti, Duration ttl) {
        if (jti == null || jti.isBlank() || ttl == null || ttl.isNegative() || ttl.isZero()) {
            return;
        }
        try {
            redisTemplate.opsForValue().set(PREFIX + jti, "1", ttl);
        } catch (Exception ex) {
            log.warn("Redis blacklist write failed: {}", ex.getMessage());
        }
    }

    public boolean isBlacklisted(String jti) {
        if (jti == null || jti.isBlank()) {
            return false;
        }
        try {
            Boolean exists = redisTemplate.hasKey(PREFIX + jti);
            return Boolean.TRUE.equals(exists);
        } catch (Exception ex) {
            log.warn("Redis blacklist read failed: {}", ex.getMessage());
            return false;
        }
    }
}
