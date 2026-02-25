package poly.edu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import poly.edu.dao.PostDAO;
import poly.edu.dto.AdminPostDTO;
import poly.edu.entity.Post;
import poly.edu.service.PostService;
import java.util.List;
import java.util.stream.Collectors;

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
    public List<AdminPostDTO> findAll() {
        return postDAO.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AdminPostDTO> findByApproved(Integer isApproved) {
        return postDAO.findByIsApproved(isApproved)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void approvePost(Integer id) {
        Post post = postDAO.findById(id).orElseThrow();
        post.setIsApproved(1);
        postDAO.save(post);
    }

    @Override
    public void deactivePost(Integer id) {
        Post post = postDAO.findById(id).orElseThrow();
        post.setIsActive(0);
        postDAO.save(post);
    }

    @Override
    public void deletePost(Integer id) {
        postDAO.deleteById(id);
    }
}
