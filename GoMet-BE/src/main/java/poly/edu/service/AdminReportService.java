package poly.edu.service;

import poly.edu.entity.Report;
import java.util.List;

public interface AdminReportService {

    List<Report> findAll();

    void deleteReport(Integer reportID);

    void deletePostAndHandleReport(Integer postID);
}
