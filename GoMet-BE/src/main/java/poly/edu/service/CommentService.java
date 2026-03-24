package poly.edu.service;

import poly.edu.dto.AdminCommentDTO;
import poly.edu.dto.CommentDTO;
import java.util.List;
import java.util.Map;

public interface CommentService {
    // Lấy danh sách comment theo bài viết (có check like)
    List<CommentDTO> getCommentsByPost(Integer postID, Integer currentAccountID);

    // Thêm comment mới
    CommentDTO saveNewComment(CommentDTO req);

    // Admin xóa bình luận (Khóa mõm: isActive = -1)
    void delete(Integer id);

    // User tự xóa bình luận của mình (isActive = 0)
    void deleteByUser(Integer id, Integer userId);

    // Admin khôi phục bình luận (isActive = 1)
    void restore(Integer id);

    // Admin lấy tất cả bình luận
    List<AdminCommentDTO> findAll();

    // Thống kê rating
    Map<String, Object> getRatingStats(Integer postID);
}