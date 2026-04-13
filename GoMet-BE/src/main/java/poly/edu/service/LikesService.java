package poly.edu.service;

import poly.edu.dto.LikesDTO;
import java.util.List;

public interface LikesService {
    // Trả về true nếu là Like mới, false nếu là Hủy Like
    boolean toggleLike(Integer accountId, Integer postId);

    // Lấy danh sách người đã Like bài viết
    List<LikesDTO> getLikesByPostId(Integer postId);

    // Kiểm tra trạng thái Like của user hiện tại
    boolean checkUserLikedPost(Integer accountId, Integer postId);
}