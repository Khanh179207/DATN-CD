package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poly.edu.dto.AdminAccountDTO;
import poly.edu.service.AccountService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/accounts")
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

    /** Ban an account (set isActive = 0) */
    @PatchMapping("/{id}/ban")
    public ResponseEntity<?> ban(@PathVariable Integer id) {
        accountService.ban(id);
        return ResponseEntity.ok(Map.of("message", "Account banned successfully"));
    }

    /** Unban an account (set isActive = 1) */
    @PatchMapping("/{id}/unban")
    public ResponseEntity<?> unban(@PathVariable Integer id) {
        accountService.unban(id);
        return ResponseEntity.ok(Map.of("message", "Account unbanned successfully"));
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
