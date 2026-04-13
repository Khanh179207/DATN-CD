package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import poly.edu.dto.ContactDTO;
import poly.edu.service.SharePostService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/share")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class SharePostController {

    private final SharePostService sharePostService;

    /**
     * Get contacts for sharing (Followers and Following).
     */
    @GetMapping("/contacts/{accountID}")
    public ResponseEntity<List<ContactDTO>> getContacts(@PathVariable Integer accountID) {
        return ResponseEntity.ok(sharePostService.getSharingContacts(accountID));
    }

    /**
     * Share post via email.
     */
    @PostMapping("/email")
    public ResponseEntity<?> shareViaEmail(@RequestBody Map<String, Object> payload) {
        String toEmail = (String) payload.get("toEmail");
        Integer postID = (Integer) payload.get("postID");
        String senderName = (String) payload.get("senderName");
        String customMessage = (String) payload.get("customMessage");

        if (toEmail == null || postID == null || senderName == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Missing required fields"));
        }

        sharePostService.sendSharePostEmail(toEmail, postID, senderName, customMessage);
        
        return ResponseEntity.ok(Map.of("message", "Email sent successfully"));
    }
}
