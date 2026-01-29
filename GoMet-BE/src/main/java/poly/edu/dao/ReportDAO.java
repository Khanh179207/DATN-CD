package poly.edu.dao;

import poly.edu.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportDAO extends JpaRepository<Report, Integer> {
}
