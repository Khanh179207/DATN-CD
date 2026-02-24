package poly.edu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import poly.edu.dao.ErrorDAO;
import poly.edu.dto.AdminErrorDTO;
import poly.edu.entity.Error;
import poly.edu.service.AdminErrorService;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminErrorServiceImpl implements AdminErrorService {

    private final ErrorDAO errorDAO;

    private AdminErrorDTO toDTO(Error e) {
        return AdminErrorDTO.builder()
                .errorID(e.getErrorID())
                .errorName(e.getErrorName())
                .description(e.getDescription())
                .createdAt(e.getCreatedAt())
                .username(
                        e.getAccount() != null
                                ? e.getAccount().getUsername()
                                : null
                )
                .build();
    }

    @Override
    public List<AdminErrorDTO> findAll() {
        return errorDAO.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public void delete(Integer id) {
        errorDAO.deleteById(id);
    }
}