package poly.edu.service;

import poly.edu.dto.AdminPostDTO;
import java.util.List;

public interface AdminPostService {
    List<AdminPostDTO> findAll();
    List<AdminPostDTO> findByApproved(Integer isApproved);

    // 🔥 Xóa các hàm cũ đi, chỉ dùng hàm MỚI có truyền Admin ID để lưu Log
    void approvePost(Integer id, Integer adminId, String adminName);
    void rejectPost(Integer id, Integer adminId, String adminName, String reason);

    // Các hàm khác giữ nguyên
    void deactivePost(Integer id);
    void deletePost(Integer id);
    void banAuthorByPostId(Integer postId);
}