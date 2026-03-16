package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poly.edu.dao.AccountDAO;
import poly.edu.dao.CommentDAO;
import poly.edu.dao.PostDAO;
import poly.edu.dto.CommentDTO;
import poly.edu.entity.Account;
import poly.edu.entity.Comment;
import poly.edu.entity.Post;
import poly.edu.service.NotificationService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentDAO commentDAO;
    private final PostDAO postDAO;
    private final AccountDAO accountDAO;
    private final NotificationService notificationService;

    @GetMapping("/post/{postID}")
    public ResponseEntity<List<CommentDTO>> getByPost(@PathVariable Integer postID) {
        List<CommentDTO> result = commentDAO.findByPost_PostID(postID).stream()
                .map(this::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<?> addComment(@RequestBody CommentDTO req) {
        if (req.getPostID() == null || req.getAccountID() == null || req.getContent() == null || req.getContent().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Thiếu thông tin bình luận"));
        }
        Post post = postDAO.findById(req.getPostID()).orElse(null);
        Account account = accountDAO.findById(req.getAccountID()).orElse(null);
        if (post == null || account == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Không tìm thấy bài viết hoặc tài khoản"));
        }

        Comment comment = Comment.builder()
                .post(post)
                .account(account)
                .content(req.getContent())
                .build();

        Comment saved = commentDAO.save(comment);

        // Create notification for the post owner if commenter is not the owner
        Integer postOwnerId = post.getAccount().getAccountID();
        if (!req.getAccountID().equals(postOwnerId)) {
            notificationService.notifyComment(account.getUsername(), postOwnerId, req.getPostID());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Integer id) {
        if (!commentDAO.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        commentDAO.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Đã xóa bình luận"));
    }

    private CommentDTO toDTO(Comment c) {
        CommentDTO dto = new CommentDTO();
        dto.setCommentID(c.getCommentID());
        dto.setPostID(c.getPost() != null ? c.getPost().getPostID() : null);
        dto.setContent(c.getContent());
        if (c.getAccount() != null) {
            dto.setAccountID(c.getAccount().getAccountID());
            dto.setAuthorName(c.getAccount().getUsername());
            dto.setAuthorAvatar(c.getAccount().getAvatar());
        }
        return dto;
    }
}
