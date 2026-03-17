package poly.edu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import poly.edu.dao.PostDAO;
import poly.edu.dto.PostDTO;
import poly.edu.entity.Post;
import poly.edu.service.PostService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostDAO postDAO;

    // API Lấy danh sách bài viết của User để nộp sự kiện (Fix lỗi 404)
    @Override
    public List<PostDTO> getPostsByAccountId(Integer accountId) {
        // Lấy danh sách từ DB
        List<Post> posts = postDAO.findByAccount_AccountIDOrderByCreatedAtDesc(accountId);

        // Chuyển từ Entity sang DTO
        return posts.stream().map(post -> {
            PostDTO dto = new PostDTO();
            dto.setPostID(post.getPostID());
            dto.setTitle(post.getTitle());
            dto.setMedia(post.getMedia());
            dto.setVideo(post.getVideo());
            dto.setCreatedAt(post.getCreatedAt());
            dto.setViews(post.getViews());
            dto.setLikeCount(post.getLikeCount());
            // Map thêm accountID để Vue dùng nếu cần
            if (post.getAccount() != null) {
                dto.setAccountID(post.getAccount().getAccountID());
            }
            return dto;
        }).collect(Collectors.toList());
    }

    // Tạm thời để trống, sau khi sếp test xong cái danh sách mình sẽ viết logic tạo bài!
    @Override
    public PostDTO createPost(PostDTO postDTO) {
        return null;
    }

    // Tạm thời để trống
    @Override
    public PostDTO getPostById(Integer postId) {
        return null;
    }
}