package poly.edu.service;

import poly.edu.dto.ReportSummaryDTO;
import poly.edu.entity.Report;

import java.util.List;

public interface AdminReportService {

    /** @deprecated Use {@link #findAllSummaries()} — avoids N+1 on Post collections. */
    @Deprecated
    List<Report> findAll();

    /** Returns lightweight DTO list — single JOIN query, no N+1. */
    List<ReportSummaryDTO> findAllSummaries();

    void deleteReport(Integer reportID);

    void deletePostAndHandleReport(Integer postID);
}
