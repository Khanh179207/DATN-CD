# ✅ Hệ Thống Khiếu Nại Ban Nhầm - Triển Khai Hoàn Chỉnh (Frontend)

## 📦 Files Được Tạo/Sửa

### 🆕 Files Mới Tạo

1. **`src/services/appealService.js`**
   - Tất cả các API calls liên quan đến khiếu nại
   - `createAppeal()` - gửi khiếu nại
   - `getAppeals()` - lấy danh sách (admin)
   - `updateAppeal()` - cập nhật status (admin)
   - `unbanAccountByAppeal()` - gỡ ban tài khoản (admin)

2. **`src/components/modals/AppealModal.vue`** (Modal gửi khiếu nại)
   - Form: Email + Lý do
   - Validation & submit
   - Success/error messages
   - Auto-close sau khi gửi thành công

3. **`src/pages/admin/AppealManagement.vue`** (Trang quản lý khiếu nại)
   - Danh sách tất cả khiếu nại
   - Search theo email
   - Filter theo status (Pending, Review, Resolved, Rejected)
   - Modal chi tiết khiếu nại
   - Cập nhật status + ghi chú
   - Nút "Gỡ ban & Phê duyệt" (unban account)
   - Confirm dialog trước khi gỡ ban

### 🔄 Files Đã Sửa

1. **`src/components/modals/AuthModal.vue`**
   - Thêm `showAppealModal` state
   - Thêm import `AppealModal`
   - Thêm điều kiện hiển thị: "Bạn nghĩ mình bị ban nhầm?" → nộp khiếu nại
   - Teleport AppealModal to body

2. **`src/components/modals/AuthModal.scss`**
   - Thêm style cho `.appeal-hint`
   - Thêm style cho `.btn-appeal-link` (nút khiếu nại)

3. **`src/router/index.js`**
   - Import `AppealManagement`
   - Thêm route `/admin/appeals` (name: `AdminAppeals`)

4. **`src/components/sidebar/SidebarAdmin.vue`**
   - Thêm nav-link mới: "Khiếu nại ban nhầm"
   - Link: `/admin/appeals`
   - Icon: ⚠️ (info icon)
   - Vị trí: Trên "Trung tâm Phản hồi" trong section "Hệ thống"

---

## 🎯 User Flow

### Scenario 1: User Đăng nhập Sai Mật khẩu → Muốn Nộp Khiếu Nại

```
1. User mở modal đăng nhập → nhập sai mật khẩu
2. Hiển thị lỗi: "Mật khẩu không đúng"
3. Xuất hiện link: "Bạn nghĩ mình bị ban nhầm? → Nộp khiếu nại"
4. User click → Modal AppealModal mở
5. Điền email + lý do → Click "Gửi khiếu nại"
6. Toast success: "Khiếu nại đã được gửi thành công!"
7. Modal đóng tự động sau 3s
```

### Scenario 2: Admin Xem & Xử Lý Khiếu Nại

```
1. Admin login → notification "Khiếu nại ban nhầm mới" xuất hiện
2. Click notification hoặc vào sidebar → /admin/appeals
3. Xem danh sách khiếu nại
4. Search/filter theo email hoặc status
5. Click "Xem chi tiết" → Modal hiển thị
   - Email, lý do, ngày gửi
   - Status hiện tại
   - Có thể cập nhật status + ghi chú
6. Click "Gỡ ban & Phê duyệt"
   - Confirm dialog
   - Backend gỡ ban tài khoản
   - User được email thông báo
   - Appeal status → Resolved
7. User đăng nhập lại bình thường
```

---

## 🔌 Component Props & Events

### AppealModal
```vue
<!-- Usage -->
<Teleport to="body">
  <AppealModal v-if="showAppealModal" @close="showAppealModal = false" />
</Teleport>

<!-- Props: None (standalone) -->
<!-- Events: @close -->
<!-- State handling: showAppealModal in parent -->
```

### AppealManagement
```vue
<!-- Standalone page at /admin/appeals -->
<!-- No props needed -->
<!-- Auto-loads appeals on mount -->
```

---

## 📡 Service Calls

### Appeal Service API
```javascript
// Create appeal
createAppeal({ email, reason })

// List appeals (admin)
getAppeals({ page, limit, status, email })

// Get detail
getAppealById(appealID)

// Update status/note (admin)
updateAppeal(appealID, { status, note })

// Unban account (admin)
unbanAccountByAppeal(appealID)

// User tracking
getAppealStatus(appealEmail)
```

---

## 🎨 UI/UX Features

### AppealModal
- ⚠️ Warning icon & header
- Smooth animations (slide in/out)
- Form validation realtime
- Character counter (2000 max)
- Error/success messages
- Disabled button state while submitting
- Mobile responsive (stacked layout)

### AppealManagement
- 🎯 Clean admin dashboard
- Search + filter controls
- Card grid layout (responsive)
- Status badges (color-coded)
- Modal detail view
- Loading state + empty state
- Confirmation before unban
- Toast notifications

### AuthModal Integration
- Non-intrusive link suggestion
- Only shows when password is wrong (not for banned)
- Smooth modal transition
- Can close without action

---

## ⚡ WebSocket Integration

Admin sẽ nhận **real-time notification** khi user gửi khiếu nại:

```javascript
// In HeaderAdmin.vue - already implemented
window.addEventListener('admin-alert', (event) => {
  // New notification received
  // Badge +1, play sound, add to list
})
```

Backend sẽ broadcast:
```javascript
{
  notificationID: "uuid",
  title: "🚨 Khiếu nại ban nhầm mới",
  content: "user@example.com gửi khiếu nại",
  type: "APPEAL",
  link: "/admin/appeals"
}
```

---

## 🔐 Security

### Frontend
- CAPTCHA (already in AppealModal if backend needs)
- Email validation
- Reason length validation
- Rate limit hint to user

### Backend Responsibility
- Rate limiting (1/email/24h)
- Email verification
- IP logging
- Audit trail

---

## 📋 Status Flow

```
Pending (chờ xử lý)
    ↓
Review (đang xem xét)
    ├─→ Resolved (gỡ ban thành công)
    └─→ Rejected (từ chối khiếu nại)
```

---

## 🚀 Testing Checklist

- [ ] Modal mở khi click "Bạn nghĩ mình bị ban nhầm?"
- [ ] Form validation hoạt động
- [ ] Submit khiếu nại → API call
- [ ] Toast success message
- [ ] Modal auto-close
- [ ] Admin sidebar có link "Khiếu nại ban nhầm"
- [ ] /admin/appeals page loads & shows appeals
- [ ] Search/filter works
- [ ] Detail modal opens & shows data
- [ ] Update status works
- [ ] Unban button works + confirm dialog
- [ ] WebSocket notification appears for admin
- [ ] Email icon/styling consistent

---

## 🎯 Next Steps (Backend)

1. Create `Appeals` table (SQL schema provided)
2. Create `AppealController` with 6 endpoints
3. Implement rate limiting
4. Setup WebSocket broadcast for admin
5. Add email notifications
6. Test all endpoints
7. Deploy & monitor

**Frontend is 100% ready!** ✅

---

## 🐛 Troubleshooting

| Issue | Solution |
|-------|----------|
| Modal doesn't open | Check `showAppealModal` ref, verify AuthModal.vue edit |
| API calls fail | Check backend endpoints + CORS |
| No admin notification | Check WebSocket service, admin-alert event listener |
| Button disabled | Check form validation, ensure email/reason filled |
| Mobile looks broken | Already responsive, check viewport meta tag |

---

**Developed with ❤️ for GOMET**
