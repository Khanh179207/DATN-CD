package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poly.edu.dao.AccountDAO;
import poly.edu.dao.FollowDAO;
import poly.edu.entity.Account;
import poly.edu.entity.Follow;
import poly.edu.service.NotificationService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/follows")
@RequiredArgsConstructor
public class FollowController {

    private final FollowDAO followDAO;
    private final AccountDAO accountDAO;
    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<?> follow(
            @RequestParam Integer followerID,
            @RequestParam Integer followeeID) {
        if (followerID.equals(followeeID)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Không thể tự follow"));
        }

        // 1. Tìm bản ghi dựa trên cặp ID (KHÔNG lọc theo Status ở bước này)
        // Bạn cần thêm hàm findByFollower_AccountIDAndFollowee_AccountID vào FollowDAO
        Optional<Follow> existing = followDAO.findByFollower_AccountIDAndFollowee_AccountID(followerID, followeeID);

        if (existing.isPresent()) {
            Follow f = existing.get();
            if (f.getStatus() == 1) {
                return ResponseEntity.ok(Map.of("message", "Đã follow trước đó"));
            }
            // 2. Nếu đã tồn tại nhưng Status = 0 (đã unfollow trước đó), thì UPDATE lại thành 1
            f.setStatus(1);
            f.setFollowedAt(LocalDate.now());
            followDAO.save(f);
        } else {
            // 3. Nếu hoàn toàn chưa có bản ghi nào thì mới tạo mới (INSERT)
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
        }

        // Tạo thông báo
        Account followerObj = accountDAO.findById(followerID).get();
        notificationService.notifyFollow(followerObj.getUsername(), followeeID);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Đã follow"));
    }

    @DeleteMapping
    public ResponseEntity<?> unfollow(
            @RequestParam Integer followerID,
            @RequestParam Integer followeeID) {
        // Chỉ cần tìm bản ghi đang Active (Status = 1) để Unfollow
        followDAO.findByFollower_AccountIDAndFollowee_AccountIDAndStatus(followerID, followeeID, 1)
                .ifPresent(f -> {
                    f.setStatus(0);
                    // Có thể set thêm f.setUnFollowedAt(LocalDate.now()) nếu bạn giữ cột này
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

    @GetMapping("/my-follows")
    public ResponseEntity<List<Integer>> getMyFollows(@RequestParam Integer followerID) {
        // Truyền trạng thái 1 (đang follow) vào hàm DAO
        List<Integer> followedIds = followDAO.findFolloweeIdsByFollowerIdAndStatus(followerID, 1);
        return ResponseEntity.ok(followedIds);
    }
}
