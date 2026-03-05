package poly.edu.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import poly.edu.dao.AccountDAO;
import poly.edu.service.SystemSettingService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class MaintenanceModeFilterTest {

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("returns 503 for non-admin request when maintenance is ON")
    void blocksRegularRequestWhenMaintenanceOn() throws Exception {
        SystemSettingService settings = mock(SystemSettingService.class);
        when(settings.isMaintenanceMode()).thenReturn(true);
        when(settings.getMaintenanceMessage()).thenReturn("Maintenance now");

        MaintenanceModeFilter filter = new MaintenanceModeFilter(
                settings,
                mock(AccountDAO.class),
                new ObjectMapper()
        );

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/posts");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertThat(response.getStatus()).isEqualTo(503);
        assertThat(response.getContentAsString()).contains("MAINTENANCE");
    }

    @Test
    @DisplayName("allows admin endpoint for authenticated admin when maintenance is ON")
    void allowsAdminEndpointForAdmin() throws Exception {
        SystemSettingService settings = mock(SystemSettingService.class);
        when(settings.isMaintenanceMode()).thenReturn(true);

        MaintenanceModeFilter filter = new MaintenanceModeFilter(
                settings,
                mock(AccountDAO.class),
                new ObjectMapper()
        );

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                1,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/admin/system/settings");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    @DisplayName("returns 503 with module code when only module maintenance is ON")
    void blocksModuleRequestWhenModuleMaintenanceOn() throws Exception {
        SystemSettingService settings = mock(SystemSettingService.class);
        when(settings.isMaintenanceMode()).thenReturn(false);
        when(settings.isModuleMaintenanceEnabled(SystemSettingService.MODULE_POSTS)).thenReturn(true);
        when(settings.getModuleDisplayName(SystemSettingService.MODULE_POSTS)).thenReturn("Bài viết & danh mục");

        MaintenanceModeFilter filter = new MaintenanceModeFilter(
                settings,
                mock(AccountDAO.class),
                new ObjectMapper()
        );

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/posts");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertThat(response.getStatus()).isEqualTo(503);
        assertThat(response.getContentAsString()).contains("MAINTENANCE_MODULE");
        assertThat(response.getContentAsString()).contains(SystemSettingService.MODULE_POSTS);
    }
}
