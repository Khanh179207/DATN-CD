package poly.edu.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import poly.edu.dto.AdminPostDTO;

public interface PostService {
    Page<AdminPostDTO> findAll(Pageable pageable);
    Page<AdminPostDTO> findByApproved(Integer isApproved, Pageable pageable);
    void approvePost(Integer id);
    void deactivePost(Integer id);
    void deletePost(Integer id);
}
