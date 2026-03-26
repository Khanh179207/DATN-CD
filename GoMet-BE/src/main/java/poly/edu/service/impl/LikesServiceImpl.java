package poly.edu.service.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poly.edu.dao.*;
import poly.edu.dto.LikesDTO;
import poly.edu.entity.*;
import poly.edu.service.LikesService;
import poly.edu.service.NotificationService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LikesServiceImpl implements LikesService {

    private final LikesDAO likesDAO;
    private final PostDAO postDAO;
    private final AccountDAO accountDAO;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public boolean toggleLike(Integer accountId, Integer postId) {
        Optional<Likes> existingLike = likesDAO.findByAccount_AccountIDAndPost_PostID(accountId, postId);

        if (existingLike.isPresent()) {
            // UNLIKE
            likesDAO.delete(existingLike.get());
            return false;
        } else {
            // LIKE
            Account actor = accountDAO.findById(accountId)
                    .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy Account"));

            Post post = postDAO.findById(postId)
                    .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy Post"));

            Likes newLike = new Likes();
            newLike.setAccount(actor);
            newLike.setPost(post);
            likesDAO.save(newLike);

            Integer ownerId = post.getAccount().getAccountID();

            // ❗ Không gửi cho chính mình
            if (!ownerId.equals(accountId)) {
                notificationService.notifyLike(
                        actor.getUsername(),
                        ownerId,
                        postId);
            }

            return true;
        }
    }

    @Override
    public List<LikesDTO> getLikesByPostId(Integer postId) {
        return likesDAO.findByPost_PostIDOrderByCreatedAtDesc(postId)
                .stream()
                .map(like -> new LikesDTO(
                        like.getLikeID(),
                        like.getAccount().getAccountID(),
                        like.getAccount().getUsername(),
                        like.getAccount().getAvatar(),
                        like.getPost().getPostID(),
                        like.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean checkUserLikedPost(Integer accountId, Integer postId) {
        return likesDAO.existsByAccount_AccountIDAndPost_PostID(accountId, postId);
    }

}