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
- Hướng dẫn UI/UX cho hệ thống thông báo để đảm bảo trải nghiệm người dùng tốt nhất.

---

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
- Admin API: 
  - `GET /api/admin/member-accounts` → danh sách tài khoản member (cho dropdown)
  - `POST /api/admin/notifications/all` → gửi broadcast toàn hệ thống
  - `POST /api/admin/notifications/user/{accountID}` → gửi tới 1 user cụ thể
  - `GET /api/admin/notifications` → xem danh sách thông báo admin
  - `GET /api/admin/notifications/{id}` → xem chi tiết + danh sách người đọc
  - `DELETE /api/admin/notifications/{id}` → xóa thông báo admin

Tham khảo file controller chính:
- [GoMet-BE/src/main/java/poly/edu/controller/NotificationController.java](GoMet-BE/src/main/java/poly/edu/controller/NotificationController.java)
- [GoMet-BE/src/main/java/poly/edu/controller/AdminNotificationController.java](GoMet-BE/src/main/java/poly/edu/controller/AdminNotificationController.java)
- [GoMet-BE/src/main/java/poly/edu/controller/AdminAccountController.java](GoMet-BE/src/main/java/poly/edu/controller/AdminAccountController.java)

---

## 4. Các hàm thông báo hiện có (backend)

File chính chứa logic: [GoMet-BE/src/main/java/poly/edu/service/impl/NotificationServiceImpl.java](GoMet-BE/src/main/java/poly/edu/service/impl/NotificationServiceImpl.java)

Hàm nổi bật:
- `createNotification(title, content, type, receiverId, actorId, postId, link)` — tạo entity, lưu DB (NotificationDAO.save) và gọi gửi realtime.
- `sendRealtimeNotification(accountId, NotificationDTO)` — gửi real-time bằng `messagingTemplate.convertAndSend("/topic/notifications/" + accountId, dto)`.
- `sendAdminAlert(Notification)` — gửi broadcast admin: `/topic/admin-alerts` và user-specific `/topic/admin-notifications/{adminId}`.
- **NEW**: `notifyPremiumStatus(accountId, isActivated, endAt)` — thông báo kích hoạt/hết hạn premium

Các hàm hỗ trợ sự kiện (tạo notification & gửi): 
- `notifyFollow`, `notifyLike`, `notifyComment`, `notifyRating`, `notifyEventVote`, `notifyAdminTicket`, `notifyAdminFeedback`, `notifyAdminReport`, `notifyAdminPostPendingApproval`, `notifyPostApproved`, `notifyPostRejected`, `notifyPostDisabled`.
- **NEW**: `notifyPremiumStatus` — xử lý thông báo premium

Những nơi gọi các hàm notify (các file nguồn phát sự kiện):
- Like: [GoMet-BE/src/main/java/poly/edu/service/impl/LikesServiceImpl.java](GoMet-BE/src/main/java/poly/edu/service/impl/LikesServiceImpl.java) — trong `toggleLike()` gọi `notificationService.notifyLike(...)`.
- Comment: [GoMet-BE/src/main/java/poly/edu/service/impl/CommentServiceImpl.java](GoMet-BE/src/main/java/poly/edu/service/impl/CommentServiceImpl.java) — trong `saveNewComment()` gọi `notificationService.notifyComment(...)`.
- Follow: [GoMet-BE/src/main/java/poly/edu/controller/FollowController.java](GoMet-BE/src/main/java/poly/edu/controller/FollowController.java) — trong `follow()` gọi `notificationService.notifyFollow(...)`.
- Vote (event): [GoMet-BE/src/main/java/poly/edu/controller/VoteController.java](GoMet-BE/src/main/java/poly/edu/controller/VoteController.java) — `toggleVote()` gọi `notificationService.notifyEventVote(...)`.
- Post / Admin flow: [GoMet-BE/src/main/java/poly/edu/controller/PostController.java](GoMet-BE/src/main/java/poly/edu/controller/PostController.java) và [GoMet-BE/src/main/java/poly/edu/service/impl/AdminPostServiceImpl.java](GoMet-BE/src/main/java/poly/edu/service/impl/AdminPostServiceImpl.java) — gọi `notifyAdminPostPendingApproval`, `notifyPostApproved`, `notifyPostRejected`, ...
- **Premium flow**: [GoMet-BE/src/main/java/poly/edu/controller/PaymentController.java](GoMet-BE/src/main/java/poly/edu/controller/PaymentController.java) — trong `vnpayCallback()`, `force-upgrade`, và scheduler `autoCancelExpiredSubscriptions()` gọi `notificationService.notifyPremiumStatus(...)`.
- Admin manual notifications: [GoMet-BE/src/main/java/poly/edu/service/impl/AdminNotificationServiceImpl.java](GoMet-BE/src/main/java/poly/edu/service/impl/AdminNotificationServiceImpl.java) — hiện tại **chỉ lưu DB** (không tự động broadcast realtime).

