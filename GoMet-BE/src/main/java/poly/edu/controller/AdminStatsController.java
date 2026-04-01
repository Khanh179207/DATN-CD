package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import poly.edu.dao.*;
import poly.edu.entity.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/stats")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")

public class AdminStatsController {

    private final AccountDAO accountDAO;
    private final PostDAO postDAO;
    private final TicketDAO ticketDAO;
    private final BlacklistWordDAO blacklistWordDAO;
    private final PaymentTransactionDAO paymentTransactionDAO;

    // 🔥 Đã khai báo thêm các DAO để lấy số liệu thật 100%
    private final CommentDAO commentDAO;
    private final CategoryDAO categoryDAO;
    private final EventDAO eventDAO;
    private final AppealDAO appealDAO; // Thêm dòng này vào cụm private final...

    /**
     * 1. API TỔNG QUAN HỆ THỐNG (Overview)
     */
    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getOverviewStats() {
        Map<String, Object> stats = new HashMap<>();

        long premiumUsers = accountDAO.countByIsPremium(1);
        long totalUsers = accountDAO.count();

        // System Tab
        stats.put("totalUsers", totalUsers);
        stats.put("totalPosts", postDAO.countByIsApprovedAndIsActive(1, 1));
        stats.put("premiumUsers", premiumUsers);

        Long totalViews = postDAO.getTotalViews();
        stats.put("totalViews", totalViews != null ? totalViews : 0L);

        // Revenue Tab (Đã gỡ ARPU)
        Long dbRevenue = paymentTransactionDAO.getTotalRevenue();
        stats.put("revenue", dbRevenue != null ? dbRevenue : 0L);
        stats.put("transactions", paymentTransactionDAO.countSuccessfulTransactions());
        stats.put("refunds", paymentTransactionDAO.countFailedTransactions());

        // Content Tab (Dữ liệu thật 100%)
        stats.put("categories", categoryDAO.count());
        stats.put("comments", commentDAO.count());
        stats.put("events", eventDAO.count());

        // Thay Achievements thành Total Likes
        Long totalLikes = postDAO.getTotalLikes();
        stats.put("totalLikes", totalLikes != null ? totalLikes : 0L);

        // Security Tab
        stats.put("totalTickets", ticketDAO.count());

        // 🔥 Đã móc sang bảng Appeal thật
        stats.put("appeals", appealDAO.count());
        stats.put("blacklist", blacklistWordDAO.count());

        // 🔥 Đã dùng câu Query tự viết
        stats.put("banned", accountDAO.countBannedAccounts());

        return ResponseEntity.ok(stats);
    }

    /**
     * 2. API BIỂU ĐỒ TĂNG TRƯỞNG (Growth Chart)
     */
    @GetMapping("/growth")
    public ResponseEntity<Map<String, Object>> getGrowthStats() {
        Map<String, Object> result = new HashMap<>();
        int currentYear = LocalDate.now().getYear();

        List<Object[]> postStats = postDAO.countPostsByMonth(currentYear);
        List<Object[]> userStats = accountDAO.countUsersByMonth(currentYear);

        long[] postsData = new long[12];
        long[] usersData = new long[12];

        postStats.forEach(row -> postsData[(Integer) row[0] - 1] = ((Number) row[1]).longValue());
        userStats.forEach(row -> usersData[(Integer) row[0] - 1] = ((Number) row[1]).longValue());

        result.put("labels", Arrays.asList("Th1", "Th2", "Th3", "Th4", "Th5", "Th6", "Th7", "Th8", "Th9", "Th10", "Th11", "Th12"));
        result.put("posts", postsData);
        result.put("users", usersData);

        return ResponseEntity.ok(result);
    }

    /**
     * 3. API BẢNG XẾP HẠNG TOP 5 (Top Ranking)
     */
    @GetMapping("/top-ranking")
    public ResponseEntity<Map<String, Object>> getTopRanking() {
        Map<String, Object> result = new HashMap<>();

        List<Object[]> topCreators = accountDAO.findTopCreators();
        List<Map<String, Object>> topChefsList = topCreators.stream().map(row -> {
            Map<String, Object> map = new HashMap<>();
            map.put("name", row[0]);
            map.put("postCount", row[1]);
            map.put("avatar", row[2] != null ? row[2] : "");
            return map;
        }).collect(Collectors.toList());

        List<Post> topPostsDb = postDAO.findTop5ByIsActiveAndIsApprovedOrderByViewsDesc(1, 1);
        List<Map<String, Object>> topPostsList = topPostsDb.stream().map(p -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", p.getPostID());
            map.put("title", p.getTitle());
            map.put("views", p.getViews());
            map.put("author", p.getAccount().getUsername());
            return map;
        }).collect(Collectors.toList());

        result.put("topChefs", topChefsList);
        result.put("topPosts", topPostsList);

