# Tóm tắt luồng xử lý: Appeals & Tickets

Mục tiêu: mô tả ngắn gọn, theo luồng FE → API (controller) → Service → DAO/Notification, để người không biết có thể hiểu và thuyết trình.

---

**1) Appeals (Khiếu nại / Gỡ ban tài khoản)**

- User gửi request từ FE:
  - File: GoMet-FE/src/components/modals/AppealModal.vue
  - Hàm: `handleSubmit()` => gọi `createAppeal(appealData)` trong GoMet-FE/src/services/appealService.js

  > FE gửi POST `/api/appeals` với body: { email, reason } (public, không cần token)

- API backend (Controller):
  - File: GoMet-BE/src/main/java/poly/edu/controller/AppealController.java
  - Method: `@PostMapping("/appeals") createAppeal(@RequestBody Map<String,String> request)`
    - Validate `email` và `reason`, gọi `appealService.createAppeal(email, reason)`

- Service (interface → impl):
  - Interface: GoMet-BE/src/main/java/poly/edu/service/AppealService.java (`createAppeal(String email, String reason)`, + các method admin)
  - Impl: GoMet-BE/src/main/java/poly/edu/service/impl/AppealServiceImpl.java
    - Luồng chính: kiểm tra account tồn tại & bị ban → kiểm tra rate-limit `canCreateAppeal` → tạo entity `Appeal` → lưu qua `AppealDAO` → gửi notification cho admin (NotificationService) → trả về `AppealDTO`.
    - Khi admin xử lý: `updateAppeal(...)` lưu `status`, `note`, `adminId`, `adminName`, gửi email (EmailService) và notify user (NotificationService.notifyAppealUpdate).
    - Gỡ ban: `unbanAccountByAppeal(...)` gọi `accountService.unban(...)`, cập nhật trạng thái phiếu, gửi email/notification.

- Luồng admin (FE → BE):
  - FE: GoMet-FE/src/services/appealService.js
    - `getAppeals()` → GET `/api/admin/appeals` (gửi token via getAuthConfig())
    - `updateAppeal(appealID, {status,note})` → PUT `/api/admin/appeals/{appealID}` (token)
    - `unbanAccountByAppeal(appealID)` → POST `/api/admin/appeals/{appealID}/unban` (token)
  - BE Controller: `@GetMapping("/admin/appeals")`, `@PutMapping("/admin/appeals/{appealID}")`, `@PostMapping("/admin/appeals/{appealID}/unban")` (tất cả `@PreAuthorize("hasRole('ADMIN')")`)

- Lưu ý quan trọng:
  - FE có hàm `getAppealStatus` gọi `/api/appeals/status?email=...` (GoMet-FE/src/services/appealService.js) nhưng trong `AppealController` không thấy mapping `/api/appeals/status`. Có `AppealService.getAppealStatusByEmail(...)` nhưng chưa có endpoint công khai — cần bổ sung controller hoặc sửa FE.

---

**2) Tickets (Phiếu hỗ trợ / Feedback / Bug / Report)**

