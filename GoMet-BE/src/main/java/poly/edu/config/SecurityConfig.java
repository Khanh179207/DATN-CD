package poly.edu.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    // 🔥 THÊM CÁI NÀY: Cấu hình CORS "Mở toang" cho Frontend gọi thoải mái không bị block
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*")); // Cho phép mọi domain (Vue, React...)
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "x-auth-token"));
        configuration.setExposedHeaders(Arrays.asList("x-auth-token"));
        configuration.setAllowCredentials(true); // Quan trọng nếu gửi kèm Token/Cookie
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Tắt CSRF để dùng JWT mượt hơn
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // 🔥 Áp dụng cấu hình CORS ở trên
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                .authorizeHttpRequests(auth -> auth
                        // Cho phép các yêu cầu Pre-flight (OPTIONS) đi qua hết
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // ✅ DANH SÁCH CỬA TỰ DO HOÀN TOÀN
                        .requestMatchers(
                                "/api/auth/**",
                                "/uploads/**",
                                "/ws/**",
                                "/ws-chat/**",
                                "/api/payments/**",
                                "/api/ai/**",
                                "/api/appeals/**"
                        ).permitAll()

                        // ✅ CỬA ĐỌC CHO GUEST (Chỉ cho phép xem - HTTP GET)
                        .requestMatchers(HttpMethod.GET,
                                "/api/posts/**",
                                "/api/events/**",
                                "/api/categories/**",
                                "/api/comments/**",
                                "/api/likes/**", // 🔥 ĐÃ THÊM: Mở cửa cho API xem lượt tim
                                "/api/system-config/**",
                                "/api/users/**"
                        ).permitAll()

                        // 🔒 PHÂN QUYỀN ĐẶC BIỆT
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // 🔒 TẤT CẢ CÁC REQUEST CÒN LẠI: Phải đăng nhập (Có Token hợp lệ)
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}