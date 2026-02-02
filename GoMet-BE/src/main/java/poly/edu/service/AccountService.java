package poly.edu.service;

import poly.edu.dto.AdminAccountDTO;

import java.util.List;

public interface AccountService {
    List<AdminAccountDTO> findAll();
    AdminAccountDTO findById(Integer id);
    AdminAccountDTO save(AdminAccountDTO dto);
    void delete(Integer id);          // delete bang deactivate
    void hardDelete(Integer id);      // xóa thật
}
