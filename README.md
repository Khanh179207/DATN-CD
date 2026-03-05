# DATN-CD

## Feature test guide (Maintenance + Weekly Certificates)

### 1) Backend DB migration
- Start backend once so Flyway runs `V6__maintenance_and_weekly_certificates.sql`.
- Verify tables exist in SQL Server: `SystemSetting`, `WeeklyCertificate`.
- Verify default settings:
	- `MAINTENANCE_MODE = 0`
	- `MAINTENANCE_MESSAGE` has default notice.

### 2) Maintenance mode APIs
- Public status: `GET /api/system/settings/public`
- Admin read: `GET /api/admin/system/settings`
- Admin update: `PUT /api/admin/system/settings/maintenance`
	- Body: `{ "enabled": true, "message": "Bảo trì hệ thống" }`

Expected behavior when ON:
- Non-admin/guest API requests return `503` with `{ code: "MAINTENANCE", message: "..." }`.
- Admin API namespace remains accessible for ADMIN accounts.
- Login/register UI in FE is blocked and shows maintenance notice.

### 3) Weekly certificates APIs
- Weekly leaderboard (admin):
	- `GET /api/admin/leaderboard/weekly?weekStart=YYYY-MM-DD&weekEnd=YYYY-MM-DD&top=10`
- Generate certificates (admin/manual):
	- `POST /api/admin/certificates/generate?weekStart=YYYY-MM-DD&weekEnd=YYYY-MM-DD&top=3`
- Admin list certificates:
	- `GET /api/admin/certificates?weekStart=YYYY-MM-DD&weekEnd=YYYY-MM-DD`
- User my certificates:
	- `GET /api/certificates/me?year=2026`

Scoring currently implemented:
- Weekly score uses `History.lastViewedAt` view interactions on approved/active posts.
- Structure is service-based for adding weighted ratings/comments later.

### 4) Frontend routes/screens
- Maintenance page: `/maintenance`
- Admin maintenance: `/admin/maintenance`
- Admin weekly top + generate: `/admin/weekly-certs`
- User certificates section: profile page (`/profile`, own profile only)

### 5) Added automated check
- Backend test file: `GoMet-BE/src/test/java/poly/edu/config/MaintenanceModeFilterTest.java`
	- Verifies non-admin request blocked with `503` in maintenance mode.
	- Verifies admin request is allowed during maintenance mode.
