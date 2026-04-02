package poly.edu.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import poly.edu.dto.EventPostDTO;
import poly.edu.entity.EventPosts;
import java.util.List;

public interface EventPostsDAO extends JpaRepository<EventPosts, Integer> {

    // 1. Lấy danh sách Entity (Dùng cho logic nội bộ)
    List<EventPosts> findByEvent_EventID(Integer eventId);

    // 2. Lấy danh sách DTO bài thi (8 trường sạch sẽ)
    @Query("SELECT new poly.edu.dto.EventPostDTO(" +
            "ep.eventPostID, p.postID, p.title, p.media, a.username, a.avatar, ep.voteCount, ep.createdAt) " +
            "FROM EventPosts ep " +
            "JOIN ep.post p " +
            "JOIN p.account a " +
            "WHERE ep.event.eventID = :eventId")
    List<EventPostDTO> findPostsByEventId(@Param("eventId") Integer eventId);

    // 3. Check xem bài viết cụ thể đã nộp chưa (Giữ lại để sơ phòng)
    @Query("SELECT COUNT(ep) > 0 FROM EventPosts ep WHERE ep.event.eventID = :eventId AND ep.post.postID = :postId")
    boolean existsByEventIdAndPostId(@Param("eventId") Integer eventId, @Param("postId") Integer postId);

    // 4. 🔥 LOGIC MỚI: Check xem Tác giả (Author) đã nộp bài nào cho sự kiện này chưa
    // Nếu kết quả > 0 nghĩa là sếp này đã nộp bài rồi, không cho nộp thêm bài khác nữa.
    @Query("SELECT COUNT(ep) > 0 FROM EventPosts ep " +
            "WHERE ep.event.eventID = :eventId " +
            "AND ep.post.account.accountID = :accountId")
    boolean existsByEventIdAndAuthorId(@Param("eventId") Integer eventId, @Param("accountId") Integer accountId);
}