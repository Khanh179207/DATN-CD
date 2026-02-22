package poly.edu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poly.edu.dao.NotificationDAO;
import poly.edu.dao.PostDAO;
import poly.edu.dao.ReportDAO;
import poly.edu.entity.Notification;
import poly.edu.entity.Post;
import poly.edu.entity.Report;
import poly.edu.service.AdminReportService;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminReportServiceImpl implements AdminReportService {

    private final ReportDAO reportDAO;
    private final PostDAO postDAO;
    private final NotificationDAO notificationDAO;

    @Override
    public List<Report> findAll() {
        return reportDAO.findAll();
    }

    @Override
    public void deleteReport(Integer reportID) {
        reportDAO.deleteById(reportID);
    }

    @Override
    @Transactional
    public void deletePostAndHandleReport(Integer postID) {

        // 1️⃣ Lấy bài viết
        Post post = postDAO.findById(postID)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // 2️⃣ Lấy tất cả report của bài đó
        List<Report> reports = reportDAO.findByPost_PostID(postID);

        // 3️⃣ Tạo notification cho từng người report
        for (Report report : reports) {

            Notification notification = Notification.builder()
                    .title("Bài viết đã bị xóa")
                    .content("Bài viết bạn báo cáo đã được admin xem xét và xóa.")
                    .type("REPORT_RESULT")
                    .account(report.getAccount())
                    .post(post)
                    .isRead(0)
                    .createdAt(LocalDate.now())
                    .build();

            notificationDAO.save(notification);
        }

        // 4️⃣ Xóa tất cả report
        reportDAO.deleteByPost_PostID(postID);

        // 5️⃣ Soft delete bài viết
        post.setIsActive(0);
        postDAO.save(post);
    }
}