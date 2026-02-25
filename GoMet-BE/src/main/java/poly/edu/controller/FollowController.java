package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poly.edu.dao.AccountDAO;
import poly.edu.dao.FollowDAO;
import poly.edu.entity.Account;
import poly.edu.entity.Follow;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/follows")
@RequiredArgsConstructor
public class FollowController {

    private final FollowDAO followDAO;
    private final AccountDAO accountDAO;

    @PostMapping
    public ResponseEntity<?> follow(
            @RequestParam Integer followerID,
            @RequestParam Integer followeeID) {
        if (followerID.equals(followeeID)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Không thể tự follow"));
        }
        Optional<Follow> existing = followDAO.findByFollower_AccountIDAndFollowee_AccountIDAndStatus(followerID, followeeID, 1);
        if (existing.isPresent()) {
            return ResponseEntity.ok(Map.of("message", "Đã follow trước đó"));
        }
        Account follower = accountDAO.findById(followerID).orElse(null);
        Account followee = accountDAO.findById(followeeID).orElse(null);
        if (follower == null || followee == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Tài khoản không tồn tại"));
        }
        Follow follow = Follow.builder()
                .follower(follower)
                .followee(followee)
                .status(1)
                .followedAt(LocalDate.now())
                .build();
        followDAO.save(follow);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Đã follow"));
    }

    @DeleteMapping
    public ResponseEntity<?> unfollow(
            @RequestParam Integer followerID,
            @RequestParam Integer followeeID) {
        followDAO.findByFollower_AccountIDAndFollowee_AccountIDAndStatus(followerID, followeeID, 1)
                .ifPresent(f -> {
                    f.setStatus(0);
                    f.setUnFollowedAt(LocalDate.now());
                    followDAO.save(f);
                });
        return ResponseEntity.ok(Map.of("message", "Đã unfollow"));
    }

    @GetMapping("/check")
    public ResponseEntity<?> checkFollow(
            @RequestParam Integer followerID,
            @RequestParam Integer followeeID) {
        boolean isFollowing = followDAO.findByFollower_AccountIDAndFollowee_AccountIDAndStatus(followerID, followeeID, 1).isPresent();
        return ResponseEntity.ok(Map.of("isFollowing", isFollowing));
    }
}
