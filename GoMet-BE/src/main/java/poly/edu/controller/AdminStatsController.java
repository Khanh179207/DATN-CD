//package poly.edu.controller;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import poly.edu.dao.*;
//import poly.edu.entity.*;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@RestController
//@RequestMapping("/api/admin/stats") // Thêm /api cho đúng chuẩn rest
//@RequiredArgsConstructor
//@CrossOrigin("*")
//public class AdminStatsController {
//
//    private final AccountDAO accountDAO;
//    private final PostDAO postDAO;
//    private final TicketDAO ticketDAO;
//    private final NotificationDAO notificationDAO;
//    private final EventDAO eventDAO;
//    private final BlacklistWordDAO blacklistWordDAO; // 🔥 Thống kê bộ lọc mới
//    private final CommentDAO commentDAO;
//
//    /**
//     * 📊 TỔNG QUAN DỮ LIỆU (THE BIG NUMBERS)
//     * Dùng để hiển thị các con số "khủng" ở đầu trang Dashboard
//     */
//    @GetMapping("/overview")
//    public ResponseEntity<Map<String, Object>> getOverviewStats() {
//        Map<String, Object> stats = new HashMap<>();
//
//        // 1. User Stats
//        long totalUsers = accountDAO.count();
//        long premiumUsers = accountDAO.countByIsPremium(1); // Sếp nhớ thêm hàm này vào DAO
//        long bannedUsers = accountDAO.countByIsActive(0);
//
//        // 2. Content Stats
//        long totalPosts = postDAO.count();
//        // Giả sử sếp đã có các hàm count theo trạng thái trong PostDAO
//        long pendingPosts = postDAO.countByIsApprovedAndIsActive(0, 1);
//        long livePosts = postDAO.countByIsApprovedAndIsActive(1, 1);
//
//        // 3. System Health (Ticket & Moderation)
//        long totalTickets = ticketDAO.count();
//        long blacklistCount = blacklistWordDAO.count();
//
//        // 4. Engagement
//        // Dùng Query thủ công trong PostDAO: @Query("SELECT SUM(p.views) FROM Post p")
//        Long totalViews = postDAO.getTotalViews();
//
//        // 5. Business (Doanh thu ước tính)
//        long revenue = premiumUsers * 99000L;
//
//        stats.put("totalUsers", totalUsers);
//        stats.put("premiumUsers", premiumUsers);
//        stats.put("bannedUsers", bannedUsers);
//        stats.put("totalPosts", totalPosts);
//        stats.put("pendingPosts", pendingPosts);
//        stats.put("livePosts", livePosts);
//        stats.put("totalTickets", totalTickets);
//        stats.put("blacklistCount", blacklistCount);
//        stats.put("totalViews", totalViews != null ? totalViews : 0);
//        stats.put("revenue", revenue);
//        stats.put("serverStatus", "STABLE"); // Trạng thái ảo cho sang
//
//        return ResponseEntity.ok(stats);
//    }
//
//    /**
//     * 📈 BIỂU ĐỒ TĂNG TRƯỞNG (GROWTH CHARTS)
//     * Thống kê 12 tháng gần nhất
//     */
//    @GetMapping("/growth")
//    public ResponseEntity<Map<String, Object>> getGrowthStats() {
//        Map<String, Object> result = new HashMap<>();
//
//        // Logic lấy 12 tháng gần nhất
//        List<String> labels = new ArrayList<>();
//        List<Long> postCounts = new ArrayList<>();
//        List<Long> userCounts = new ArrayList<>();
//
//        for (int i = 11; i >= 0; i--) {
//            LocalDate target = LocalDate.now().minusMonths(i);
//            String monthLabel = target.getMonthValue() + "/" + target.getYear();
//            labels.add(monthLabel);
//
//            // Ở đây trong thực tế sếp nên dùng 1 query @Query duy nhất để Group By Month
//            // Nhưng để demo nhanh, sếp có thể tạm thời lấy dữ liệu thô rồi xử lý
//            postCounts.add((long) (Math.random() * 100)); // Demo dữ liệu ngẫu nhiên nếu sếp chưa viết Query Group By
//            userCounts.add((long) (Math.random() * 50));
//        }
//
//        result.put("labels", labels);
//        result.put("posts", postCounts);
//        result.put("users", userCounts);
//
//        return ResponseEntity.ok(result);
//    }
//
//    /**
//     * 🏆 BẢNG XẾP HẠNG (LEADERBOARDS)
//     * Top đầu bếp và Top bài viết
//     */
//    @GetMapping("/top-ranking")
//    public ResponseEntity<Map<String, Object>> getTopRanking() {
//        Map<String, Object> result = new HashMap<>();
//
//        // 1. Top bài viết xem nhiều nhất (Limit 5)
//        List<Post> topPosts = postDAO.findAll().stream()
//                .filter(p -> p.getIsApproved() == 1)
//                .sorted(Comparator.comparing(Post::getViews).reversed())
//                .limit(5)
//                .collect(Collectors.toList());
//
//        // 2. Top người dùng Premium tích cực
//        List<Account> topChefs = accountDAO.findAll().stream()
//                .filter(a -> a.getIsPremium() == 1)
//                .sorted((a, b) -> Integer.compare(b.getPosts().size(), a.getPosts().size()))
//                .limit(5)
//                .collect(Collectors.toList());
//
//        result.put("topPosts", topPosts.stream().map(p -> Map.of(
//                "id", p.getPostID(),
//                "title", p.getTitle(),
//                "views", p.getViews(),
//                "author", p.getAccount().getUsername()
//        )).collect(Collectors.toList()));
//
//        result.put("topChefs", topChefs.stream().map(a -> Map.of(
//                "name", a.getUsername(),
//                "postCount", a.getPosts().size(),
//                "avatar", a.getAvatar() != null ? a.getAvatar() : ""
//        )).collect(Collectors.toList()));
//
//        return ResponseEntity.ok(result);
//    }
//
//    /**
//     * 🎫 THỐNG KÊ TICKET (REPORTS & BUGS)
//     */
//    @GetMapping("/tickets-summary")
//    public ResponseEntity<Map<String, Long>> getTicketSummary() {
//        List<Ticket> tickets = ticketDAO.findAll();
//        Map<String, Long> summary = tickets.stream()
//                .collect(Collectors.groupingBy(
//                        t -> t.getTicketType() != null ? t.getTicketType().toUpperCase() : "OTHER",
//                        Collectors.counting()
//                ));
//        return ResponseEntity.ok(summary);
//    }
//}