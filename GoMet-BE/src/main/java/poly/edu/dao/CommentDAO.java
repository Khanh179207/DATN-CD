package poly.edu.dao;

import poly.edu.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentDAO extends JpaRepository<Comment, Integer> {

    List<Comment> findByPost_PostID(Integer postID);
}
