package poly.edu.service;

import poly.edu.dto.AdminAccountDTO;

import java.util.List;

public interface AccountService {
    List<AdminAccountDTO> findAll();
    AdminAccountDTO findById(Integer id);
    AdminAccountDTO save(AdminAccountDTO dto);
    void ban(Integer id);
    void unban(Integer id);
    void delete(Integer id);
    void hardDelete(Integer id);
}
