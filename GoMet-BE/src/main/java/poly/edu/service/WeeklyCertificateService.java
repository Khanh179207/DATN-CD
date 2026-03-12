package poly.edu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poly.edu.dao.AccountDAO;
import poly.edu.dao.HistoryDAO;
import poly.edu.dao.WeeklyCertificateDAO;
import poly.edu.dto.WeeklyCertificateDTO;
import poly.edu.dto.WeeklyLeaderboardEntryDTO;
import poly.edu.entity.Account;
import poly.edu.entity.WeeklyCertificate;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WeeklyCertificateService {

    private static final Random RANDOM = new Random();

    private final HistoryDAO historyDAO;
    private final AccountDAO accountDAO;
    private final WeeklyCertificateDAO weeklyCertificateDAO;

    public List<WeeklyLeaderboardEntryDTO> getWeeklyLeaderboard(LocalDate weekStart, LocalDate weekEnd, int top) {
        validateRange(weekStart, weekEnd);
        int safeTop = Math.max(1, Math.min(top, 100));
        LocalDateTime startAt = weekStart.atStartOfDay();
        LocalDateTime endExclusive = weekEnd.plusDays(1).atStartOfDay();

        List<Object[]> rows = historyDAO.findTopPostAuthorsByViewsBetween(startAt, endExclusive);
        List<WeeklyLeaderboardEntryDTO> leaderboard = new ArrayList<>();
        int rank = 1;
        for (Object[] row : rows) {
            if (rank > safeTop) {
                break;
            }
            Integer userId = ((Number) row[0]).intValue();
            Integer score = ((Number) row[1]).intValue();
            Account account = accountDAO.findById(userId).orElse(null);
            if (account == null || !account.isAccountActive()) {
                continue;
            }
            leaderboard.add(WeeklyLeaderboardEntryDTO.builder()
                    .userId(account.getAccountID())
                    .username(account.getUsername())
                    .avatar(account.getAvatar())
                    .score(score)
                    .rank(rank++)
                    .build());
        }

        return leaderboard;
    }

    @Transactional
    public List<WeeklyCertificateDTO> generateCertificates(LocalDate weekStart, LocalDate weekEnd, int top) {
        if (top <= 0) {
            throw new IllegalArgumentException("top must be greater than 0");
        }
        List<WeeklyLeaderboardEntryDTO> leaderboard = getWeeklyLeaderboard(weekStart, weekEnd, top);
        List<WeeklyCertificateDTO> generated = new ArrayList<>();

        for (WeeklyLeaderboardEntryDTO entry : leaderboard) {
            Optional<WeeklyCertificate> existed = weeklyCertificateDAO.findByWeekStartAndUser_AccountID(weekStart, entry.getUserId());
            WeeklyCertificate cert;
            if (existed.isPresent()) {
                cert = existed.get();
            } else {
                Account account = accountDAO.findById(entry.getUserId())
                        .orElseThrow(() -> new IllegalArgumentException("User not found: " + entry.getUserId()));

                cert = WeeklyCertificate.builder()
                        .weekStart(weekStart)
                        .weekEnd(weekEnd)
                        .user(account)
                        .rank(entry.getRank())
                        .score(entry.getScore())
                        .title("Top " + entry.getRank() + " GOMET Weekly Chef")
                        .issuedAt(LocalDateTime.now())
                        .certificateCode(generateUniqueCode(weekStart, entry.getUserId(), entry.getRank()))
                        .build();
            }
            cert.setRank(entry.getRank());
            cert.setScore(entry.getScore());
            cert.setTitle("Top " + entry.getRank() + " GOMET Weekly Chef");
            cert.setWeekEnd(weekEnd);

            WeeklyCertificate saved = weeklyCertificateDAO.save(cert);
            generated.add(toDto(saved));
        }

        return generated;
    }

    public List<WeeklyCertificateDTO> getMyCertificates(Integer accountId, Integer year) {
        List<WeeklyCertificate> list;
        if (year != null) {
            LocalDate from = LocalDate.of(year, 1, 1);
            LocalDate to = LocalDate.of(year, 12, 31);
            list = weeklyCertificateDAO.findByUser_AccountIDAndWeekStartBetweenOrderByWeekStartDesc(accountId, from, to);
        } else {
            list = weeklyCertificateDAO.findByUser_AccountIDOrderByWeekStartDesc(accountId);
        }
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<WeeklyCertificateDTO> getAdminCertificates(LocalDate weekStart, LocalDate weekEnd) {
        validateRange(weekStart, weekEnd);
        return weeklyCertificateDAO.findByWeekStartAndWeekEndOrderByRankAsc(weekStart, weekEnd)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public List<WeeklyCertificateDTO> generateLastWeekCertificates(int top) {
        LocalDate now = LocalDate.now();
        LocalDate thisWeekStart = now.with(DayOfWeek.MONDAY);
        LocalDate weekStart = thisWeekStart.minusWeeks(1);
        LocalDate weekEnd = weekStart.plusDays(6);
        return generateCertificates(weekStart, weekEnd, top);
    }

    private WeeklyCertificateDTO toDto(WeeklyCertificate cert) {
        Account user = cert.getUser();
        return WeeklyCertificateDTO.builder()
                .id(cert.getId())
                .weekStart(cert.getWeekStart())
                .weekEnd(cert.getWeekEnd())
                .userId(user != null ? user.getAccountID() : null)
                .username(user != null ? user.getUsername() : null)
                .avatar(user != null ? user.getAvatar() : null)
                .rank(cert.getRank())
                .score(cert.getScore())
                .title(cert.getTitle())
                .issuedAt(cert.getIssuedAt())
                .certificateCode(cert.getCertificateCode())
                .build();
    }

    private String generateUniqueCode(LocalDate weekStart, Integer userId, Integer rank) {
        WeekFields weekFields = WeekFields.ISO;
        int week = weekStart.get(weekFields.weekOfWeekBasedYear());
        int year = weekStart.get(weekFields.weekBasedYear());

        for (int attempt = 0; attempt < 10; attempt++) {
            String suffix = String.format("%04d", RANDOM.nextInt(10_000));
            String code = String.format("GOMET-%d%02d-%d-%d-%s", year, week, userId, rank, suffix);
            if (!weeklyCertificateDAO.existsByCertificateCode(code)) {
                return code;
            }
        }

        return String.format("GOMET-%d%02d-%d-%d-%d", year, week, userId, rank, System.currentTimeMillis() % 100000);
    }

    private void validateRange(LocalDate weekStart, LocalDate weekEnd) {
        if (weekStart == null || weekEnd == null) {
            throw new IllegalArgumentException("weekStart and weekEnd are required");
        }
        if (weekEnd.isBefore(weekStart)) {
            throw new IllegalArgumentException("weekEnd must be on or after weekStart");
        }
    }
}
