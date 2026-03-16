package poly.edu.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import poly.edu.entity.Account;
import poly.edu.entity.Comment;
import poly.edu.entity.CommentLike;
import java.util.Optional;

public interface CommentLikeDAO extends JpaRepository<CommentLike, Integer> {
    Optional<CommentLike> findByAccountAndComment(Account account, Comment comment);
    int countByComment(Comment comment);
    boolean existsByAccountAndComment(Account account, Comment comment);
}
