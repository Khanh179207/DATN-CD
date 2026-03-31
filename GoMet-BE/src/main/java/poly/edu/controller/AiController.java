package poly.edu.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    // ✅ Bây giờ Spring sẽ hiểu biến này lấy từ file application.properties
    @Value("${groq.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/chef-chat")
    public ResponseEntity<?> chefChat(@RequestBody Map<String, Object> payload) {
        String url = "https://api.groq.com/openai/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            // In ra console để sếp biết lỗi gì (nếu có) khi chạy dev
            System.err.println("Lỗi gọi Groq: " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Đầu bếp đang đi chợ rồi sếp!"));
        }
    }
}