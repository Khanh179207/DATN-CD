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

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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

        if (body.containsKey("username")) acc.setUsername(body.get("username"));
        if (body.containsKey("avatar")) acc.setAvatar(body.get("avatar"));
        if (body.containsKey("bio")) acc.setBio(body.get("bio"));
        if (body.containsKey("password") && !body.get("password").isBlank()) {
            acc.setPassword(passwordEncoder.encode(body.get("password")));
        }

        accountDAO.save(acc);
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

        return dto;
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchUsers(@RequestParam(required = false, defaultValue = "") String keyword) {
        List<Account> accounts;

        if (keyword.isBlank()) {
            accounts = accountDAO.findAll(); // Nếu không gõ gì thì lấy tất cả
        } else {
            accounts = accountDAO.findByUsernameContainingIgnoreCase(keyword.trim()); // Tìm theo tên
        }

        List<UserProfileDTO> result = accounts.stream()
                .map(this::toDTO)
                .toList();

        return ResponseEntity.ok(result);
    }
}