---

## 5. Luồng thực tế (một ví dụ đầy đủ) — PREMIUM STATUS

### 5.1 Kích hoạt Premium thành công
1) User thanh toán VNPay thành công → callback đến `PaymentController.vnpayCallback()`
2) `vnpayCallback()` xác thực → gọi `updateAccountToPremium(accId, realPlanType, transId)`
3) `updateAccountToPremium()` tính toán `endDate` và cập nhật Account + Subscription → trả về `LocalDateTime endDate`
4) Gọi `notificationService.notifyPremiumStatus(accId, true, endDate)`
5) `notifyPremiumStatus()` tạo thông báo với:
   - Title: "Premium đã được kích hoạt"
   - Content: "Gói Premium của bạn đã được kích hoạt thành công. Thời hạn đến: [dd/MM/yyyy HH:mm]."
   - Type: "PREMIUM_PURCHASED"
   - Link: "/profile"
6) `createNotification()` lưu DB + gửi realtime qua WebSocket
7) Frontend nhận và hiển thị với avatar hệ thống `/logogoc.jpg`

### 5.2 Premium hết hạn (scheduler)
1) Scheduler `autoCancelExpiredSubscriptions()` chạy định kỳ
2) Query DB lấy danh sách AccountID có subscription hết hạn
3) Cập nhật trạng thái Subscription và Account (isPremium = 0)
4) Với mỗi accountId: gọi `notificationService.notifyPremiumStatus(accountId, false, null)`
5) `notifyPremiumStatus()` tạo thông báo với:
   - Title: "Premium đã hết hạn"
   - Content: "Gói Premium của bạn đã hết hạn. Hãy gia hạn để tiếp tục sử dụng các đặc quyền."
   - Type: "PREMIUM_EXPIRED"
   - Link: "/upgrade"
6) `createNotification()` lưu DB + gửi realtime

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
  - Admin: `sendAdminNotificationToAll(payload)`, `sendAdminNotificationToUser(accountID, payload)`
  - Utility: `resolveNotificationLink(notification)`

- UI/handlers:
  - User header: [GoMet-FE/src/components/topbar/Header.vue](GoMet-FE/src/components/topbar/Header.vue) — lắng nghe `realtime-notification`, xử lý hiển thị, sound, BroadcastChannel,
  - Admin header: [GoMet-FE/src/components/topbar/HeaderAdmin.vue](GoMet-FE/src/components/topbar/HeaderAdmin.vue) — lắng nghe `admin-alert` và `admin-notification`.
  - **NEW**: Admin notification management: [GoMet-FE/src/pages/admin/NotificationManagement.vue](GoMet-FE/src/pages/admin/NotificationManagement.vue) — UI đồng bộ với CategoryManagement

