package poly.edu.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import poly.edu.entity.WeeklyCertificate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WeeklyCertificateDAO extends JpaRepository<WeeklyCertificate, Integer> {

    boolean existsByCertificateCode(String certificateCode);

    List<WeeklyCertificate> findByUser_AccountIDOrderByWeekStartDesc(Integer accountId);

    List<WeeklyCertificate> findByUser_AccountIDAndWeekStartBetweenOrderByWeekStartDesc(Integer accountId, LocalDate from, LocalDate to);

    List<WeeklyCertificate> findByWeekStartAndWeekEndOrderByRankAsc(LocalDate weekStart, LocalDate weekEnd);

    Optional<WeeklyCertificate> findByWeekStartAndUser_AccountID(LocalDate weekStart, Integer accountId);
}
