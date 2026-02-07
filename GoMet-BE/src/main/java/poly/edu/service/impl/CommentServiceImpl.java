package poly.edu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import poly.edu.dao.CommentDAO;
import poly.edu.dto.AdminCommentDTO;
import poly.edu.entity.Comment;
import poly.edu.service.CommentService;
import java.util.List;
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
}
