package poly.edu.service;

import poly.edu.dto.AdminCommentDTO;
import poly.edu.dto.CommentDTO;

import java.util.List;

public interface CommentService {
    List<AdminCommentDTO> findAll();

    void delete(Integer id);

    /**
     * Get all comments for a post, organized as a tree with replies nested under
     * their parents.
     */
    List<CommentDTO> getCommentsByPost(Integer postID);

    /**
     * Toggle like status for a comment by a specific account.
     * Returns the new like count.
     */
    int toggleLike(Integer accountID, Integer commentID);
}
