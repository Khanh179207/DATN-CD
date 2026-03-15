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
import poly.edu.service.CommentService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentDAO commentDAO;
    private final PostDAO postDAO;
    private final AccountDAO accountDAO;
    private final CommentService commentService;

    @GetMapping("/post/{postID}")
    public ResponseEntity<List<CommentDTO>> getByPost(@PathVariable Integer postID) {
        return ResponseEntity.ok(commentService.getCommentsByPost(postID));
    }

    @PostMapping
    public ResponseEntity<?> addComment(@RequestBody CommentDTO req) {
        if (req.getAccountID() == null || req.getContent() == null || req.getContent().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Thiếu thông tin bình luận"));
        }

        Account account = accountDAO.findById(req.getAccountID()).orElse(null);
        if (account == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Không tìm thấy tài khoản"));
        }

        Comment parentComment = null;
        if (req.getCmtid() != null) {
            parentComment = commentDAO.findById(req.getCmtid()).orElse(null);
            if (parentComment == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Không tìm thấy bình luận gốc"));
            }
        }

        Post post = null;
        if (req.getPostID() != null) {
            post = postDAO.findById(req.getPostID()).orElse(null);
            if (post == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Không tìm thấy bài viết"));
            }
        } else if (parentComment == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Thiếu thông tin bài viết hoặc bình luận gốc"));
        } else {
            post = parentComment.getPost(); // Nếu postID null, kế thừa post từ bình luận gốc
        }

        Comment comment = Comment.builder()
                .post(post)
                .parentComment(parentComment)
                .account(account)
                .content(req.getContent())
                .build();

        Comment saved = commentDAO.save(comment);
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
        dto.setCmtid(c.getParentComment() != null ? c.getParentComment().getCommentID() : null);
        dto.setContent(c.getContent());
        if (c.getAccount() != null) {
            dto.setAccountID(c.getAccount().getAccountID());
            dto.setAuthorName(c.getAccount().getUsername());
            dto.setAuthorAvatar(c.getAccount().getAvatar());
        }
        dto.setCreatedAt(c.getCreatedAt());
        return dto;
    }
}
