package poly.edu.dao;

import poly.edu.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentDAO extends JpaRepository<Comment, Integer> {

    List<Comment> findByPost_PostID(Integer postID);

    long countByPost_PostID(Integer postID);

    @Query("SELECT c.post.postID, COUNT(c) FROM Comment c WHERE c.post.postID IN :postIds GROUP BY c.post.postID")
    List<Object[]> countByPostIds(@Param("postIds") List<Integer> postIds);
}
