package poly.edu.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import poly.edu.entity.ModerationAction;
import poly.edu.entity.ModerationActionType;

import java.util.List;

public interface ModerationActionDAO extends JpaRepository<ModerationAction, Long> {

    List<ModerationAction> findByPost_PostIDOrderByCreatedAtDesc(Integer postId);

    Page<ModerationAction> findByAdmin_AccountIDOrderByCreatedAtDesc(Integer adminId, Pageable pageable);

    @Query("SELECT ma FROM ModerationAction ma WHERE ma.post.postID = :postId ORDER BY ma.createdAt DESC")
    List<ModerationAction> findTimelineForPost(@Param("postId") Integer postId);

    long countByPost_PostIDAndAction(Integer postId, ModerationActionType action);
}