- Browser notifications: [GoMet-FE/src/services/browserNotificationService.js](GoMet-FE/src/services/browserNotificationService.js)
  - Hiển thị desktop notifications với icon avatar phù hợp

---

## 7. Hệ thống Avatar & Logo

### 7.1 Quy tắc hiển thị avatar
- **Thông báo từ user**: hiển thị avatar của user đó
- **Thông báo từ hệ thống**: hiển thị logo GoMet `/logogoc.jpg`
- **Fallback**: nếu avatar null/undefined/error → dùng `/logogoc.jpg`

### 7.2 Implementation
**Backend** (NotificationServiceImpl.convertToDTO):
```java
String avatarUrl = (actor != null && actor.getAvatar() != null)
    ? actor.getAvatar()
    : "/logogoc.jpg";
```

**Frontend** (Header.vue, HeaderAdmin.vue):
```javascript
const defaultSystemAvatar = '/logogoc.jpg'

const handleAvatarError = (event) => {
  if (event?.target) {
    event.target.src = defaultSystemAvatar
  }
}

// Trong normalizeNotification:
avatar: notification.avatarUrl || notification.avatar || defaultSystemAvatar
```

### 7.3 File resources
- Logo hệ thống: `GoMet-FE/public/logogoc.jpg`
- Runtime path: `/logogoc.jpg` (truy cập trực tiếp từ root)
- Đảm bảo file tồn tại trong thư mục public

---

## 8. UI/UX Guidelines - Quản lý Thông báo Admin

### 8.1 Thiết kế tổng quan
- **Layout**: Đồng bộ hoàn toàn với [CategoryManagement.vue](GoMet-FE/src/pages/admin/categoryadmin/CategoryManagement.vue)
- **Wrapper class**: `category-sovereign-wrapper notification-sync-page`
- **Grid system**: Sử dụng `lux-cat-card` cho danh sách thông báo
- **Modal design**: Áp dụng `.modal-body-lux`, `.form-group-lux`, `.input-lux`

### 8.2 Stat Cards (Header)
- **Tổng số**: Icon `<Layers>`, màu gradient chính
- **Broadcast**: Icon `<Send>`, màu secondary  
- **Targeted**: Icon `<Users>`, màu highlight (cam)

### 8.3 Modal "Khởi tạo Thông báo"
```vue
<template>
  <!-- Radio tabs chọn đối tượng -->
  <div class="recipient-switch">
    <label class="radio-tab" :class="{ active: form.sendTo === 'all' }">
      <input type="radio" value="all" v-model="form.sendTo" />
      <span>Tất cả</span>
    </label>
    <label class="radio-tab" :class="{ active: form.sendTo === 'user' }">
      <input type="radio" value="user" v-model="form.sendTo" />
      <span>Cá nhân / Nhiều người</span>
    </label>
  </div>

  <!-- Multi-select recipients với search -->
  <Transition name="recipient-panel">
    <div v-if="form.sendTo === 'user'" class="form-group-lux animate-in">
      <label>Chọn người nhận <span class="req">*</span></label>
      <div ref="recipientPickerRef">
        <!-- Selected chips -->
        <div class="selected-chip-list" v-if="selectedRecipients.length">
          <button v-for="member in selectedRecipients" :key="member.accountID" 
                  class="selected-chip" type="button" @click="removeRecipient(member.accountID)">
            {{ member.username || ('ID ' + member.accountID) }}
            <X :size="14" />
          </button>
        </div>

        <!-- Search input + dropdown -->
        <div class="recipient-picker" @click="showRecipientDropdown = true">
          <Search :size="16" />
          <input v-model="recipientKeyword" type="text" class="input-lux recipient-input" 
                 placeholder="Tìm theo ID, username, email..." @focus="showRecipientDropdown = true" />
        </div>

        <!-- Dropdown với animation -->
        <Transition name="recipient-dropdown-anim">
          <div v-if="showRecipientDropdown" class="recipient-dropdown custom-scroll">
            <button v-for="member in filteredRecipients" :key="member.accountID" 
                    type="button" class="recipient-option" @click="addRecipient(member)">
              <img :src="member.avatar || '/logogoc.jpg'" alt="avatar" @error="handleAvatarError" />
              <div>
                <strong>{{ member.username || ('ID ' + member.accountID) }}</strong>
                <span>{{ member.email || 'Không có email' }}</span>
              </div>
            </button>
          </div>
        </Transition>
      </div>
    </div>
  </Transition>

  <!-- Form fields -->
  <div class="form-group-lux">
    <label>Tiêu đề thông báo <span class="req">*</span></label>
    <input v-model="form.title" type="text" class="input-lux" placeholder="Nhập tiêu đề..." />
  </div>

  <div class="form-group-lux">
    <label>Nội dung thông báo <span class="req">*</span></label>
    <textarea v-model="form.content" class="input-lux" rows="4" placeholder="Nhập nội dung..."></textarea>
  </div>

  <div class="form-group-lux">
    <label>Đường dẫn đích (tùy chọn)</label>
    <input v-model="form.link" type="text" class="input-lux" placeholder="https://... hoặc /route" />
  </div>
</template>
```

