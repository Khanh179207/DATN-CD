# Hướng dẫn triển khai Backend Appeal System

## 📋 Bước 1: Tạo Appeal Table

1. Mở **SQL Server Management Studio**
2. Kết nối đến database GoMet
3. Chạy script từ file `create-appeals-table.sql`

```sql
CREATE TABLE Appeals (
    appealID INT PRIMARY KEY IDENTITY(1,1),
    email NVARCHAR(100) NOT NULL,
    reason NVARCHAR(2000) NOT NULL,
    status NVARCHAR(20) NOT NULL DEFAULT 'Pending',
    adminNote NVARCHAR(1000),
    createdAt DATETIME NOT NULL DEFAULT GETDATE(),
    updatedAt DATETIME,
    ipAddress NVARCHAR(50),
    reviewedByAdminID INT
);

CREATE INDEX IDX_Appeals_Email ON Appeals(email);
CREATE INDEX IDX_Appeals_Status ON Appeals(status);
CREATE INDEX IDX_Appeals_CreatedAt ON Appeals(createdAt DESC);
```

## 🔧 Bước 2: Backend Files Created

Các file đã tạo:

1. **Entity**: `src/main/java/poly/edu/entity/Appeal.java`
   - JPA entity map với bảng Appeals

2. **DAO**: `src/main/java/poly/edu/dao/AppealDAO.java`
   - JpaRepository interface
   - Custom queries for appeals

3. **DTO**: `src/main/java/poly/edu/dto/AppealDTO.java`
   - Data transfer object cho API responses

4. **Service Interface**: `src/main/java/poly/edu/service/AppealService.java`
   - Service contract

5. **Service Implementation**: `src/main/java/poly/edu/service/impl/AppealServiceImpl.java`
   - Business logic implementation
   - Rate limiting (1 appeal per email per 24h)
   - Account unban logic

6. **Controller**: `src/main/java/poly/edu/controller/AppealController.java`
   - 5 REST endpoints

## 🌐 API Endpoints

### 1. **POST /api/appeals** - Create Appeal (Public)
```bash
curl -X POST http://localhost:8080/api/appeals \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "reason": "Tôi bị ban nhầm vì lỗi hệ thống"
  }'
```

**Response** (201 Created):
```json
{
  "success": true,
  "message": "Khiếu nại đã được tạo thành công",
  "data": {
    "appealID": 1,
    "email": "user@example.com",
    "reason": "Tôi bị ban nhầm vì lỗi hệ thống",
    "status": "Pending",
    "createdAt": "2026-03-19T10:30:00"
  }
}
```

### 2. **GET /api/appeals/status?email=user@example.com** - Get Status (Public)
```bash
curl http://localhost:8080/api/appeals/status?email=user@example.com
```

**Response** (200 OK):
```json
{
  "success": true,
  "data": {
    "appealID": 1,
    "email": "user@example.com",
    "status": "Pending",
    "createdAt": "2026-03-19T10:30:00"
  }
}
```

### 3. **GET /api/admin/appeals** - List All Appeals (Admin Only)
```bash
curl -H "Authorization: Bearer {ADMIN_TOKEN}" \
  http://localhost:8080/api/admin/appeals
```

**Response** (200 OK):
```json
{
  "success": true,
  "data": [
    {
      "appealID": 1,
      "email": "user@example.com",
      "reason": "Tôi bị ban nhầm vì lỗi hệ thống",
      "status": "Pending",
      "createdAt": "2026-03-19T10:30:00"
    }
  ],
  "count": 1
}
```

### 4. **PUT /api/admin/appeals/{appealID}** - Update Status (Admin Only)
```bash
curl -X PUT -H "Authorization: Bearer {ADMIN_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "Review",
    "note": "Đang kiểm tra lại logs"
  }' \
  http://localhost:8080/api/admin/appeals/1
```

**Response** (200 OK):
```json
{
  "success": true,
  "message": "Cập nhật khiếu nại thành công",
  "data": {
    "appealID": 1,
    "status": "Review",
    "adminNote": "Đang kiểm tra lại logs",
    "updatedAt": "2026-03-19T11:00:00"
  }
}
```

### 5. **POST /api/admin/appeals/{appealID}/unban** - Unban Account (Admin Only)
```bash
curl -X POST -H "Authorization: Bearer {ADMIN_TOKEN}" \
  http://localhost:8080/api/admin/appeals/1/unban
```

**Response** (200 OK):
```json
{
  "success": true,
  "message": "Tài khoản đã được gỡ ban thành công",
  "data": true
}
```

## ⚙️ Configuration

### Security Requirements
- Endpoints `/admin/*` require `@PreAuthorize("hasRole('ADMIN')")`
- Make sure Spring Security is configured with proper JWT/role validation

### Rate Limiting
- 1 appeal per email per 24 hours
- Enforced by `AppealService.canCreateAppeal()`

### Cross-Origin
- `@CrossOrigin(origins = "*", maxAge = 3600)` allows frontend requests

## 🧪 Testing in Postman

1. Create Appeal
   - POST to `/api/appeals`
   - Body: `{"email": "test@test.com", "reason": "..."}`

2. Get Status
   - GET to `/api/appeals/status?email=test@test.com`

3. Admin: List All
   - GET to `/api/admin/appeals`
   - Add JWT token in Authorization header

4. Admin: Update
   - PUT to `/api/admin/appeals/1`
   - Body: `{"status": "Review", "note": "..."}`

5. Admin: Unban
   - POST to `/api/admin/appeals/1/unban`

## 📝 Next Steps

1. **Database**: Run SQL script to create Appeals table
2. **Compile**: `mvn clean compile`
3. **Test**: Run Postman tests
4. **Frontend**: Frontend will now work with these endpoints
5. **Enhancement**: Implement email notifications when appeals are reviewed

## 🔗 Related Files

- Frontend Service: `GoMet-FE/src/services/appealService.js`
- Frontend Modal: `GoMet-FE/src/components/modals/AppealModal.vue`
- Frontend Admin: `GoMet-FE/src/pages/admin/AppealManagement.vue`
- SQL Script: `create-appeals-table.sql`

## ✅ Verification

After deployment, verify by:

1. Frontend sends appeal
2. Check console logs - should show success (not 404)
3. Query database: `SELECT * FROM Appeals;`
4. Admin page shows appeal in list

---

**Created**: 2026-03-19
**Version**: 1.0
