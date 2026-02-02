package poly.edu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import poly.edu.dao.AccountDAO;
import poly.edu.dto.AdminAccountDTO;
import poly.edu.entity.Account;
import poly.edu.service.AccountService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountDAO accountDAO;

    private AdminAccountDTO toDTO(Account acc) {
        AdminAccountDTO dto = new AdminAccountDTO();
        dto.setAccountID(acc.getAccountID());
        dto.setUsername(acc.getUsername());
        dto.setEmail(acc.getEmail());
        dto.setPassword(acc.getPassword());
        dto.setAvatar(acc.getAvatar());
        dto.setIsPremium(acc.getIsPremium());
        dto.setIsActive(acc.getIsActive());
        return dto;
    }

    private Account toEntity(AdminAccountDTO dto) {
        return Account.builder()
                .accountID(dto.getAccountID())
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .avatar(dto.getAvatar())
                .isPremium(dto.getIsPremium())
                .isActive(dto.getIsActive())
                .isAdmin(0)
                .point(0)
                .token("NONE")
                .createdAt(LocalDate.now())
                .build();
    }

    @Override
    public List<AdminAccountDTO> findAll() {
        return accountDAO.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AdminAccountDTO findById(Integer id) {
        return toDTO(accountDAO.findById(id).orElseThrow());
    }

    @Override
    public AdminAccountDTO save(AdminAccountDTO dto) {
        Account acc = toEntity(dto);

        if (dto.getAccountID() != null) {
            acc.setUpdatedAt(LocalDate.now());
        }

        return toDTO(accountDAO.save(acc));
    }

    @Override
    public void delete(Integer id) {
        Account acc = accountDAO.findById(id).orElseThrow();
        acc.setIsActive(0);
        acc.setDeletedAt(LocalDate.now());
        accountDAO.save(acc);
    }

    @Override
    public void hardDelete(Integer id) {
        accountDAO.deleteById(id);
    }
}
