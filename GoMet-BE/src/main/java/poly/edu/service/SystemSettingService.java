package poly.edu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poly.edu.dao.SystemSettingDAO;
import poly.edu.dto.ModuleMaintenanceDTO;
import poly.edu.dto.ModuleMaintenanceUpdateDTO;
import poly.edu.dto.MaintenanceSettingsResponseDTO;
import poly.edu.dto.SystemPublicSettingsDTO;
import poly.edu.entity.SystemSetting;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SystemSettingService {

    public static final String KEY_MAINTENANCE_MODE = "MAINTENANCE_MODE";
    public static final String KEY_MAINTENANCE_MESSAGE = "MAINTENANCE_MESSAGE";
    private static final String DEFAULT_MESSAGE = "Hệ thống đang bảo trì. Vui lòng quay lại sau.";
    public static final String MODULE_POSTS = "POSTS";
    public static final String MODULE_COMMENTS = "COMMENTS";
    public static final String MODULE_EVENTS = "EVENTS";
    public static final String MODULE_LEADERBOARD = "LEADERBOARD";
    public static final String MODULE_NOTIFICATIONS = "NOTIFICATIONS";
    public static final String MODULE_PROFILE = "PROFILE";
    public static final String MODULE_MEALPLAN = "MEALPLAN";
    public static final String MODULE_AI = "AI";

    private static final Map<String, ModuleMeta> MODULE_DEFINITIONS = new LinkedHashMap<>();

    static {
        MODULE_DEFINITIONS.put(MODULE_POSTS, new ModuleMeta(MODULE_POSTS, "Bài viết & danh mục", "MAINTENANCE_MODULE_POSTS"));
        MODULE_DEFINITIONS.put(MODULE_COMMENTS, new ModuleMeta(MODULE_COMMENTS, "Bình luận", "MAINTENANCE_MODULE_COMMENTS"));
        MODULE_DEFINITIONS.put(MODULE_EVENTS, new ModuleMeta(MODULE_EVENTS, "Sự kiện", "MAINTENANCE_MODULE_EVENTS"));
        MODULE_DEFINITIONS.put(MODULE_LEADERBOARD, new ModuleMeta(MODULE_LEADERBOARD, "Leaderboard & chứng nhận", "MAINTENANCE_MODULE_LEADERBOARD"));
        MODULE_DEFINITIONS.put(MODULE_NOTIFICATIONS, new ModuleMeta(MODULE_NOTIFICATIONS, "Thông báo", "MAINTENANCE_MODULE_NOTIFICATIONS"));
        MODULE_DEFINITIONS.put(MODULE_PROFILE, new ModuleMeta(MODULE_PROFILE, "Hồ sơ người dùng", "MAINTENANCE_MODULE_PROFILE"));
        MODULE_DEFINITIONS.put(MODULE_MEALPLAN, new ModuleMeta(MODULE_MEALPLAN, "Meal plan", "MAINTENANCE_MODULE_MEALPLAN"));
        MODULE_DEFINITIONS.put(MODULE_AI, new ModuleMeta(MODULE_AI, "AI assistant", "MAINTENANCE_MODULE_AI"));
    }

    private final SystemSettingDAO systemSettingDAO;

    @Value("${system.settings.cache.ttl-seconds:15}")
    private long cacheTtlSeconds;

    private volatile SettingsCache cache;

    public boolean isMaintenanceMode() {
        return getCached().maintenanceMode();
    }

    public String getMaintenanceMessage() {
        return getCached().message();
    }

    public boolean isModuleMaintenanceEnabled(String moduleKey) {
        if (moduleKey == null || moduleKey.isBlank()) {
            return false;
        }
        SettingsCache current = getCached();
        return current.moduleEnabled().getOrDefault(moduleKey, false);
    }

    public String getModuleDisplayName(String moduleKey) {
        ModuleMeta meta = MODULE_DEFINITIONS.get(moduleKey);
        return meta != null ? meta.displayName() : moduleKey;
    }

    public SystemPublicSettingsDTO getPublicSettings() {
        SettingsCache current = getCached();
        return new SystemPublicSettingsDTO(current.maintenanceMode(), current.message(), current.modules());
    }

    public MaintenanceSettingsResponseDTO getAdminSettings() {
        SettingsCache current = getCached();
        return new MaintenanceSettingsResponseDTO(current.maintenanceMode(), current.message(), current.updatedAt(), current.modules());
    }

    @Transactional
    public MaintenanceSettingsResponseDTO updateMaintenance(boolean enabled, String message, List<ModuleMaintenanceUpdateDTO> moduleUpdates) {
        LocalDateTime now = LocalDateTime.now();

        SystemSetting mode = systemSettingDAO.findById(KEY_MAINTENANCE_MODE)
                .orElse(SystemSetting.builder().key(KEY_MAINTENANCE_MODE).build());
        mode.setValue(enabled ? "1" : "0");
        mode.setUpdatedAt(now);
        systemSettingDAO.save(mode);

        String finalMessage = (message == null || message.isBlank()) ? DEFAULT_MESSAGE : message.trim();
        SystemSetting msg = systemSettingDAO.findById(KEY_MAINTENANCE_MESSAGE)
                .orElse(SystemSetting.builder().key(KEY_MAINTENANCE_MESSAGE).build());
        msg.setValue(finalMessage);
        msg.setUpdatedAt(now);
        systemSettingDAO.save(msg);

        if (moduleUpdates != null) {
            Map<String, Boolean> updateMap = moduleUpdates.stream()
                    .filter(m -> m.getKey() != null && !m.getKey().isBlank())
                    .collect(Collectors.toMap(ModuleMaintenanceUpdateDTO::getKey, ModuleMaintenanceUpdateDTO::isEnabled, (a, b) -> b));

            for (ModuleMeta meta : MODULE_DEFINITIONS.values()) {
                if (!updateMap.containsKey(meta.key())) {
                    continue;
                }
                SystemSetting moduleSetting = systemSettingDAO.findById(meta.settingKey())
                        .orElse(SystemSetting.builder().key(meta.settingKey()).build());
                moduleSetting.setValue(updateMap.get(meta.key()) ? "1" : "0");
                moduleSetting.setUpdatedAt(now);
                systemSettingDAO.save(moduleSetting);
            }
        }

        SettingsCache refreshed = loadFromDb(System.currentTimeMillis());
        cache = refreshed;
        return new MaintenanceSettingsResponseDTO(refreshed.maintenanceMode(), refreshed.message(), refreshed.updatedAt(), refreshed.modules());
    }

    private SettingsCache getCached() {
        SettingsCache current = cache;
        long nowMs = System.currentTimeMillis();
        if (current != null && current.expiresAtMs() > nowMs) {
            return current;
        }
        synchronized (this) {
            current = cache;
            if (current != null && current.expiresAtMs() > nowMs) {
                return current;
            }
            SettingsCache loaded = loadFromDb(nowMs);
            cache = loaded;
            return loaded;
        }
    }

    private SettingsCache loadFromDb(long nowMs) {
        SystemSetting mode = systemSettingDAO.findById(KEY_MAINTENANCE_MODE)
                .orElse(SystemSetting.builder()
                        .key(KEY_MAINTENANCE_MODE)
                        .value("0")
                        .updatedAt(LocalDateTime.now())
                        .build());

        SystemSetting message = systemSettingDAO.findById(KEY_MAINTENANCE_MESSAGE)
                .orElse(SystemSetting.builder()
                        .key(KEY_MAINTENANCE_MESSAGE)
                        .value(DEFAULT_MESSAGE)
                        .updatedAt(LocalDateTime.now())
                        .build());

        boolean enabled = "1".equals(mode.getValue());
        String finalMessage = (message.getValue() == null || message.getValue().isBlank())
                ? DEFAULT_MESSAGE
                : message.getValue();
        LocalDateTime updatedAt = mode.getUpdatedAt() != null ? mode.getUpdatedAt() : LocalDateTime.now();

        Map<String, Boolean> moduleEnabled = new LinkedHashMap<>();
        List<ModuleMaintenanceDTO> modules = MODULE_DEFINITIONS.values().stream()
            .map(meta -> {
                SystemSetting moduleSetting = systemSettingDAO.findById(meta.settingKey())
                    .orElse(SystemSetting.builder().key(meta.settingKey()).value("0").updatedAt(LocalDateTime.now()).build());
                boolean moduleOn = "1".equals(moduleSetting.getValue());
                moduleEnabled.put(meta.key(), moduleOn);
                return new ModuleMaintenanceDTO(meta.key(), meta.displayName(), moduleOn);
            })
            .toList();

        return new SettingsCache(enabled, finalMessage, updatedAt, modules, moduleEnabled, nowMs + cacheTtlSeconds * 1000);
    }

        private record SettingsCache(
            boolean maintenanceMode,
            String message,
            LocalDateTime updatedAt,
            List<ModuleMaintenanceDTO> modules,
            Map<String, Boolean> moduleEnabled,
            long expiresAtMs
        ) {}

        private record ModuleMeta(String key, String displayName, String settingKey) {}
}
