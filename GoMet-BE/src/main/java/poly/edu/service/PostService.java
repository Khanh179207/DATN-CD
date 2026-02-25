package poly.edu.service;

import poly.edu.dto.AdminPostDTO;
import java.util.List;

public interface PostService {
    List<AdminPostDTO> findAll();
    List<AdminPostDTO> findByApproved(Integer isApproved);
    void approvePost(Integer id);
    void deactivePost(Integer id);
    void deletePost(Integer id);
}
