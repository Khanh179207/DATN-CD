package poly.edu.service;

import poly.edu.dto.AdminCommentDTO;
import poly.edu.dto.CommentDTO;
import java.util.List;
import java.util.Map;

public interface CommentService {
    // Sửa hàm lấy danh sách comment
    List<CommentDTO> getCommentsByPost(Integer postID, Integer currentAccountID);
    CommentDTO saveNewComment(CommentDTO req);
    void delete(Integer id);
    List<AdminCommentDTO> findAll();
    Map<String, Object> getRatingStats(Integer postID);
}