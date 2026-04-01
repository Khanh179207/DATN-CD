package poly.edu.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // 🔥 Thêm lại cái này
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    // 🔥 PHẢI CÓ DÒNG NÀY THÌ MỚI HẾT LỖI "cannot find symbol"
    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;

    // 🔥 Cần giữ lại Bean này để các chỗ Login/Register không bị lỗi
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configure(http))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Bây giờ biến jwtAuthFilter đã tồn tại, sẽ không còn lỗi nữa
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // ✅ Vùng mở hoàn toàn
                        .requestMatchers("/api/auth/**", "/uploads/**", "/ws/**", "/ws-chat/**", "/api/payments/**").permitAll()

                        // ✅ Cho phép qua hết ở đây, nhưng sẽ "xử" bằng @PreAuthorize ở Controller
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}