package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import poly.edu.dto.AdminErrorDTO;
import poly.edu.service.AdminErrorService;
import java.util.List;

@RestController
@RequestMapping("/api/admin/errors")
@RequiredArgsConstructor
@CrossOrigin
public class AdminErrorController {

    private final AdminErrorService adminErrorService;

    @GetMapping
    public List<AdminErrorDTO> getAll() {
        return adminErrorService.findAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        adminErrorService.delete(id);
    }
}