### 8.4 Modal "Chi tiết Thông báo"
- **Design**: Đồng bộ hoàn toàn với modal tạo mới
- **Read-only fields**: Sử dụng `.input-lux.readonly-input` với cùng style
- **Số người đọc**: Hiển thị trong `.readonly-count` (hình vuông bo góc, gradient cam)
- **Danh sách người đọc**: Avatar + username + email + thời gian đọc

### 8.5 Animations Requirements
- **Tab switching**: `recipient-panel` transition (opacity + translateY)
- **Dropdown**: `recipient-dropdown-anim` transition (opacity + translateY)  
- **Timing**: 0.18s - 0.22s ease transitions
- **Smoothness**: Không được "đùng" một cái, phải mượt mà

### 8.6 Color System
- **Primary**: Gradient chính (từ design system)
- **Secondary**: Màu phụ
- **Highlight**: Cam gradient cho stats và counters
- **Readonly**: #f8fafc background, #e2e8f0 border, #0f172a text

---

## 9. Các gap hiện có & khuyến nghị nhanh

- Admin manual notifications (API `POST /api/admin/notifications/*`) hiện chỉ lưu DB (AdminNotificationServiceImpl.save) và **không** tự động gửi realtime — nếu muốn realtime cần gọi `NotificationServiceImpl.sendAdminAlert()` hoặc mở rộng `AdminNotificationServiceImpl` để broadcast.
- Hiện dùng topic-based user-specific path `/topic/notifications/{accountId}` (thay vì `/user/{username}/queue/...`). Điều này hoạt động nhưng cần quy ước rõ ràng.
- Đảm bảo client STOMP gửi header `Authorization: Bearer <token>` khi connect, BE đã có JWT extractor trong `WebSocketConfig`.

---

## 10. Rules thống nhất (đề xuất) — bắt buộc tuân theo khi mở rộng

### 10.1 Backend general
- **Rule B1**: Mọi notification đều phải được tạo qua `NotificationService.createNotification(...)` hoặc helper wrapper tương đương để đảm bảo **lưu DB** + **gửi realtime** đồng nhất.
- **Rule B2**: `NotificationDTO` bắt buộc có: `notificationID` (unique), `title`, `content`, `type` (UPPER_SNAKE), `createdAt` (ISO), `isRead` (0/1), `username`, `avatarUrl`, `postId` (nullable), `link` (nullable).
- **Rule B3**: Nếu muốn broadcast admin (real-time) sau khi lưu, gọi `sendAdminAlert()` hoặc dùng `messagingTemplate.convertAndSend("/topic/admin-alerts", dto)`; KHÔNG chỉ lưu DB rồi nghĩ là realtime tự có.
- **Rule B4**: Chuẩn hóa các giá trị `type` (ví dụ: LIKE, COMMENT, FOLLOW, RATING, EVENT_VOTE, TICKET, FEEDBACK, REPORT, POST_PENDING_APPROVAL, POST_APPROVED, POST_REJECTED, POST_DISABLED, ADMIN_MANUAL, PREMIUM_PURCHASED, PREMIUM_EXPIRED).
- **Rule B5**: Viết unit test cho mỗi notifyX() để đảm bảo DTO đúng và `messagingTemplate` được gọi.
- **NEW Rule B6**: Thông báo từ hệ thống phải có `avatarUrl = "/logogoc.jpg"` và `username = "Hệ thống GoMet"`

