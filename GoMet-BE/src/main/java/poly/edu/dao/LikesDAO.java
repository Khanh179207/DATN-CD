package poly.edu.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import poly.edu.entity.Likes;
import java.util.List;
import java.util.Optional;

public interface LikesDAO extends JpaRepository<Likes, Integer> {

    // 1. Tìm xem user đã like bài này chưa (để Toggle)
    Optional<Likes> findByAccount_AccountIDAndPost_PostID(Integer accountId, Integer postId);

    // 2. Lấy danh sách người đã like (đã có)
    List<Likes> findByPost_PostIDOrderByCreatedAtDesc(Integer postId);

    // 3. Trả về true/false (đã có)
    boolean existsByAccount_AccountIDAndPost_PostID(Integer accountId, Integer postId);

    // 🔥 4. BỔ SUNG DÒNG NÀY: Để lấy con số hiển thị trên Card (Ví dụ: 10 likes)
    long countByPost_PostID(Integer postId);
}