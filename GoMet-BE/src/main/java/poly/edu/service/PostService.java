package poly.edu.service;

import poly.edu.dto.PostDTO;
import java.util.List;

public interface PostService {
    // Lưu bài viết mới (khi sếp bấm Publish ở Vue)
    PostDTO createPost(PostDTO postDTO);

    // Lấy danh sách bài để nộp sự kiện (Cái API vừa bị 404 lúc nãy)
    List<PostDTO> getPostsByAccountId(Integer accountId);

    // Lấy chi tiết bài viết để hiển thị
    PostDTO getPostById(Integer postId);
}