### 10.2 WebSocket / Messaging
- **Rule M1**: Chủ đề (topic) phải theo quy tắc: `/topic/notifications/{accountId}`, `/topic/admin-notifications/{accountId}`, `/topic/admin-alerts` — hoặc thống nhất chuyển sang `/user/{principal}/queue/notifications` nếu muốn dùng cơ chế user-destinations.
- **Rule M2**: Luôn attach header `Authorization: Bearer <token>` khi connect/ gửi (BE đã giải mã trong `WebSocketConfig`).

### 10.3 Frontend
- **Rule F1**: Event tên thống nhất: `realtime-notification`, `admin-notification`, `admin-alert`.
- **Rule F2**: Notification object shape trên FE phải khớp `NotificationDTO` (trong `Header.vue` có mapping hiện tại). Nếu mở rộng field, cập nhật `addNotificationToDropdown()` và các component hiển thị.
- **Rule F3**: Khi nhận sự kiện, kiểm tra `notificationID` để tránh duplicate trước khi thêm vào dropdown.
- **Rule F4**: Khi thêm loại thông báo mới có behavior đặc biệt (ví dụ: mở modal, redirect admin), cập nhật `Header.vue`/`HeaderAdmin.vue` để xử lý `handleNotiClick()` theo `type`.
- **Rule F5**: Giữ fallback: nếu WebSocket mất kết nối, client nên fallback polling `GET /api/notifications/{accountID}` khi user mở dropdown.
- **NEW Rule F6**: Avatar fallback bắt buộc: luôn dùng `'/logogoc.jpg'` cho thông báo hệ thống, xử lý lỗi avatar với `@error` handler
- **NEW Rule F7**: UI admin notification phải đồng bộ với CategoryManagement, sử dụng cùng design system

### 10.4 Bảo mật & vận hành
- **Rule S1**: Tất cả endpoint liên quan đến Notification phải yêu cầu xác thực (`@PreAuthorize` đã có trên controller hiện tại), không trả dữ liệu cho anonymous.
- **Rule S2**: Tránh gửi trong payload dữ liệu nhạy cảm; nếu cần, hạn chế chỉ gửi `link` tới route thay vì toàn bộ nội dung nhạy cảm.
- **Rule S3**: Logging: ghi log khi gửi realtime thất bại (hiện `sendRealtimeNotification` đã catch và in lỗi) và có cơ chế retry/backfill nếu cần.

---

## 11. Hướng dẫn thêm tính năng thông báo mới (quick-start)

1. Backend: Thêm method `notifyX(...)` vào `NotificationService` interface.
2. Implement trong `NotificationServiceImpl` sử dụng `createNotification(...)` (đảm bảo gửi realtime tự động).
3. Từ nơi phát event (service/controller), gọi `notificationService.notifyX(...)` với các tham số cần thiết.
4. Frontend: Nếu cần UI mới, cập nhật `Header.vue` / `HeaderAdmin.vue` để mapping `type` → icon/link/behavior.
5. Viết test cho backend (unit) và kiểm tra trên FE: mở tab, chờ WebSocket, chắc chắn event được nhận.
6. **UI/UX check**: Đảm bảo avatar fallback hoạt động, design đồng bộ với hệ thống

