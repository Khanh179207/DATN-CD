package poly.edu.dao;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import poly.edu.entity.Votes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface VoteDAO extends JpaRepository<Votes, Integer> {

    // Tìm xem User đã vote cho bài viết này chưa
    Optional<Votes> findByAccount_AccountIDAndEventPost_EventPostID(Integer accountID, Integer eventPostID);

    // Đếm số lượng bài user đã vote trong 1 sự kiện (Để check giới hạn maxVotes)
    @Query("SELECT COUNT(v) FROM Votes v " +
            "WHERE v.account.accountID = :accId " +
            "AND v.eventPost.event.eventID = :eventId")
    long countVotesByUserInEvent(@Param("accId") Integer accId, @Param("eventId") Integer eventId);

    // 🔥 BỔ SUNG THÊM HÀM NÀY: Đếm tổng số vote của 1 bài dự thi
    long countByEventPost_EventPostID(Integer eventPostID);

    @Modifying
    @Transactional
    @Query("DELETE FROM Votes v WHERE v.eventPost.eventPostID = :eventPostID")
    void deleteByEventPostID(@Param("eventPostID") Integer eventPostID);
}