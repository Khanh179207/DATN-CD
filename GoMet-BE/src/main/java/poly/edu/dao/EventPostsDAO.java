package poly.edu.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import poly.edu.dto.EventPostDTO;
import poly.edu.entity.EventPosts;
import java.util.List;

public interface EventPostsDAO extends JpaRepository<EventPosts, Integer> {

    // Lấy bài dự thi theo EventID (Trả về Entity)
    List<EventPosts> findByEvent_EventID(Integer eventId);

    // 🔥 API lấy danh sách bài dự thi kèm thông tin tác giả (Hàm sếp gọi ở Controller)
    @Query("SELECT new poly.edu.dto.EventPostDTO(ep.eventPostID, p.postID, p.title, p.media, a.username, a.avatar, ep.voteCount, ep.createdAt) " +
            "FROM EventPosts ep " +
            "JOIN ep.post p " +
            "JOIN p.account a " +
            "WHERE ep.event.eventID = :eventId")
    List<EventPostDTO> findPostsByEventId(@Param("eventId") Integer eventId);

    boolean existsByEvent_EventIDAndPost_PostID(Integer eventId, Integer postId);
}