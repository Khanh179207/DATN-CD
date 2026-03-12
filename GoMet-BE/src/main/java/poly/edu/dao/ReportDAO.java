package poly.edu.dao;

import org.springframework.data.jpa.repository.Query;
import poly.edu.dto.ReportSummaryDTO;
import poly.edu.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportDAO extends JpaRepository<Report, Integer> {
    List<Report> findByPost_PostID(Integer postID);

    void deleteByPost_PostID(Integer postID);

    /**
     * Fetch report summaries as a lightweight DTO (single JOIN query).
     * Avoids N+1 caused by Hibernate eagerly loading Post's collections
     * (comments, likes, ingredients, etc.) when serialising full entities.
     */
    @Query("SELECT new poly.edu.dto.ReportSummaryDTO(" +
           "  r.reportID, a.username, p.postID, p.title, r.reason, r.createdAt) " +
           "FROM Report r JOIN r.account a JOIN r.post p " +
           "ORDER BY r.createdAt DESC")
    List<ReportSummaryDTO> findAllSummaries();
}
