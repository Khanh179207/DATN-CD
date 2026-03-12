package poly.edu.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import poly.edu.dao.EmailJobRecipientDAO;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Rate limiting for admin email sends.
 * Uses the admin_email_rate_events table (DB-based, no Redis required).
 *
 * Limits: 100 emails/day and 20 emails/minute (configurable).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminRateLimitService {

    private final EmailJobRecipientDAO recipientDAO;

    @Value("${admin.email.rate.daily:100}")
    private int dailyLimit;

    @Value("${admin.email.rate.minute:20}")
    private int minuteLimit;

    /**
     * Check and record a rate-limit event for the given admin.
     * @throws ResponseStatusException 429 if limit exceeded.
     */
    public void checkAndRecord(Integer adminId) {
        Instant now      = Instant.now();
        Instant dayAgo   = now.minus(1, ChronoUnit.DAYS);
        Instant minuteAgo = now.minus(1, ChronoUnit.MINUTES);

        long dayCount;
        long minuteCount;
        try {
            dayCount = recipientDAO.countRateEvents(adminId, dayAgo);
            minuteCount = recipientDAO.countRateEvents(adminId, minuteAgo);
        } catch (DataAccessException e) {
            return;
        }

        if (dayCount >= dailyLimit) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                "Daily email send limit (" + dailyLimit + ") reached. Try again tomorrow.");
        }
        if (minuteCount >= minuteLimit) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                "Rate limit: max " + minuteLimit + " email sends per minute.");
        }

        // Record the event
        try {
            recipientDAO.insertRateEvent(adminId);
        } catch (DataAccessException e) {
        }
    }
}
