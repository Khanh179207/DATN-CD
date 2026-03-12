package poly.edu.config;

/**
 * BCryptPasswordEncoder bean has been moved to {@link SecurityConfig#passwordEncoder()}.
 * Cache configuration is handled separately.
 */
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class AppConfig {

    /**
     * Expose ObjectMapper as a Spring bean so it can be injected into services.
     * Spring Boot 4 no longer auto-exposes the auto-configured ObjectMapper.
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().registerModule(new JavaTimeModule());
    }
}

