package poly.edu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import poly.edu.dao.CommentDAO;
import poly.edu.dto.AdminCommentDTO;
import poly.edu.dto.CommentDTO;
import poly.edu.entity.Comment;
import poly.edu.service.CommentService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentDAO commentDAO;
    private final poly.edu.dao.CommentLikeDAO commentLikeDAO;
    private final poly.edu.dao.AccountDAO accountDAO;

    private AdminCommentDTO toDTO(Comment c) {
        AdminCommentDTO dto = new AdminCommentDTO();
        dto.setCommentID(c.getCommentID());
        dto.setContent(c.getContent());

        dto.setUsername(c.getAccount().getUsername());
        dto.setPostTitle(c.getPost().getTitle());

        return dto;
    }

    private CommentDTO toCommentDTO(Comment c) {
        CommentDTO dto = new CommentDTO();
        dto.setCommentID(c.getCommentID());
        dto.setPostID(c.getPost() != null ? c.getPost().getPostID() : null);
        dto.setCmtid(c.getParentComment() != null ? c.getParentComment().getCommentID() : null);
        dto.setContent(c.getContent());
        if (c.getAccount() != null) {
            dto.setAccountID(c.getAccount().getAccountID());
            dto.setAuthorName(c.getAccount().getUsername());
            dto.setAuthorAvatar(c.getAccount().getAvatar());
        }
        dto.setImageUrls(c.getAttachments());
        dto.setLikeCount(commentLikeDAO.countByComment(c));
        
        // Cần biết user hiện tại là ai để set isLiked. 
        // Tuy nhiên hàm toCommentDTO hiện tại không có context này. 
        // Tạm thời để false, lát nữa sẽ xử lý ở tầng Service gọi DTO.
        dto.setIsLiked(false);
        
        dto.setCreatedAt(c.getCreatedAt());
        return dto;
    }

    @Override
    public int toggleLike(Integer accountID, Integer commentID) {
        Comment comment = commentDAO.findById(commentID)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        poly.edu.entity.Account account = accountDAO.findById(accountID)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        java.util.Optional<poly.edu.entity.CommentLike> existing = commentLikeDAO.findByAccountAndComment(account, comment);
        if (existing.isPresent()) {
            commentLikeDAO.delete(existing.get());
        } else {
            poly.edu.entity.CommentLike like = poly.edu.entity.CommentLike.builder()
                    .account(account)
                    .comment(comment)
                    .build();
            commentLikeDAO.save(like);
        }
        return commentLikeDAO.countByComment(comment);
    }

    @Override
    public List<AdminCommentDTO> findAll() {
        return commentDAO.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Integer id) {
        commentDAO.deleteById(id);
    }

    @Override
    public List<CommentDTO> getCommentsByPost(Integer postID) {
        // Lấy danh sách flat
        List<Comment> entities = commentDAO.findByPost_PostID(postID);
        
        // Chuyển sang DTO
        List<CommentDTO> flat = entities.stream()
                .map(this::toCommentDTO)
                .collect(Collectors.toList());

        Map<Integer, CommentDTO> byId = flat.stream()
                .collect(Collectors.toMap(CommentDTO::getCommentID, Function.identity()));

        Map<Integer, List<CommentDTO>> childrenMap = new HashMap<>();
        for (CommentDTO dto : flat) {
            if (dto.getCmtid() != null) {
                childrenMap.computeIfAbsent(dto.getCmtid(), k -> new ArrayList<>()).add(dto);
            }
        }

        List<CommentDTO> roots = new ArrayList<>();
        for (CommentDTO dto : flat) {
            if (dto.getCmtid() == null || !byId.containsKey(dto.getCmtid())) {
                roots.add(dto);
            }
        }

        for (CommentDTO root : roots) {
            attachChildren(root, childrenMap);
        }

        return roots;
    }

    private void attachChildren(CommentDTO parent, Map<Integer, List<CommentDTO>> childrenMap) {
        List<CommentDTO> children = childrenMap.get(parent.getCommentID());
        if (children == null || children.isEmpty()) {
            return;
        }
        parent.setChildren(children);
        for (CommentDTO child : children) {
            attachChildren(child, childrenMap);
        }
    }
}
