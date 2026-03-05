package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poly.edu.dao.AccountDAO;
import poly.edu.dao.FollowDAO;
import poly.edu.dao.PostDAO;
import poly.edu.dto.UserProfileDTO;
import poly.edu.entity.Account;
import poly.edu.service.EmailService;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin
public class UserController {

    private final BCryptPasswordEncoder passwordEncoder;

    private final AccountDAO accountDAO;
    private final PostDAO postDAO;
    private final FollowDAO followDAO;
    private final EmailService emailService;

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileDTO> getProfile(@PathVariable Integer id) {
        return accountDAO.findById(id).map(acc -> {
            UserProfileDTO dto = toDTO(acc);
            return ResponseEntity.ok(dto);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProfile(
            @PathVariable Integer id,
            @RequestBody Map<String, String> body) {
        Account acc = accountDAO.findById(id).orElse(null);
        if (acc == null) return ResponseEntity.notFound().build();

        List<String> changed = new ArrayList<>();

        if (body.containsKey("username") && !body.get("username").equals(acc.getUsername())) {
            acc.setUsername(body.get("username"));
            changed.add("Display name (username)");
        }
        if (body.containsKey("avatar") && !body.get("avatar").equals(acc.getAvatar())) {
            acc.setAvatar(body.get("avatar"));
            changed.add("Profile photo (avatar)");
        }
        if (body.containsKey("bio")) {
            String newBio = body.get("bio");
            String oldBio = acc.getBio() == null ? "" : acc.getBio();
            if (!newBio.equals(oldBio)) {
                acc.setBio(newBio);
                changed.add("Bio / About me");
            }
        }
        if (body.containsKey("password") && !body.get("password").isBlank()) {
            acc.setPassword(passwordEncoder.encode(body.get("password")));
            changed.add("Password");
        }

        accountDAO.save(acc);

        // Send email notification only when at least one field actually changed
        if (!changed.isEmpty() && acc.getEmail() != null) {
            emailService.sendProfileUpdateEmail(acc.getEmail(), acc.getUsername(), changed);
        }

        return ResponseEntity.ok(toDTO(acc));
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<?> getStats(@PathVariable Integer id) {
        Account acc = accountDAO.findById(id).orElse(null);
        if (acc == null) return ResponseEntity.notFound().build();

        long postCount = postDAO.countByAccountId(id);
        long followerCount = followDAO.countByFollowee_AccountIDAndStatus(id, 1);
        long followingCount = followDAO.countByFollower_AccountIDAndStatus(id, 1);

        return ResponseEntity.ok(Map.of(
                "postCount", postCount,
                "followerCount", followerCount,
                "followingCount", followingCount,
                "point", acc.getPoint()
        ));
    }

    private UserProfileDTO toDTO(Account acc) {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setAccountID(acc.getAccountID());
        dto.setUsername(acc.getUsername());
        dto.setEmail(acc.getEmail());
        dto.setAvatar(acc.getAvatar());
        dto.setPoint(acc.getPoint());
        dto.setIsPremium(acc.getIsPremium());
        dto.setIsAdmin(acc.getIsAdmin());
        dto.setBio(acc.getBio());
        dto.setCreatedAt(acc.getCreatedAt());

        long postCount = acc.getPosts() != null ? acc.getPosts().stream()
                .filter(p -> p.getIsApproved() == 1 && p.getIsActive() == 1).count() : 0;
        dto.setPostCount(postCount);

        long followerCount = followDAO.countByFollowee_AccountIDAndStatus(acc.getAccountID(), 1);
        dto.setFollowerCount(followerCount);

        long followingCount = followDAO.countByFollower_AccountIDAndStatus(acc.getAccountID(), 1);
        dto.setFollowingCount(followingCount);

        long totalViews = acc.getPosts() != null ? acc.getPosts().stream()
                .mapToLong(p -> p.getViews() != null ? p.getViews() : 0).sum() : 0;
        dto.setTotalViews(totalViews);

        dto.setLastLoginAt(acc.getLastLoginAt());
        dto.setLastLoginIp(acc.getLastLoginIp());
        dto.setMfaEnabled(acc.getMfaEnabled());

        return dto;
    }
}
