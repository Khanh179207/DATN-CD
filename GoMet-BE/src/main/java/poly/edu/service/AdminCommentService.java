package poly.edu.service;

import poly.edu.dto.AdminCommentDTO;

import java.util.List;

public interface AdminCommentService {
    List<AdminCommentDTO> findAll();
    void delete(Integer id);
}
