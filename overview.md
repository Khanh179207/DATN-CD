# Tổng quan dự án & Tài liệu chức năng Thông báo

Phiên bản tóm tắt: dự án gồm hai phần chính:
- Backend: `GoMet-BE` — Spring Boot (Java), REST API, WebSocket (STOMP/SockJS), database chứa bảng `Notification`.
- Frontend: `GoMet-FE` — Vue 3 + Vite SPA, có `notificationService` (REST) và `webSocketService` (STOMP client).

---

## 1. Mục tiêu file này
- Mô tả ngắn về cơ chế thông báo hiện có.
- Liệt kê các hàm thông báo đã có ở backend và frontend.
- Mô tả luồng dữ liệu: từ file nào -> hàm nào -> gửi tới đâu -> file frontend nào xử lý.
- Đưa ra các quy tắc thống nhất để mở rộng chức năng thông báo an toàn và dễ bảo trì.

---
`
## 2. Tổng quan chức năng Thông báo

- Hai kênh chính:
  - REST API: dùng để lấy lịch sử, đánh dấu đã đọc, xóa.
  - Real-time (WebSocket / STOMP): dùng để đẩy thông báo tức thì tới client.
- Các thông báo được lưu vào DB (bảng `Notification`) khi có sự kiện; đồng thời (hầu hết) được gửi real-time bằng `SimpMessagingTemplate`.
- Frontend nhận sự kiện realtime và phát sự kiện DOM (`CustomEvent`) cho các component hiển thị.

---

## 3. Các API REST liên quan (backend)

- `GET /api/notifications/{accountID}` → lấy danh sách thông báo (mới nhất trước)
- `PUT /api/notifications/{id}/read` → đánh dấu 1 thông báo là đã đọc
- `PUT /api/notifications/{accountID}/read-all` → đánh dấu tất cả thông báo của user là đã đọc
- `DELETE /api/notifications/{id}` → xóa 1 thông báo
- Admin API: `POST /api/admin/notifications/all`, `POST /api/admin/notifications/user/{accountID}`, `GET /api/admin/notifications` (xem), `DELETE /api/admin/notifications/{id}`

Tham khảo file controller chính:
- [GoMet-BE/src/main/java/poly/edu/controller/NotificationController.java](GoMet-BE/src/main/java/poly/edu/controller/NotificationController.java)
- [GoMet-BE/src/main/java/poly/edu/controller/AdminNotificationController.java](GoMet-BE/src/main/java/poly/edu/controller/AdminNotificationController.java)

---

## 4. Các hàm thông báo hiện có (backend)

File chính chứa logic: [GoMet-BE/src/main/java/poly/edu/service/impl/NotificationServiceImpl.java](GoMet-BE/src/main/java/poly/edu/service/impl/NotificationServiceImpl.java)

Hàm nổi bật:
- `createNotification(title, content, type, receiverId, actorId, postId, link)` — tạo entity, lưu DB (NotificationDAO.save) và gọi gửi realtime.
- `sendRealtimeNotification(accountId, NotificationDTO)` — gửi real-time bằng `messagingTemplate.convertAndSend("/topic/notifications/" + accountId, dto)`.
- `sendAdminAlert(Notification)` — gửi broadcast admin: `/topic/admin-alerts` và user-specific `/topic/admin-notifications/{adminId}`.
- Các hàm hỗ trợ sự kiện (tạo notification & gửi): `notifyFollow`, `notifyLike`, `notifyComment`, `notifyRating`, `notifyEventVote`, `notifyAdminTicket`, `notifyAdminFeedback`, `notifyAdminReport`, `notifyAdminPostPendingApproval`, `notifyPostApproved`, `notifyPostRejected`, `notifyPostDisabled`.

Những nơi gọi các hàm notify (các file nguồn phát sự kiện):
- Like: [GoMet-BE/src/main/java/poly/edu/service/impl/LikesServiceImpl.java](GoMet-BE/src/main/java/poly/edu/service/impl/LikesServiceImpl.java) — trong `toggleLike()` gọi `notificationService.notifyLike(...)`.
- Comment: [GoMet-BE/src/main/java/poly/edu/service/impl/CommentServiceImpl.java](GoMet-BE/src/main/java/poly/edu/service/impl/CommentServiceImpl.java) — trong `saveNewComment()` gọi `notificationService.notifyComment(...)`.
- Follow: [GoMet-BE/src/main/java/poly/edu/controller/FollowController.java](GoMet-BE/src/main/java/poly/edu/controller/FollowController.java) — trong `follow()` gọi `notificationService.notifyFollow(...)`.
- Vote (event): [GoMet-BE/src/main/java/poly/edu/controller/VoteController.java](GoMet-BE/src/main/java/poly/edu/controller/VoteController.java) — `toggleVote()` gọi `notificationService.notifyEventVote(...)`.
- Post / Admin flow: [GoMet-BE/src/main/java/poly/edu/controller/PostController.java](GoMet-BE/src/main/java/poly/edu/controller/PostController.java) và [GoMet-BE/src/main/java/poly/edu/service/impl/AdminPostServiceImpl.java](GoMet-BE/src/main/java/poly/edu/service/impl/AdminPostServiceImpl.java) — gọi `notifyAdminPostPendingApproval`, `notifyPostApproved`, `notifyPostRejected`, ...
- Admin manual notifications: [GoMet-BE/src/main/java/poly/edu/service/impl/AdminNotificationServiceImpl.java](GoMet-BE/src/main/java/poly/edu/service/impl/AdminNotificationServiceImpl.java) — hiện tại **chỉ lưu DB** (không tự động broadcast realtime).

---

## 5. Luồng thực tế (một ví dụ đầy đủ) — LIKE

1) User thực hiện action trên FE → Vue call API/Controller: ví dụ click like → request đến backend `LikesServiceImpl.toggleLike()` ([GoMet-BE/.../LikesServiceImpl.java]).
2) `LikesServiceImpl.toggleLike()` lưu Like vào DB rồi gọi `notificationService.notifyLike(actorUsername, ownerId, postId)`.
3) `NotificationServiceImpl.notifyLike(...)` chuẩn bị nội dung rồi gọi `createNotification(...)`.
4) `createNotification(...)` lưu bản ghi vào DB (NotificationDAO.save) và gọi `sendRealtimeNotification(receiverId, dto)`.
5) `sendRealtimeNotification` gọi `messagingTemplate.convertAndSend("/topic/notifications/" + receiverId, notificationDTO)`.
6) Frontend (nơi subscribe): `GoMet-FE/src/services/webSocketService.js` kết nối STOMP và đã subscribe tới `/topic/notifications/${accountId}`. Khi nhận message, client parse JSON và gọi `handleNotification()`.
7) `handleNotification()` phát `CustomEvent('realtime-notification', { detail: notificationDTO })` ra `window`.
8) Component hiển thị (ví dụ Header.vue) lắng nghe `'realtime-notification'` và chạy `handleRealtimeNotification()`:
   - Thêm thông báo vào dropdown (`addNotificationToDropdown`),
   - Phát âm thanh,
   - Cập nhật badge/tiêu đề tab,
   - Gửi BroadcastChannel để đồng bộ trên nhiều tab,
   - (Tùy chọn) Hiển thị Browser Notification.

Luồng tương tự áp dụng cho: COMMENT, FOLLOW, EVENT_VOTE, RATING, POST_APPROVED/REJECTED... (chỉ khác hàm gọi ban đầu trong backend).

Luồng admin đặc biệt:
- Một số hành động gọi `NotificationServiceImpl.sendAdminAlert()` → gửi vào `/topic/admin-alerts` (broadcast cho admins) và `/topic/admin-notifications/{adminId}` (user-specific admin).
- FE subscribe cả hai: `webSocketService` subscribe `/topic/admin-notifications/${accountId}` và `/topic/admin-alerts` → HeaderAdmin.vue lắng nghe `admin-notification` và `admin-alert`.

---

## 6. Frontend: các thành phần liên quan

- WebSocket client: [GoMet-FE/src/services/webSocketService.js](GoMet-FE/src/services/webSocketService.js)
  - Kết nối: `SockJS('http://localhost:8080/ws')` + `Stomp.over(socket)`
  - Subscriptions: `/topic/notifications/${accountId}`, `/topic/admin-notifications/${accountId}`, `/topic/admin-alerts`
  - Custom events phát ra: `realtime-notification`, `admin-notification`, `admin-alert`.

- REST wrapper: [GoMet-FE/src/services/notificationService.js](GoMet-FE/src/services/notificationService.js)
  - `getNotifications(accountID)`, `markNotificationRead(notificationID)`, `markAllNotificationsRead(accountID)`, `deleteNotification(notificationID)`.

- UI/handlers:
  - User header: [GoMet-FE/src/components/topbar/Header.vue](GoMet-FE/src/components/topbar/Header.vue) — lắng nghe `realtime-notification`, xử lý hiển thị, sound, BroadcastChannel,
  - Admin header: [GoMet-FE/src/components/topbar/HeaderAdmin.vue](GoMet-FE/src/components/topbar/HeaderAdmin.vue) — lắng nghe `admin-alert` và `admin-notification`.

---

## 7. Các gap hiện có & khuyến nghị nhanh

- Admin manual notifications (API `POST /api/admin/notifications/*`) hiện chỉ lưu DB (AdminNotificationServiceImpl.save) và **không** tự động gửi realtime — nếu muốn realtime cần gọi `NotificationServiceImpl.sendAdminAlert()` hoặc mở rộng `AdminNotificationServiceImpl` để broadcast.
- Hiện dùng topic-based user-specific path `/topic/notifications/{accountId}` (thay vì `/user/{username}/queue/...`). Điều này hoạt động nhưng cần quy ước rõ ràng.
- Đảm bảo client STOMP gửi header `Authorization: Bearer <token>` khi connect, BE đã có JWT extractor trong `WebSocketConfig`.

---

## 8. Rules thống nhất (đề xuất) — bắt buộc tuân theo khi mở rộng

- Backend general:
  - Rule B1: Mọi notification đều phải được tạo qua `NotificationService.createNotification(...)` hoặc helper wrapper tương đương để đảm bảo **lưu DB** + **gửi realtime** đồng nhất.
  - Rule B2: `NotificationDTO` bắt buộc có: `notificationID` (unique), `title`, `content`, `type` (UPPER_SNAKE), `createdAt` (ISO), `isRead` (0/1), `username`, `avatarUrl`, `postId` (nullable), `link` (nullable).
  - Rule B3: Nếu muốn broadcast admin (real-time) sau khi lưu, gọi `sendAdminAlert()` hoặc dùng `messagingTemplate.convertAndSend("/topic/admin-alerts", dto)`; KHÔNG chỉ lưu DB rồi nghĩ là realtime tự có.
  - Rule B4: Chuẩn hóa các giá trị `type` (ví dụ: LIKE, COMMENT, FOLLOW, RATING, EVENT_VOTE, TICKET, FEEDBACK, REPORT, POST_PENDING_APPROVAL, POST_APPROVED, POST_REJECTED, POST_DISABLED, ADMIN_MANUAL).
  - Rule B5: Viết unit test cho mỗi notifyX() để đảm bảo DTO đúng và `messagingTemplate` được gọi.

- WebSocket / Messaging:
  - Rule M1: Chủ đề (topic) phải theo quy tắc: `/topic/notifications/{accountId}`, `/topic/admin-notifications/{accountId}`, `/topic/admin-alerts` — hoặc thống nhất chuyển sang `/user/{principal}/queue/notifications` nếu muốn dùng cơ chế user-destinations.
  - Rule M2: Luôn attach header `Authorization: Bearer <token>` khi connect/ gửi (BE đã giải mã trong `WebSocketConfig`).

- Frontend:
  - Rule F1: Event tên thống nhất: `realtime-notification`, `admin-notification`, `admin-alert`.
  - Rule F2: Notification object shape trên FE phải khớp `NotificationDTO` (trong `Header.vue` có mapping hiện tại). Nếu mở rộng field, cập nhật `addNotificationToDropdown()` và các component hiển thị.
  - Rule F3: Khi nhận sự kiện, kiểm tra `notificationID` để tránh duplicate trước khi thêm vào dropdown.
  - Rule F4: Khi thêm loại thông báo mới có behavior đặc biệt (ví dụ: mở modal, redirect admin), cập nhật `Header.vue`/`HeaderAdmin.vue` để xử lý `handleNotiClick()` theo `type`.
  - Rule F5: Giữ fallback: nếu WebSocket mất kết nối, client nên fallback polling `GET /api/notifications/{accountID}` khi user mở dropdown.

- Bảo mật & vận hành:
  - Rule S1: Tất cả endpoint liên quan đến Notification phải yêu cầu xác thực (`@PreAuthorize` đã có trên controller hiện tại), không trả dữ liệu cho anonymous.
  - Rule S2: Tránh gửi trong payload dữ liệu nhạy cảm; nếu cần, hạn chế chỉ gửi `link` tới route thay vì toàn bộ nội dung nhạy cảm.
  - Rule S3: Logging: ghi log khi gửi realtime thất bại (hiện `sendRealtimeNotification` đã catch và in lỗi) và có cơ chế retry/backfill nếu cần.

---

## 9. Hướng dẫn thêm tính năng thông báo mới (quick-start)

1. Backend: Thêm method `notifyX(...)` vào `NotificationService` interface.
2. Implement trong `NotificationServiceImpl` sử dụng `createNotification(...)` (đảm bảo gửi realtime tự động).
3. Từ nơi phát event (service/controller), gọi `notificationService.notifyX(...)` với các tham số cần thiết.
4. Frontend: Nếu cần UI mới, cập nhật `Header.vue` / `HeaderAdmin.vue` để mapping `type` → icon/link/behavior.
5. Viết test cho backend (unit) và kiểm tra trên FE: mở tab, chờ WebSocket, chắc chắn event được nhận.

---

## 10. Tham khảo các file chính (quick links)
- [GoMet-BE/src/main/java/poly/edu/service/impl/NotificationServiceImpl.java](GoMet-BE/src/main/java/poly/edu/service/impl/NotificationServiceImpl.java)
- [GoMet-BE/src/main/java/poly/edu/controller/NotificationController.java](GoMet-BE/src/main/java/poly/edu/controller/NotificationController.java)
- [GoMet-BE/src/main/java/poly/edu/controller/AdminNotificationController.java](GoMet-BE/src/main/java/poly/edu/controller/AdminNotificationController.java)
- [GoMet-BE/src/main/java/poly/edu/config/WebSocketConfig.java](GoMet-BE/src/main/java/poly/edu/config/WebSocketConfig.java)
- [GoMet-BE/src/main/java/poly/edu/service/impl/AdminNotificationServiceImpl.java](GoMet-BE/src/main/java/poly/edu/service/impl/AdminNotificationServiceImpl.java)
- [GoMet-BE/src/main/java/poly/edu/service/impl/LikesServiceImpl.java](GoMet-BE/src/main/java/poly/edu/service/impl/LikesServiceImpl.java)
- [GoMet-BE/src/main/java/poly/edu/service/impl/CommentServiceImpl.java](GoMet-BE/src/main/java/poly/edu/service/impl/CommentServiceImpl.java)
- [GoMet-BE/src/main/java/poly/edu/controller/FollowController.java](GoMet-BE/src/main/java/poly/edu/controller/FollowController.java)
- [GoMet-BE/src/main/java/poly/edu/controller/VoteController.java](GoMet-BE/src/main/java/poly/edu/controller/VoteController.java)
- [GoMet-FE/src/services/webSocketService.js](GoMet-FE/src/services/webSocketService.js)
- [GoMet-FE/src/services/notificationService.js](GoMet-FE/src/services/notificationService.js)
- [GoMet-FE/src/components/topbar/Header.vue](GoMet-FE/src/components/topbar/Header.vue)
- [GoMet-FE/src/components/topbar/HeaderAdmin.vue](GoMet-FE/src/components/topbar/HeaderAdmin.vue)

---
