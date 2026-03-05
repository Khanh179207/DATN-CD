package poly.edu.config;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import poly.edu.dao.AccountDAO;
import poly.edu.entity.Account;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Validates the JWT access token on every request.
 * Sets Spring Security's Authentication context so downstream code can use
 * {@code SecurityContextHolder.getContext().getAuthentication()}.
 *
 * Also supports the legacy UUID bearer token for backward compatibility
 * (used by older frontend builds and non-upgraded endpoints).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final AccountDAO        accountDAO;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String bearerToken = extractBearerToken(request);
        if (bearerToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // ── Try JWT first ────────────────────────────────────────────────────
        Optional<Claims> claims = jwtTokenProvider.validateAndParse(bearerToken);
        if (claims.isPresent()) {
            authenticateFromJwt(claims.get(), request);
            filterChain.doFilter(request, response);
            return;
        }

        // ── Fall back to legacy UUID token ───────────────────────────────────
        authenticateFromLegacyToken(bearerToken, request);
        filterChain.doFilter(request, response);
    }

    private void authenticateFromJwt(Claims claims, HttpServletRequest request) {
        try {
            Integer accountId = Integer.parseInt(claims.getSubject());
            String rolesStr   = claims.get("roles", String.class);

            List<SimpleGrantedAuthority> authorities = (rolesStr != null && !rolesStr.isBlank())
                    ? Arrays.stream(rolesStr.split(","))
                             .map(r -> new SimpleGrantedAuthority("ROLE_" + r.trim().toUpperCase()))
                             .collect(Collectors.toList())
                    : List.of(new SimpleGrantedAuthority("ROLE_USER"));

            var auth = new UsernamePasswordAuthenticationToken(accountId, null, authorities);
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (Exception e) {
            log.debug("JWT auth setup failed: {}", e.getMessage());
        }
    }

    private void authenticateFromLegacyToken(String token, HttpServletRequest request) {
        try {
            Optional<Account> accOpt = accountDAO.findAll().stream()
                    .filter(a -> token.equals(a.getToken()))
                    .findFirst();

            if (accOpt.isEmpty() || !accOpt.get().isAccountActive()) return;

            Account acc = accOpt.get();
            List<SimpleGrantedAuthority> authorities = acc.isAdminAccount()
                    ? List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"))
                    : List.of(new SimpleGrantedAuthority("ROLE_USER"));

            var auth = new UsernamePasswordAuthenticationToken(acc.getAccountID(), null, authorities);
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (Exception e) {
            log.debug("Legacy token auth failed: {}", e.getMessage());
        }
    }

    private String extractBearerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7).trim();
        }
        return null;
    }
}
