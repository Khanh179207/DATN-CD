package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import poly.edu.dto.AdminAccountDTO;
import poly.edu.service.AccountService;
import java.util.List;

@RestController
@RequestMapping("/api/admin/accounts")
@RequiredArgsConstructor
@CrossOrigin
public class AdminAccountController {

    private final AccountService accountService;

    @GetMapping
    public List<AdminAccountDTO> getAll() {
        return accountService.findAll();
    }

    @GetMapping("/{id}")
    public AdminAccountDTO getOne(@PathVariable Integer id) {
        return accountService.findById(id);
    }

    @PostMapping
    public AdminAccountDTO create(@RequestBody AdminAccountDTO dto) {
        return accountService.save(dto);
    }

    @PutMapping("/{id}")
    public AdminAccountDTO update(@PathVariable Integer id,
                                  @RequestBody AdminAccountDTO dto) {
        dto.setAccountID(id);
        return accountService.save(dto);
    }

    @DeleteMapping("/{id}")
    public void softDelete(@PathVariable Integer id) {
        accountService.delete(id);
    }

    @DeleteMapping("/hard/{id}")
    public void hardDelete(@PathVariable Integer id) {
        accountService.hardDelete(id);
    }
}
