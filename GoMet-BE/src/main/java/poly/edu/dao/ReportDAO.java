package poly.edu.dao;

import poly.edu.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportDAO extends JpaRepository<Report, Integer> {

}
    List<Report> findByPost_PostID(Integer postID);

    void deleteByPost_PostID(Integer postID);
}
