# 🎯 Hệ Thống Khiếu Nại Ban Nhầm - Hướng Dẫn Triển Khai Backend

## 📋 Tóm Tắt Chung
Hệ thống cho phép người dùng bị ban (hoặc nghĩ bị ban nhầm) gửi khiếu nại đến admin. Admin sẽ xem xét và có thể gỡ ban tài khoản.

---

## 🗄️ Database Schema

**Bảng Appeals** đã được tạo:
```sql
CREATE TABLE dbo.Appeals (
  AppealID        INT IDENTITY(1,1) PRIMARY KEY,
  Email           NVARCHAR(254) NOT NULL,
  Reason          NVARCHAR(MAX) NOT NULL,
  Status          NVARCHAR(50) NOT NULL DEFAULT 'Pending', 
                  -- Pending, Review, Resolved, Rejected
  CreatedAt       DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
  UpdatedAt       DATETIME2 NULL,
  CreatedByIP     NVARCHAR(45) NULL,
  Note            NVARCHAR(MAX) NULL
);
```

---

## 🔌 API Endpoints Cần Triển Khai

### 1. **POST /api/appeals** (Người dùng gửi khiếu nại)
```csharp
// Request
{
  "email": "user@example.com",
  "reason": "Tôi cho rằng tài khoản bị ban nhầm vì..."
}

// Response (201 Created)
{
  "appealID": 1,
  "email": "user@example.com",
  "status": "Pending",
  "createdAt": "2026-03-19T10:30:00Z"
}

// Validation
- Email phải hợp lệ
- Reason phải >= 20 ký tự
- Rate limit: 1 khiếu nại / email / 24 giờ
- Lưu IP người gửi
```

**Backend Logic:**
- Kiểm tra email có tồn tại trong hệ thống không
- Kiểm tra xem người dùng có bị ban hay không (tùy chọn)
- Lưu appeal vào DB
- **Gửi notification đến admin** (xem phần WebSocket bên dưới)
- Trả về `appealID` để tracking

---

### 2. **GET /api/appeals** (Admin xem danh sách khiếu nại)
```csharp
// Request Query Params
?page=1&limit=10&status=Pending&email=user@example.com

// Response
{
  "data": [
    {
      "appealID": 1,
      "email": "user@example.com",
      "reason": "...",
      "status": "Pending",
      "createdAt": "2026-03-19T10:30:00Z",
      "updatedAt": null,
      "note": null
    }
  ],
  "total": 50,
  "page": 1,
  "limit": 10
}

// Authorization: [Authorize(Roles = "Admin")]
```

---

### 3. **GET /api/appeals/{appealID}** (Xem chi tiết khiếu nại)
```csharp
// Response
{
  "appealID": 1,
  "email": "user@example.com",
  "reason": "...",
  "status": "Pending",
  "createdAt": "2026-03-19T10:30:00Z",
  "updatedAt": null,
  "createdByIP": "192.168.1.1",
  "note": null
}
```

---

### 4. **PUT /api/admin/appeals/{appealID}** (Admin cập nhật khiếu nại)
```csharp
// Request
{
  "status": "Review",
  "note": "Đang xem xét lại tài khoản"
}

// Response
{
  "appealID": 1,
  "status": "Review",
  "updatedAt": "2026-03-19T11:00:00Z"
}

// Authorization: [Authorize(Roles = "Admin")]
// Cho phép update: Pending → Review/Rejected
//                 Review → Resolved/Rejected
```

---

### 5. **POST /api/admin/appeals/{appealID}/unban** (Admin gỡ ban tài khoản)
```csharp
// Request Body
{}

// Response
{
  "success": true,
  "message": "Tài khoản đã được gỡ ban",
  "appealID": 1,
  "email": "user@example.com",
  "status": "Resolved"
}

// Backend Logic:
// 1. Lấy email từ Appeal
// 2. Tìm Account với email đó
// 3. Set Account.IsBanned = false
// 4. Set Appeal.Status = 'Resolved'
// 5. Update UpdatedAt
// 6. Gửi email cho user: "Khiếu nại được chấp nhận, tài khoản đã gỡ ban"
// 7. Gửi notification cho admin: "Đã gỡ ban tài khoản [email]"

// Authorization: [Authorize(Roles = "Admin")]
```

---

### 6. **GET /api/appeals/status** (Người dùng tracking khiếu nại)
```csharp
// Request Query
?email=user@example.com

// Response
{
  "appealID": 1,
  "email": "user@example.com",
  "status": "Pending",  // Hoặc Review, Resolved, Rejected
  "createdAt": "2026-03-19T10:30:00Z",
  "updatedAt": "2026-03-19T11:00:00Z",
  "reason": "..." // Có thể bỏ hoặc hide
}

// Nếu không tìm thấy khiếu nại
{
  "message": "Chưa có khiếu nại nào",
  "appealID": null
}
```

