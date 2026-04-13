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

    // Sửa lại chữ ký 2 hàm này:
    void delete(Integer id, Integer adminId, String adminName);
    void restore(Integer id, Integer adminId, String adminName);

    // User tự xóa bình luận của mình (isActive = 0)
    void deleteByUser(Integer id, Integer userId);




    // Admin lấy tất cả bình luận
    List<AdminCommentDTO> findAll();

    // Thống kê rating
    Map<String, Object> getRatingStats(Integer postID);
}