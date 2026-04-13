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

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Tắt CSRF để dùng JWT mượt hơn
                .cors(cors -> cors.configure(http))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                .authorizeHttpRequests(auth -> auth
                        // Cho phép các yêu cầu Pre-flight (OPTIONS) đi qua hết
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // ✅ DANH SÁCH CỬA TỰ DO HOÀN TOÀN (Ai cũng được gọi mọi method GET/POST...)
                        .requestMatchers(
                                "/api/auth/**",
                                "/uploads/**",
                                "/ws/**",
                                "/ws-chat/**",
                                "/api/payments/**",
                                "/api/ai/**",
                                "/api/appeals/**" // Cho phép gửi khiếu nại công khai
                        ).permitAll()

                        // ✅ CỬA ĐỌC CHO GUEST (Chỉ cho phép xem - HTTP GET)
                        // Guest có thể lướt xem bài, xem sự kiện, xem bình luận mà không cần đăng nhập
                        .requestMatchers(HttpMethod.GET,
                                "/api/posts/**",
                                "/api/events/**",
                                "/api/categories/**",
                                "/api/comments/**",
                                "/api/system-config/**",
                                "/api/users/**"// Cấp quyền đọc cấu hình banner/giá tiền
                        ).permitAll()

                        // 🔒 PHÂN QUYỀN ĐẶC BIỆT
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // 🔒 TẤT CẢ CÁC REQUEST CÒN LẠI: Phải đăng nhập (Có Token hợp lệ)
                        // Ví dụ: POST /api/posts (Đăng bài), PUT /api/users (Sửa profile) sẽ rơi vào đây
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}