- User gửi request từ FE (tạo ticket):
  - File: GoMet-FE/src/components/modals/FeedbackModal.vue
  - Hàm: `submitFeedback()`
    - Nếu có ảnh: gọi `uploadMedia(file, 'tickets')` → nhận `attachmentUrl`
    - Tạo payload theo `TicketDTO`:
      - `{ accountId, ticketType, title, description, attachment, targetPostId }
    - Gọi `api.post('/api/tickets/create', payload)` (yêu cầu auth/token)

- API backend (Controller):
  - File: GoMet-BE/src/main/java/poly/edu/controller/UserTicketController.java
  - Class-level: `@RequestMapping("/api/tickets")` và `@PreAuthorize("isAuthenticated()")` → chỉ user đã đăng nhập
  - Method: `@PostMapping("/create") createTicket(@RequestBody TicketDTO ticketDTO)` → gọi `ticketService.saveTicket(ticketDTO)`

- Service (interface → impl):
  - Interface: GoMet-BE/src/main/java/poly/edu/service/TicketService.java (`saveTicket(TicketDTO dto)`, `updateTicketStatus(...)`, v.v.)
  - Impl: GoMet-BE/src/main/java/poly/edu/service/impl/TicketServiceImpl.java
    - `saveTicket`: map DTO → Ticket entity, set `status=0` (Pending), liên kết Account (accountDAO) và Post (postDAO nếu có), lưu via `ticketDAO.save`, gọi `sendAdminAlert()` → `notificationService.notifyAdminTicket(...)` để thông báo admin.
    - `updateTicketStatus(ticketId, newStatus, adminId, adminName, adminNote)`: cập nhật `status`, `adminId`, `adminName`, set `processedAt`/`resolvedAt`, lưu, gọi `notificationService.notifyTicketUpdate(...)` để thông báo user.

- Luồng admin (FE → BE):
  - FE admin: GoMet-FE/src/services/ticketService.js
    - `getAllTickets()` → GET `/api/admin/tickets` (token)
    - `getTicketsByStatus(status)` → GET `/api/admin/tickets/status/{status}` (token)
    - `updateTicketStatus(id, status, note)` → PUT `/api/admin/tickets/{id}/status?status={status}` with body `{ note }` (token)
  - BE Controller: GoMet-BE/src/main/java/poly/edu/controller/AdminTicketController.java (`@RequestMapping("/api/admin/tickets")`, `@PreAuthorize("hasRole('ADMIN')")`)
    - `GET /api/admin/tickets`, `GET /api/admin/tickets/pending`, `GET /api/admin/tickets/resolved`, `PUT /{id}/status`

---

**3) Notification & Real-time**

- File chính: GoMet-BE/src/main/java/poly/edu/service/impl/NotificationServiceImpl.java
  - `createNotification(...)` lưu Notification vào DB (NotificationDAO) và gửi real-time qua `SimpMessagingTemplate`:
    - user-specific: `/topic/notifications/{accountId}`
    - admin broadcast: `/topic/admin-alerts` và `/topic/admin-notifications/{adminId}`
  - Ticket flow: `ticketService.saveTicket` → `notifyAdminTicket(userUsername, ticketId)` → thông báo admin
  - Ticket status change: `notifyTicketUpdate(ticketId, newStatus, accountId)` → gửi thông báo cho user
  - Appeal update: `notifyAppealUpdate(appealId, status, accountId)` → gửi cho user

**4) Quyền & Xác thực (JWT)**

- FE: `useAuthStore()` chứa `token`; admin FE services dùng `getAuthConfig()` để đính token vào header `Authorization: Bearer ...`.
- BE: Controllers dùng `@PreAuthorize("isAuthenticated()")` cho user endpoints, `@PreAuthorize("hasRole('ADMIN')")` cho admin endpoints. Admin controllers thường dùng helper `getAdminFromToken(HttpServletRequest)` (dùng `JwtUtils` + `AccountDAO`) để lấy `Account` thực tế từ token.

---

**5) File tham chiếu chính (chỉ danh sách để mở nhanh)**

- FE (client)
  - GoMet-FE/src/components/modals/AppealModal.vue
  - GoMet-FE/src/services/appealService.js
  - GoMet-FE/src/pages/admin/AppealManagement.vue
  - GoMet-FE/src/components/modals/FeedbackModal.vue
  - GoMet-FE/src/services/ticketService.js
  - GoMet-FE/src/pages/admin/ticketadmin/TicketManagement.vue

- BE (server)
  - GoMet-BE/src/main/java/poly/edu/controller/AppealController.java
  - GoMet-BE/src/main/java/poly/edu/service/impl/AppealServiceImpl.java
  - GoMet-BE/src/main/java/poly/edu/dto/AppealDTO.java
  - GoMet-BE/src/main/java/poly/edu/controller/UserTicketController.java
  - GoMet-BE/src/main/java/poly/edu/controller/AdminTicketController.java
  - GoMet-BE/src/main/java/poly/edu/service/impl/TicketServiceImpl.java
  - GoMet-BE/src/main/java/poly/edu/dto/TicketDTO.java
  - GoMet-BE/src/main/java/poly/edu/dto/AdminTicketDTO.java
  - GoMet-BE/src/main/java/poly/edu/service/impl/NotificationServiceImpl.java

---

Ghi chú / Action items ngắn:
- Bổ sung hoặc kiểm tra endpoint `GET /api/appeals/status?email=` vì FE gọi `getAppealStatus()` nhưng controller hiện tại không expose route này.
- Kiểm tra flow upload ảnh (FE uploadMedia → trả URL) để đảm bảo `attachment` đúng định dạng BE mong đợi.

Nếu bạn muốn, tôi có thể:
- Thêm mapping `/api/appeals/status` trong `AppealController` (gọi `AppealService.getAppealStatusByEmail`) và tạo test ngắn.
- Hoặc xuất bản phiên bản ngắn gọn hơn để thuyết trình (slides / bullets).
