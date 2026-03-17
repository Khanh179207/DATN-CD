package poly.edu.service;

import poly.edu.dto.AdminPostDTO;
import java.util.List;

public interface AdminPostService {
    List<AdminPostDTO> findAll();

    List<AdminPostDTO> findByApproved(Integer isApproved);

    void approvePost(Integer id);

    void rejectPost(Integer id, String reason);

    void deactivePost(Integer id);

    void deletePost(Integer id);

    void banAuthorByPostId(Integer postId);
}