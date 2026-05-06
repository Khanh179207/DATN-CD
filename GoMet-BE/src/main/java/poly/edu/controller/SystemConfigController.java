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

    // 🟢 PUBLIC: API chuyên biệt tính toán trạng thái Lễ hội (Logic: Check công tắc -> Check ngày)
    @GetMapping("/holiday-status")
    public ResponseEntity<Map<String, Boolean>> checkHolidayStatus() {
        boolean isHolidayActive = false;
        try {
            // 🔥 BƯỚC 1: KIỂM TRA CHẾ ĐỘ LỄ HỘI (CÔNG TẮC TỔNG) TRƯỚC
            String freeAccess = systemConfigDAO.findById("FREE_ACCESS_EVENT")
                    .map(SystemConfig::getConfigValue).orElse("FALSE");

            if ("TRUE".equalsIgnoreCase(freeAccess)) {
                // 🔥 BƯỚC 2: NẾU CÔNG TẮC BẬT, MỚI ĐI KIỂM TRA NGÀY
                String startStr = systemConfigDAO.findById("HOLIDAY_START")
                        .map(SystemConfig::getConfigValue).orElse(null);
                String endStr = systemConfigDAO.findById("HOLIDAY_END")
                        .map(SystemConfig::getConfigValue).orElse(null);

                if (startStr != null && endStr != null && !startStr.isBlank() && !endStr.isBlank()) {
                    LocalDateTime startTime = LocalDateTime.parse(startStr);
                    LocalDateTime endTime = LocalDateTime.parse(endStr);
                    LocalDateTime now = LocalDateTime.now();

                    // Kiểm tra xem thời gian hiện tại có nằm trong khoảng không
                    if (!now.isBefore(startTime) && !now.isAfter(endTime)) {
                        isHolidayActive = true;
                    }
                } else {
                    // Nếu công tắc BẬT nhưng Admin không cài đặt ngày -> Mặc định là đang Lễ hội
                    isHolidayActive = true;
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi kiểm tra chế độ lễ hội: " + e.getMessage());
        }

        // Trả về kết quả cuối cùng cho Frontend
        return ResponseEntity.ok(Map.of("isHolidayActive", isHolidayActive));
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