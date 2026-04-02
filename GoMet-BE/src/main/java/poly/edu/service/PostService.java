package poly.edu.service;

import poly.edu.dto.PostDTO;
import poly.edu.entity.Post;

import java.util.List;
import java.util.Map;

public interface PostService {
    // 🔥 Tạo bài viết: Sẽ có logic tự động gán CategoryID = 1 nếu User để trống
    PostDTO createPost(PostDTO postDTO);

    // Cập nhật bài viết
    PostDTO updatePost(Integer postId, PostDTO postDTO);

    // Lấy danh sách bài theo Account (Dùng để nộp sự kiện hoặc xem Profile)
    List<PostDTO> getPostsByAccountId(Integer accountId);

    // Lấy chi tiết bài viết
    PostDTO getPostById(Integer postId);

    // Xả hoặc Ẩn bài viết
    void deletePost(Integer postId);

    // Ẩn/Hiện bài viết (is_active)
    PostDTO toggleActive(Integer postId);

    List<Map<String, Object>> getLeaderboard(String timeframe, int limit);
}