        return ResponseEntity.ok(result);
    }

    /**
     * 4. API PHÂN TÍCH TICKET (Donut Chart)
     */
    @GetMapping("/tickets-summary")
    public ResponseEntity<Map<String, Long>> getTicketSummary() {
        List<Object[]> summaryRaw = ticketDAO.countTicketsByType();
        Map<String, Long> summary = new HashMap<>();

        for (Object[] row : summaryRaw) {
            String type = row[0] != null ? row[0].toString() : "Khác";
            Long count = ((Number) row[1]).longValue();
            summary.put(type, count);
        }

        return ResponseEntity.ok(summary);
    }

    /**
     * 5. API BIỂU ĐỒ DOANH THU (Revenue Area Chart)
     */
    @GetMapping("/revenue-chart")
    public ResponseEntity<List<Long>> getRevenueChart() {
        int currentYear = LocalDate.now().getYear();
        List<Object[]> monthlyRevenueDb = paymentTransactionDAO.getRevenueByMonth(currentYear);

        long[] revenueData = new long[12];
        for (Object[] row : monthlyRevenueDb) {
            int month = (Integer) row[0];
            long amount = ((Number) row[1]).longValue();
            revenueData[month - 1] = amount;
        }

        List<Long> result = new ArrayList<>();
        for (long amt : revenueData) result.add(amt);

        return ResponseEntity.ok(result);
    }

    /**
     * 🔥 6. API PHÂN BỔ DANH MỤC (Radar Chart)
     */
    @GetMapping("/category-distribution")
    public ResponseEntity<Map<String, Object>> getCategoryDistribution() {
        List<Object[]> dbData = postDAO.countPostsByCategory();

        List<String> labels = new ArrayList<>();
        List<Long> data = new ArrayList<>();

        for (Object[] row : dbData) {
            labels.add(row[0] != null ? row[0].toString() : "Khác");
            data.add(((Number) row[1]).longValue());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("labels", labels);
        result.put("data", data);

        return ResponseEntity.ok(result);
    }

    /**
     * 🔥 7. API TƯƠNG TÁC CỘNG ĐỒNG (Stacked Bar Chart)
     */
    @GetMapping("/engagement")
    public ResponseEntity<Map<String, Object>> getEngagement() {
        int currentYear = LocalDate.now().getYear();

        List<Object[]> likesDb = postDAO.sumLikesByMonth(currentYear);
        List<Object[]> commentsDb = commentDAO.countCommentsByMonth(currentYear);

        long[] likesData = new long[12];
        long[] commentsData = new long[12];

        for (Object[] row : likesDb) {
            if (row[0] != null && row[1] != null) likesData[(Integer) row[0] - 1] = ((Number) row[1]).longValue();
        }
        for (Object[] row : commentsDb) {
            if (row[0] != null && row[1] != null) commentsData[(Integer) row[0] - 1] = ((Number) row[1]).longValue();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("labels", Arrays.asList("Th1", "Th2", "Th3", "Th4", "Th5", "Th6", "Th7", "Th8", "Th9", "Th10", "Th11", "Th12"));
        result.put("likes", likesData);
        result.put("comments", commentsData);

        return ResponseEntity.ok(result);
    }

    /**
     * 🔥 8. API BIỂU ĐỒ KHIẾU NẠI (Appeals Bar Chart) - Đã đổi sang bảng Appeal
     */
    @GetMapping("/appeals-chart")
    public ResponseEntity<Map<String, Object>> getAppealsChart() {
        // 🔥 Gọi từ AppealDAO thay vì TicketDAO
        List<Object[]> dbData = appealDAO.countAppealsByStatus();

        Map<String, Long> statusMap = new HashMap<>();
        statusMap.put("Chờ Xử Lý", 0L);
        statusMap.put("Đang Duyệt", 0L);
        statusMap.put("Chấp Nhận", 0L);
        statusMap.put("Từ Chối", 0L);

        for (Object[] row : dbData) {
            String dbStatus = row[0] != null ? row[0].toString().toUpperCase() : "";
            Long count = ((Number) row[1]).longValue();

            // Dịch trạng thái từ DB của sếp sang nhãn của Frontend
            switch (dbStatus) {
                case "PENDING":
                    statusMap.put("Chờ Xử Lý", count);
                    break;
                case "IN_PROGRESS":
                    statusMap.put("Đang Duyệt", count);
                    break;
                case "APPROVED":
                case "ACCEPTED":
                case "RESOLVED":
                    statusMap.put("Chấp Nhận", count);
                    break;
                case "REJECTED":
                case "CLOSED":
                    statusMap.put("Từ Chối", count);
                    break;
                default:
                    statusMap.put("Chờ Xử Lý", statusMap.get("Chờ Xử Lý") + count);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("labels", Arrays.asList("Chờ Xử Lý", "Đang Duyệt", "Chấp Nhận", "Từ Chối"));
        result.put("data", Arrays.asList(
                statusMap.get("Chờ Xử Lý"),
                statusMap.get("Đang Duyệt"),
                statusMap.get("Chấp Nhận"),
                statusMap.get("Từ Chối")
        ));

        return ResponseEntity.ok(result);
    }
}