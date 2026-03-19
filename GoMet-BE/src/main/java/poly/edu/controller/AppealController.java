package poly.edu.controller;

import poly.edu.dto.AppealDTO;
import poly.edu.service.AppealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AppealController {

    @Autowired
    private AppealService appealService;

    /**
     * POST /api/appeals
     * Create a new appeal (public endpoint)
     */
    @PostMapping("/appeals")
    public ResponseEntity<?> createAppeal(
            @RequestBody Map<String, String> request,
            HttpServletRequest httpRequest) {
        try {
            String email = request.get("email");
            String reason = request.get("reason");

            if (email == null || email.trim().isEmpty() || 
                reason == null || reason.trim().isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Email và lý do không được bỏ trống");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // Get client IP
            String ipAddress = getClientIp(httpRequest);

            // Create appeal
            AppealDTO appeal = appealService.createAppeal(
                    email.trim(),
                    reason.trim(),
                    ipAddress
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Khiếu nại đã được tạo thành công");
            response.put("data", appeal);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * GET /api/appeals/status
     * Get appeal status by email (public endpoint - user tracking)
     */
    @GetMapping("/appeals/status")
    public ResponseEntity<?> getAppealStatus(@RequestParam String email) {
        try {
            Optional<AppealDTO> appeal = appealService.getAppealStatusByEmail(email);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            
            if (appeal.isPresent()) {
                response.put("data", appeal.get());
            } else {
                response.put("data", null);
                response.put("message", "Không tìm thấy khiếu nại cho email này");
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * GET /api/admin/appeals
     * Get all appeals (admin only)
     */
    @GetMapping("/admin/appeals")
    public ResponseEntity<?> getAllAppeals() {
        try {
            List<AppealDTO> appeals = appealService.getAllAppeals();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", appeals);
            response.put("count", appeals.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * GET /api/admin/appeals/{appealID}
     * Get appeal by ID (admin only)
     */
    @GetMapping("/admin/appeals/{appealID}")
    public ResponseEntity<?> getAppealById(@PathVariable Integer appealID) {
        try {
            Optional<AppealDTO> appeal = appealService.getAppealById(appealID);

            if (!appeal.isPresent()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Khiếu nại không tồn tại");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", appeal.get());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * PUT /api/admin/appeals/{appealID}
     * Update appeal status and note (admin only)
     */
    @PutMapping("/admin/appeals/{appealID}")
    public ResponseEntity<?> updateAppeal(
            @PathVariable Integer appealID,
            @RequestBody Map<String, String> request) {
        try {
            String status = request.get("status");
            String note = request.get("note");

            if (status == null || status.trim().isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Trạng thái không được bỏ trống");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            AppealDTO updated = appealService.updateAppeal(
                    appealID,
                    status.trim(),
                    note != null ? note.trim() : ""
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Cập nhật khiếu nại thành công");
            response.put("data", updated);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * POST /api/admin/appeals/{appealID}/unban
     * Unban account by appeal (admin only)
     */
    @PostMapping("/admin/appeals/{appealID}/unban")
    public ResponseEntity<?> unbanAccount(@PathVariable Integer appealID) {
        try {
            boolean result = appealService.unbanAccountByAppeal(appealID);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Tài khoản đã được gỡ ban thành công");
            response.put("data", result);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * Helper method to get client IP address
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