---

## 12. Tham khảo các file chính (quick links)

### Backend
- [GoMet-BE/src/main/java/poly/edu/service/impl/NotificationServiceImpl.java](GoMet-BE/src/main/java/poly/edu/service/impl/NotificationServiceImpl.java)
- [GoMet-BE/src/main/java/poly/edu/controller/NotificationController.java](GoMet-BE/src/main/java/poly/edu/controller/NotificationController.java)
- [GoMet-BE/src/main/java/poly/edu/controller/AdminNotificationController.java](GoMet-BE/src/main/java/poly/edu/controller/AdminNotificationController.java)
- [GoMet-BE/src/main/java/poly/edu/controller/PaymentController.java](GoMet-BE/src/main/java/poly/edu/controller/PaymentController.java) - Premium flow
- [GoMet-BE/src/main/java/poly/edu/config/WebSocketConfig.java](GoMet-BE/src/main/java/poly/edu/config/WebSocketConfig.java)
- [GoMet-BE/src/main/java/poly/edu/service/impl/AdminNotificationServiceImpl.java](GoMet-BE/src/main/java/poly/edu/service/impl/AdminNotificationServiceImpl.java)

### Frontend
- [GoMet-FE/src/services/webSocketService.js](GoMet-FE/src/services/webSocketService.js)
- [GoMet-FE/src/services/notificationService.js](GoMet-FE/src/services/notificationService.js)
- [GoMet-FE/src/components/topbar/Header.vue](GoMet-FE/src/components/topbar/Header.vue)
- [GoMet-FE/src/components/topbar/HeaderAdmin.vue](GoMet-FE/src/components/topbar/HeaderAdmin.vue)
- [GoMet-FE/src/pages/admin/NotificationManagement.vue](GoMet-FE/src/pages/admin/NotificationManagement.vue) - UI mẫu
- [GoMet-FE/src/pages/admin/categoryadmin/CategoryManagement.vue](GoMet-FE/src/pages/admin/categoryadmin/CategoryManagement.vue) - Design reference
- [GoMet-FE/src/services/browserNotificationService.js](GoMet-FE/src/services/browserNotificationService.js)

### Resources
- [GoMet-FE/public/logogoc.jpg](GoMet-FE/public/logogoc.jpg) - Logo hệ thống
- [GoMet-FE/src/pages/admin/NotificationManagement.scss](GoMet-FE/src/pages/admin/NotificationManagement.scss) - Style guidelines

---

## 13. Testing & Validation Checklist

✅ **Backend Premium Flow**:
- [ ] notifyPremiumStatus với isActivated=true tạo thông báo đúng
- [ ] notifyPremiumStatus với isActivated=false tạo thông báo đúng  
- [ ] PaymentController gọi notifyPremiumStatus sau thành công
- [ ] Scheduler gọi notifyPremiumStatus cho expired subscriptions

✅ **Avatar System**:
- [ ] Thông báo hệ thống có avatarUrl="/logogoc.jpg"
- [ ] Thông báo từ user có avatar của user đó
- [ ] Fallback handler hoạt động khi avatar lỗi

✅ **Admin UI**:
- [ ] Modal create đồng bộ với CategoryManagement
- [ ] Multi-select recipients hoạt động với search
- [ ] Animation tab switching mượt mà
- [ ] Animation dropdown mượt mà
- [ ] Modal detail dùng readonly inputs giống create
- [ ] Readonly count hiển thị hình vuông bo góc

✅ **Realtime**:
- [ ] WebSocket nhận thông báo premium
- [ ] Avatar hiển thị đúng trên realtime notification
- [ ] Browser notification có icon đúng

---

*Document version: 2.0 - Updated: 2026-04-18*
*Chú ý: Luôn kiểm tra các file tham chiếu để có thông tin mới nhất*