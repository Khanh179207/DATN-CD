package poly.edu.service;

public interface CommentLikeService {
    /**
     * Xử lý toggle Like (Thêm nếu chưa có, xóa nếu đã có)
     * @return true nếu kết quả cuối là Đã Like, false nếu là Đã Hủy Like
     */
    boolean toggleLike(Integer accountId, Integer commentId);
}