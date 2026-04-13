package poly.edu.service;

import poly.edu.dto.PostDTO;
import java.util.List;

public interface PostService {
    // 🔥 Tạo bài viết: Sẽ có logic tự động gán CategoryID = 1 nếu User để trống
    PostDTO createPost(PostDTO postDTO);

    // Cập nhật bài viết
    PostDTO updatePost(Integer postId, PostDTO postDTO);

    // Lấy danh sách bài theo Account (Dùng để nộp sự kiện hoặc xem Profile)
    List<PostDTO> getPostsByAccountId(Integer accountId);

    // Lấy chi tiết bài viết
    PostDTO getPostById(Integer postId);

    // Xóa hoặc Ẩn bài viết
    void deletePost(Integer postId);
}