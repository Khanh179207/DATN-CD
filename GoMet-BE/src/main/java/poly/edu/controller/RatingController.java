package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poly.edu.dao.AccountDAO;
import poly.edu.dao.PostDAO;
import poly.edu.dao.RatingDAO;
import poly.edu.dto.RatingRequestDTO;
import poly.edu.entity.Account;
import poly.edu.entity.Post;
import poly.edu.entity.Rating;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingDAO ratingDAO;
    private final PostDAO postDAO;
    private final AccountDAO accountDAO;

    @PostMapping
    public ResponseEntity<?> rate(@RequestBody RatingRequestDTO req) {
        if (req.getAccountID() == null || req.getPostID() == null || req.getRate() == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Thiếu thông tin đánh giá"));
        }
        if (req.getRate() < 1 || req.getRate() > 5) {
            return ResponseEntity.badRequest().body(Map.of("message", "Điểm đánh giá phải từ 1 đến 5"));
        }

        Post post = postDAO.findById(req.getPostID()).orElse(null);
        Account account = accountDAO.findById(req.getAccountID()).orElse(null);
        if (post == null || account == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Không tìm thấy bài viết hoặc tài khoản"));
        }

        Optional<Rating> existing = ratingDAO.findByAccount_AccountIDAndPost_PostID(req.getAccountID(), req.getPostID());
        Rating rating;
        if (existing.isPresent()) {
            rating = existing.get();
            rating.setRate(req.getRate());
        } else {
            rating = Rating.builder()
                    .account(account)
                    .post(post)
                    .rate(req.getRate())
                    .build();
        }

        ratingDAO.save(rating);
        return ResponseEntity.ok(Map.of("message", "Đánh giá thành công", "rate", rating.getRate()));
    }

    @GetMapping("/check")
    public ResponseEntity<?> checkRating(
            @RequestParam Integer accountID,
            @RequestParam Integer postID) {
        Optional<Rating> rating = ratingDAO.findByAccount_AccountIDAndPost_PostID(accountID, postID);
        if (rating.isPresent()) {
            return ResponseEntity.ok(Map.of("rated", true, "rate", rating.get().getRate()));
        }
        return ResponseEntity.ok(Map.of("rated", false));
    }
}
