package poly.edu.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import poly.edu.dao.AccountDAO;
import poly.edu.entity.Account;
import poly.edu.service.SystemSettingService;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class MaintenanceModeFilter extends OncePerRequestFilter {

    private final SystemSettingService systemSettingService;
    private final AccountDAO accountDAO;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        if (isAlwaysAllowed(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (hasAdminRole()) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!systemSettingService.isMaintenanceMode()) {
            String moduleKey = resolveModuleKey(path);
            if (moduleKey != null && systemSettingService.isModuleMaintenanceEnabled(moduleKey)) {
                writeModuleMaintenanceResponse(response, moduleKey);
                return;
            }
            filterChain.doFilter(request, response);
            return;
        }

        if (path.startsWith("/api/auth")) {
            if ("/api/auth/login".equals(path)) {
                CachedBodyHttpServletRequest wrapped = new CachedBodyHttpServletRequest(request);
                if (isAdminLoginAttempt(wrapped)) {
                    filterChain.doFilter(wrapped, response);
                } else {
                    writeMaintenanceResponse(response);
                }
                return;
            }
            if ("/api/auth/mfa/verify".equals(path)
                    || "/api/auth/verify-login".equals(path)
                    || "/api/auth/refresh".equals(path)
                    || "/api/auth/logout".equals(path)) {
                filterChain.doFilter(request, response);
                return;
            }
            writeMaintenanceResponse(response);
            return;
        }

        writeMaintenanceResponse(response);
    }

    private String resolveModuleKey(String path) {
        if (path == null || !path.startsWith("/api/")) {
            return null;
        }

        if (path.startsWith("/api/posts")
                || path.startsWith("/api/categories")
                || path.startsWith("/api/history")
                || path.startsWith("/api/ratings")
                || path.startsWith("/api/favorites")) {
            return SystemSettingService.MODULE_POSTS;
        }
        if (path.startsWith("/api/comments")) {
            return SystemSettingService.MODULE_COMMENTS;
        }
        if (path.startsWith("/api/events")) {
            return SystemSettingService.MODULE_EVENTS;
        }
        if (path.startsWith("/api/leaderboard")
                || path.startsWith("/api/certificates")
                || path.startsWith("/api/admin/leaderboard")
                || path.startsWith("/api/admin/certificates")) {
            return SystemSettingService.MODULE_LEADERBOARD;
        }
        if (path.startsWith("/api/notifications")) {
            return SystemSettingService.MODULE_NOTIFICATIONS;
        }
        if (path.startsWith("/api/user")
                || path.startsWith("/api/sessions")
                || path.startsWith("/api/subscription")) {
            return SystemSettingService.MODULE_PROFILE;
        }
        if (path.startsWith("/api/mealplan")) {
            return SystemSettingService.MODULE_MEALPLAN;
        }
        if (path.startsWith("/api/ai") || path.startsWith("/api/suggestions")) {
            return SystemSettingService.MODULE_AI;
        }
        return null;
    }

    private boolean isAlwaysAllowed(String path) {
        return "/api/system/settings/public".equals(path)
                || path.startsWith("/api/media/")
                || path.startsWith("/api/share/")
                || path.startsWith("/share/")
                || "/error".equals(path)
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui");
    }

    private boolean hasAdminRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return false;
        }
        return auth.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }

    private boolean isAdminLoginAttempt(HttpServletRequest request) {
        try {
            String body = new String(request.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            if (body.isBlank()) {
                return false;
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> payload = objectMapper.readValue(body, Map.class);
            String email = payload.get("email") != null ? payload.get("email").toString() : null;
            String password = payload.get("password") != null ? payload.get("password").toString() : null;
            if (email == null || email.isBlank() || password == null || password.isBlank()) {
                return false;
            }

            Account account = accountDAO.findByEmail(email).orElseGet(() -> accountDAO.findByUsernameIgnoreCase(email).orElse(null));
            return account != null
                    && account.isAdminAccount()
                    && account.isAccountActive()
                    && BCrypt.checkpw(password, account.getPassword());
        } catch (Exception ignored) {
            return false;
        }
    }

    private void writeMaintenanceResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(Map.of(
                "code", "MAINTENANCE",
                "message", systemSettingService.getMaintenanceMessage()
        )));
    }

    private void writeModuleMaintenanceResponse(HttpServletResponse response, String moduleKey) throws IOException {
        response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(Map.of(
                "code", "MAINTENANCE_MODULE",
                "module", moduleKey,
                "message", "Chức năng " + systemSettingService.getModuleDisplayName(moduleKey) + " đang bảo trì."
        )));
    }

    private static class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {
        private final byte[] cachedBody;

        CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {
            super(request);
            cachedBody = request.getInputStream().readAllBytes();
        }

        @Override
        public ServletInputStream getInputStream() {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(cachedBody);
            return new ServletInputStream() {
                @Override
                public int read() {
                    return byteArrayInputStream.read();
                }

                @Override
                public boolean isFinished() {
                    return byteArrayInputStream.available() == 0;
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setReadListener(ReadListener readListener) {
                }
            };
        }

        @Override
        public BufferedReader getReader() {
            return new BufferedReader(new InputStreamReader(getInputStream()));
        }
    }
}
