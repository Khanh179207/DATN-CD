package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // 🔥 IMPORT THẺ TỪ QUYỀN LỰC
import org.springframework.web.bind.annotation.*;
import poly.edu.dao.SystemConfigDAO;
import poly.edu.entity.SystemConfig;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/system-config")
@RequiredArgsConstructor
public class SystemConfigController {

    private final SystemConfigDAO systemConfigDAO;

    // 🟢 PUBLIC: Mở cửa cho mọi người (kể cả khách) lấy cấu hình để hiển thị UI
    @GetMapping
    public ResponseEntity<List<SystemConfig>> getAllConfigs() {
        return ResponseEntity.ok(systemConfigDAO.findAll());
    }

    // 🔴 ADMIN ONLY: Chỉ Admin mới có quyền thay đổi vận mệnh của hệ thống
    @PreAuthorize("hasRole('ADMIN')") // 🔥 CHỐT CHẶN ĐỎ: Chặn đứng mọi User thường hoặc Khách
    @PutMapping("/admin/update")
    public ResponseEntity<?> updateConfigs(@RequestBody Map<String, String> payload) {
        try {
            // 🔥 Lặp qua từng Key-Value mà Frontend (Vue) gửi lên
            for (Map.Entry<String, String> entry : payload.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                // TÌM THEO KEY: Nếu có thì lấy lên sửa, NẾU CHƯA CÓ THÌ TẠO MỚI
                SystemConfig config = systemConfigDAO.findById(key).orElse(new SystemConfig());

                config.setConfigKey(key);
                config.setConfigValue(value);

                // Nếu là tạo mới thì gán tạm Group mặc định để không bị lỗi NULL
                if (config.getConfigGroup() == null) {
                    config.setConfigGroup("SYSTEM");
                }

                config.setUpdatedAt(LocalDateTime.now());

                // Lưu từng dòng an toàn
                systemConfigDAO.save(config);
            }

            return ResponseEntity.ok(Map.of("message", "Cập nhật cấu hình hệ thống thành công!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Lỗi khi cập nhật cấu hình: " + e.getMessage()));
        }
    }
}