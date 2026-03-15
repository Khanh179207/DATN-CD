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
        dto.setCreatedAt(c.getCreatedAt());
        return dto;
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
        List<CommentDTO> flat = commentDAO.findByPost_PostID(postID).stream()
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
