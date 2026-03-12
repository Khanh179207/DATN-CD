package poly.edu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import poly.edu.dao.PostDAO;
import poly.edu.dto.AdminPostDTO;
import poly.edu.entity.Post;
import poly.edu.entity.PostStatus;
import poly.edu.service.PostService;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostDAO postDAO;

    private AdminPostDTO toDTO(Post post) {
        AdminPostDTO dto = new AdminPostDTO();
        dto.setPostID(post.getPostID());
        dto.setTitle(post.getTitle());
        dto.setDescription(post.getDescription());
        dto.setMedia(post.getMedia());
        dto.setLevel(post.getLevel());
        dto.setCookingTime(post.getCookingTime());
        dto.setViews(post.getViews());
        dto.setIsActive(post.getIsActive());
        dto.setIsApproved(post.getIsApproved());
        dto.setCreatedAt(post.getCreatedAt());

        dto.setUsername(post.getAccount().getUsername());
        dto.setAccountID(post.getAccount().getAccountID());
        dto.setAccountAvatar(post.getAccount().getAvatar());
        dto.setCategoryName(post.getCategory().getCategoryName());
        dto.setCategoryID(post.getCategory().getCategoryID());

        return dto;
    }

    @Override
    public Page<AdminPostDTO> findAll(Pageable pageable) {
        return postDAO.findAllForAdmin(pageable).map(this::toDTO);
    }

    @Override
    public Page<AdminPostDTO> findByApproved(Integer isApproved, Pageable pageable) {
        return postDAO.findByIsApprovedForAdmin(isApproved, pageable).map(this::toDTO);
    }

    @Override
    public void approvePost(Integer id) {
        Post post = postDAO.findById(id).orElseThrow();
        post.setIsApproved(1);
        post.setIsActive(1);
        post.setStatus(PostStatus.APPROVED);
        post.setModeratedAt(Instant.now());
        post.setUpdatedAt(Instant.now());
        postDAO.save(post);
    }

    @Override
    public void deactivePost(Integer id) {
        Post post = postDAO.findById(id).orElseThrow();
        post.setIsActive(0);
        post.setStatus(PostStatus.HIDDEN);
        post.setUpdatedAt(Instant.now());
        postDAO.save(post);
    }

    @Override
    public void deletePost(Integer id) {
        postDAO.deleteById(id);
    }
}

