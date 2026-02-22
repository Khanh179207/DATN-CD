package poly.edu.service;


import poly.edu.dto.AdminErrorDTO;
import java.util.List;

public interface AdminErrorService {
    List<AdminErrorDTO> findAll();
    void delete(Integer id);
}
