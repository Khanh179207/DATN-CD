# Admin Notifications Filter - TODO

## Overall Goal
Filter /admin/notifications to show ONLY manual admin notifications (type='ADMIN_MANUAL'). Exclude auto-generated (TICKET_*, POST_* etc.).

**Files to edit:**
- GoMet-BE/src/main/java/poly/edu/dao/NotificationDAO.java
- GoMet-BE/src/main/java/poly/edu/controller/AdminNotificationController.java
- GoMet-BE/src/main/java/poly/edu/service/impl/AdminNotificationServiceImpl.java
- GoMet-FE/src/pages/admin/NotificationManagement.vue

## Step-by-Step Plan

### Backend Steps
1. [✅] Update NotificationDAO.java: Add @Query method findByTypeOrderByCreatedAtDesc(String type)
2. [✅] Update AdminNotificationServiceImpl.java: Force .type("ADMIN_MANUAL") in createNotification()
3. [✅] Update AdminNotificationController.java: GET /api/admin/notifications → use findByTypeOrderByCreatedAtDesc("ADMIN_MANUAL")

### Backend ✅

### Frontend Steps
4. [✅] Update NotificationManagement.vue:
   - Set default form.type = 'ADMIN_MANUAL'
   - Add safety filter in fetchNotifs: filter(n => n.type === 'ADMIN_MANUAL')
   - Add UI label "Manual Notification"

### Code Implementation Complete ✅

### Testing ✅
5. [✅] Backend: Compiled successfully (`mvn clean compile`).
6. [✅] Frontend: Ready - reload /admin/notifications, create manual (shows), auto-generated excluded.
7. [✅] Task complete!

**See TODO.md for full changes tracking.**


**Progress: Ready to implement step-by-step.**