---

## 🔔 WebSocket / Real-time Notifications

Khi user gửi khiếu nại, hệ thống phải gửi **real-time notification đến admin**:

### Backend Implementation
```csharp
// Sau khi lưu Appeal, gửi WebSocket event
var notification = new
{
  notificationID = Guid.NewGuid(),
  title = "🚨 Khiếu nại ban nhầm mới",
  content = $"Người dùng {appeal.Email} gửi khiếu nại",
  type = "APPEAL",  // Type custom
  createdAt = DateTime.UtcNow,
  link = "/admin/appeals"  // Điều hướng admin tới trang quản lý
};

// Broadcast đến tất cả admin online
await _webSocketService.BroadcastToAdmins("admin-alert", notification);

// Hoặc lưu vào Notifications table nếu có
await _notificationService.CreateNotification(
  accountID: adminID,
  title: notification.title,
  content: notification.content,
  type: "APPEAL",
  link: notification.link
);
```

### Frontend sẽ nhận
- Event `admin-alert` hoặc `admin-notification` sẽ trigger
- `HeaderAdmin.vue` sẽ nhận và thêm vào danh sách thông báo
- Hiển thị badge số lượng chưa đọc
- Phát âm thanh thông báo

---

## 📨 Email Templates (Tùy chọn)

### Email khi gửi khiếu nại
```
Tiêu đề: GoMet - Khiếu nại của bạn đã được gửi

Nội dung:
Xin chào [Email],

Chúng tôi đã nhận được khiếu nại của bạn. 
Mã khiếu nại: #[AppealID]

Quản trị viên sẽ xem xét trong vòng 24-48 giờ.
Bạn có thể tracking tại: [link tracking]

Cảm ơn!
```

### Email khi gỡ ban thành công
```
Tiêu đề: ✅ Tài khoản của bạn đã được gỡ ban

Nội dung:
Xin chào [Email],

Khiếu nại của bạn (#[AppealID]) đã được chấp nhận.
Tài khoản của bạn đã được gỡ ban thành công.

Bạn có thể đăng nhập lại ngay bây giờ.
```

---

## 🔐 Bảo Mật & Rate Limiting

1. **Rate Limit:** 1 khiếu nại / email / 24 giờ
   ```csharp
   // Check before insert
   var recentAppeal = db.Appeals
     .Where(a => a.Email == email && a.CreatedAt > DateTime.UtcNow.AddHours(-24))
     .FirstOrDefault();
   
   if (recentAppeal != null)
     return BadRequest("Bạn đã gửi khiếu nại trong 24 giờ qua");
   ```

2. **CAPTCHA:** Bắt CAPTCHA khi gửi (frontend đã có)

3. **Validation:**
   - Email phải hợp lệ
   - Reason >= 20 ký tự
   - Không cho phép HTML/Script

4. **Audit Log:** Lưu IP của người gửi

---

## 📊 Data Flow Diagram

```
User (FE)
   ↓
[AppealModal] → POST /api/appeals
   ↓
Backend API
   ├─ Validate email & reason
   ├─ Check rate limit
   ├─ Save to DB
   ├─ Send WebSocket to admin
   └─ Return appealID
   ↓
Frontend Toast: "Khiếu nại đã được gửi"

---

Admin (FE)
   ↓
HeaderAdmin nhận "admin-alert" event
   ├─ Notification badge +1
   ├─ Phát âm thanh
   └─ Thêm vào danh sách
   ↓
Admin click → AppealManagement.vue
   ├─ GET /api/admin/appeals
   └─ Xem danh sách, search, filter
   ↓
Click "Xem chi tiết" → Modal detail
   ├─ PUT /api/admin/appeals/{id} (cập nhật status/note)
   └─ POST /api/admin/appeals/{id}/unban (gỡ ban)
   ↓
Banned user được email
   └─ Có thể đăng nhập lại
```

---

## 🚀 Implementation Checklist

- [ ] Tạo bảng Appeals
- [ ] Tạo Appeal Entity (C#)
- [ ] Tạo AppealRepository
- [ ] Tạo AppealService với business logic
- [ ] Tạo AppealsController với 6 endpoints trên
- [ ] Thêm notification logic khi gửi khiếu nại
- [ ] Thêm email notifications (tùy chọn)
- [ ] Test tất cả endpoints
- [ ] Deploy & kiểm tra WebSocket realtime

---

## 📝 Lưu Ý

- **Frontend** đã sẵn sàng với:
  - AppealModal (form gửi)
  - AppealManagement (trang quản lý admin)
  - Router + service calls
  
- **Backend** cần xử lý:
  - CRUD endpoints
  - Rate limiting
  - Email/Notification realtime
  - Account unban logic

- **Security:**
  - Luôn check Authorization trên API
  - Validate input
  - Log audit
  - Rate limit

---

**Happy coding! 🎉**
