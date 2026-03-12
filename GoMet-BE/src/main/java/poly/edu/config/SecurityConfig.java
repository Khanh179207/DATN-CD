package poly.edu.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Production-grade Spring Security configuration.
 *
 * Strategy:
 *  - Stateless JWT (no HTTP session)
 *  - CSRF disabled (JWT + strict CORS is the protection layer)
 *  - All `/api/auth/**` endpoints are public
 *  - All other `/api/**` endpoints require a valid JWT (or legacy UUID token via JwtAuthFilter)
 *  - Security headers: HSTS, CSP, X-Frame-Options, X-Content-Type-Options, Referrer-Policy
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final MaintenanceModeFilter maintenanceModeFilter;

    @Value("${app.allowed-origins:http://localhost:5173,http://localhost:5174,http://127.0.0.1:5173}")
    private String allowedOriginsRaw;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter, MaintenanceModeFilter maintenanceModeFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.maintenanceModeFilter = maintenanceModeFilter;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // ─── Session ───────────────────────────────────────────────────────
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // ─── CSRF: disabled for stateless JWT API ─────────────────────────
            .csrf(AbstractHttpConfigurer::disable)

            // ─── CORS: configured below ────────────────────────────────────────
            .cors(c -> c.configurationSource(corsConfigurationSource()))

            // ─── Authorization Rules ───────────────────────────────────────────
            .authorizeHttpRequests(auth -> auth
                // Public auth endpoints
                .requestMatchers(HttpMethod.POST,
                    "/api/auth/login",
                    "/api/auth/register",
                    "/api/auth/send-otp",
                    "/api/auth/verify-otp",
                    "/api/auth/forgot-password",
                    "/api/auth/reset-password",
                    "/api/auth/google",
                    "/api/auth/refresh",
                    "/api/auth/verify-login",
                    "/api/auth/mfa/verify"
                ).permitAll()
                .requestMatchers(HttpMethod.GET,
                    "/api/auth/verify-login",
                    "/api/auth/me",
                    "/api/system/settings/public"
                ).permitAll()
                .requestMatchers("/api/admin/**", "/admin/**").hasRole("ADMIN")
                // Public read-only content
                .requestMatchers(HttpMethod.GET,
                    "/api/posts/**",
                    "/api/categories/**",
                    "/api/events/**",
                    "/api/media/**",
                    "/api/search/**",
                    "/api/leaderboard/**",
                    "/api/share/**",
                    "/share/**"
                ).permitAll()
                // Everything else requires authentication
                .anyRequest().authenticated()
            )

            // ─── Exception Handling ────────────────────────────────────────────
            // Return 401 (not 403) for unauthenticated requests so the FE
            // token-refresh interceptor can handle expired tokens correctly.
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, authEx) -> {
                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    res.setContentType("application/json");
                    res.getWriter().write("{\"message\":\"Authentication required\"}");
                })
                .accessDeniedHandler((req, res, accessEx) -> {
                    res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    res.setContentType("application/json");
                    res.getWriter().write("{\"message\":\"Access denied\"}");
                })
            )

            // ─── Security Headers ──────────────────────────────────────────────
            .headers(headers -> headers
                // HSTS: enforce HTTPS for 1 year, include subdomains
                .httpStrictTransportSecurity(hsts -> hsts
                    .includeSubDomains(true)
                    .maxAgeInSeconds(31_536_000)
                    .preload(true)
                )
                // Basic CSP — tighten per environment
                .contentSecurityPolicy(csp -> csp
                    .policyDirectives(
                        "default-src 'self'; " +
                        "script-src 'self' https://accounts.google.com; " +
                        "style-src 'self' 'unsafe-inline'; " +
                        "img-src 'self' data: https:; " +
                        "connect-src 'self'; " +
                        "frame-ancestors 'none';"
                    )
                )
                // Prevent clickjacking
                .frameOptions(frame -> frame.deny())
                // Block MIME-type sniffing
                .contentTypeOptions(c -> {})
                // Referrer policy
                .referrerPolicy(r -> r
                    .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
                )
                // Permissions-Policy
                .permissionsPolicy(pp -> pp
                    .policy("camera=(), microphone=(), geolocation=(), payment=()")
                )
            )

            // ─── JWT filter ────────────────────────────────────────────────────
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterAfter(maintenanceModeFilter, JwtAuthFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        List<String> origins = Arrays.asList(allowedOriginsRaw.split(","));

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(origins);
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowCredentials(true);
        config.setExposedHeaders(List.of("Content-Disposition", "X-Total-Count"));
        config.setMaxAge(3600L); // preflight cache

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
