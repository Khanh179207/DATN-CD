package poly.edu.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WeeklyCertificateScheduler {

    private final WeeklyCertificateService weeklyCertificateService;

    @Scheduled(cron = "${certificates.weekly.cron:0 5 0 * * MON}")
    public void generateLastWeekCertificates() {
        try {
            weeklyCertificateService.generateLastWeekCertificates(3);
        } catch (Exception ex) {
            log.error("Failed to generate weekly certificates", ex);
        }
    }
}
