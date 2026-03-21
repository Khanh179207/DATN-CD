package poly.edu.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import poly.edu.entity.Comment;
import poly.edu.entity.CommentLike;
import java.util.Optional;

public interface CommentLikeDAO extends JpaRepository<CommentLike, Integer> {

    @Query("SELECT cl FROM CommentLike cl WHERE cl.account.accountID = :accId AND cl.comment.commentID = :cmtId")
    Optional<CommentLike> findByAccountAndComment(@Param("accId") Integer accId, @Param("cmtId") Integer cmtId);

    // BỔ SUNG HÀM NÀY ĐỂ FIX LỖI "cannot find symbol"
    long countByComment(Comment comment);
}