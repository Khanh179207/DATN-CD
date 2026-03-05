package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import poly.edu.dto.WeeklyCertificateDTO;
import poly.edu.service.WeeklyCertificateService;

import java.util.List;

@RestController
@RequestMapping("/api/certificates")
@RequiredArgsConstructor
public class CertificateController {

    private final WeeklyCertificateService weeklyCertificateService;

    @GetMapping("/me")
    public ResponseEntity<?> myCertificates(Authentication authentication,
                                            @RequestParam(required = false) Integer year) {
        if (authentication == null || !(authentication.getPrincipal() instanceof Integer accountId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(java.util.Map.of("message", "Authentication required"));
        }
        List<WeeklyCertificateDTO> certificates = weeklyCertificateService.getMyCertificates(accountId, year);
        return ResponseEntity.ok(certificates);
    }
}
