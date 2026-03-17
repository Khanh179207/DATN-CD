# Support Ticket Real-Time Notification Implementation

## Approved Plan Steps:

### 1. ✅ Create this TODO.md (done)
### 2. ✅ Injected NotificationService into TicketServiceImpl.java
### 3. ✅ Added notification logic in updateTicketStatus() (detects changes to 1/ACCEPTED, 2/RESOLVED, 3/REJECTED, creates notification with link "/user/tickets/{id}", auto-sends real-time via existing service)
- Capture oldStatus before setStatus(newStatus)
- After save, if oldStatus != newStatus && newStatus in [1,2,3]:
  - String link = "/user/tickets/" + ticketId;
  - notificationService.createNotification(title, content, type, ticket.getAccount().getAccountID(), null, link);
### 4. ☐ Test changes
### 5. ☐ attempt_completion

