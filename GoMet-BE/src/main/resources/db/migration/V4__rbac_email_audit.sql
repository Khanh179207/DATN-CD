-- ============================================================
-- V4: RBAC + Email Queue + Admin Audit Log
-- DB: SQL Server (MSSQL) — all statements idempotent
-- ============================================================

-- ─── 1. roles ─────────────────────────────────────────────────────────────────
IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'roles')
BEGIN
    CREATE TABLE roles (
        id          INT           IDENTITY(1,1) PRIMARY KEY,
        name        NVARCHAR(50)  NOT NULL,
        description NVARCHAR(255) NULL,
        created_at  DATETIME2     NOT NULL DEFAULT GETUTCDATE(),
        CONSTRAINT UQ_roles_name UNIQUE (name)
    );

    INSERT INTO roles (name, description) VALUES
        ('SUPER_ADMIN',  'Full system access including role management'),
        ('MODERATOR',    'Post/comment moderation and report handling'),
        ('SUPPORT',      'User support: view users, send emails, reset flags'),
        ('ANALYST',      'Read-only analytics and metrics access');
END;
GO

-- ─── 2. permissions ───────────────────────────────────────────────────────────
IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'permissions')
BEGIN
    CREATE TABLE permissions (
        id          INT           IDENTITY(1,1) PRIMARY KEY,
        code        NVARCHAR(80)  NOT NULL,
        description NVARCHAR(255) NULL,
        CONSTRAINT UQ_permissions_code UNIQUE (code)
    );

    INSERT INTO permissions (code, description) VALUES
        ('USER_READ',          'View user profiles and lists'),
        ('USER_WRITE',         'Edit user data and flags'),
        ('USER_BAN',           'Temporarily ban users'),
        ('USER_BAN_PERMANENT', 'Permanently ban users (SUPER_ADMIN only)'),
        ('POST_MODERATE',      'Approve / reject / hide posts'),
        ('REPORT_HANDLE',      'Resolve or dismiss content reports'),
        ('EMAIL_SEND',         'Create and send email blasts to users'),
        ('AUDIT_READ',         'View admin audit log entries'),
        ('ROLE_MANAGE',        'Assign and revoke roles (SUPER_ADMIN only)'),
        ('SETTINGS_WRITE',     'Change system settings and feature flags'),
        ('STATS_VIEW',         'View analytics dashboard');
END;
GO

-- ─── 3. role_permissions ──────────────────────────────────────────────────────
IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'role_permissions')
BEGIN
    CREATE TABLE role_permissions (
        role_id       INT NOT NULL,
        permission_id INT NOT NULL,
        CONSTRAINT PK_role_permissions PRIMARY KEY (role_id, permission_id),
        CONSTRAINT FK_rp_role       FOREIGN KEY (role_id)       REFERENCES roles(id)       ON DELETE CASCADE,
        CONSTRAINT FK_rp_permission FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
    );

    -- SUPER_ADMIN: all permissions
    INSERT INTO role_permissions (role_id, permission_id)
    SELECT r.id, p.id FROM roles r CROSS JOIN permissions p WHERE r.name = 'SUPER_ADMIN';

    -- MODERATOR: post/report/user-read
    INSERT INTO role_permissions (role_id, permission_id)
    SELECT r.id, p.id FROM roles r JOIN permissions p ON p.code IN (
        'USER_READ','POST_MODERATE','REPORT_HANDLE','AUDIT_READ','STATS_VIEW'
    ) WHERE r.name = 'MODERATOR';

    -- SUPPORT: user-read, email, ban-temp
    INSERT INTO role_permissions (role_id, permission_id)
    SELECT r.id, p.id FROM roles r JOIN permissions p ON p.code IN (
        'USER_READ','USER_WRITE','USER_BAN','EMAIL_SEND','AUDIT_READ'
    ) WHERE r.name = 'SUPPORT';

    -- ANALYST: read-only
    INSERT INTO role_permissions (role_id, permission_id)
    SELECT r.id, p.id FROM roles r JOIN permissions p ON p.code IN (
        'USER_READ','AUDIT_READ','STATS_VIEW'
    ) WHERE r.name = 'ANALYST';
END;
GO

-- ─── 4. account_roles ─────────────────────────────────────────────────────────
IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'account_roles')
BEGIN
    CREATE TABLE account_roles (
        account_id INT NOT NULL,
        role_id    INT NOT NULL,
        assigned_by INT NULL,
        assigned_at DATETIME2 NOT NULL DEFAULT GETUTCDATE(),
        CONSTRAINT PK_account_roles PRIMARY KEY (account_id, role_id),
        CONSTRAINT FK_ar_account  FOREIGN KEY (account_id)  REFERENCES Account(AccountID) ON DELETE CASCADE,
        CONSTRAINT FK_ar_role     FOREIGN KEY (role_id)     REFERENCES roles(id)           ON DELETE CASCADE,
        CONSTRAINT FK_ar_assigned FOREIGN KEY (assigned_by) REFERENCES Account(AccountID)
    );

    CREATE INDEX IX_account_roles_account ON account_roles (account_id);
    CREATE INDEX IX_account_roles_role    ON account_roles (role_id);

    -- Seed: existing admins (isAdmin=1) get SUPER_ADMIN role
    INSERT INTO account_roles (account_id, role_id)
    SELECT a.AccountID, r.id
    FROM Account a CROSS JOIN roles r
    WHERE a.isAdmin = 1 AND r.name = 'SUPER_ADMIN'
      AND NOT EXISTS (
          SELECT 1 FROM account_roles ar
          WHERE ar.account_id = a.AccountID AND ar.role_id = r.id
      );
END;
GO

-- ─── 5. admin_audit_logs ──────────────────────────────────────────────────────
IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'admin_audit_logs')
BEGIN
    CREATE TABLE admin_audit_logs (
        id              BIGINT        IDENTITY(1,1) PRIMARY KEY,
        actor_id        INT           NOT NULL,        -- admin who did the action
        action_type     NVARCHAR(80)  NOT NULL,        -- e.g. EMAIL_JOB_QUEUED
        target_type     NVARCHAR(50)  NULL,            -- USER | POST | EMAIL_JOB | SYSTEM
        target_id       NVARCHAR(100) NULL,            -- FK-agnostic string id
        meta_json       NVARCHAR(MAX) NULL,            -- arbitrary context
        ip              NVARCHAR(45)  NULL,
        user_agent      NVARCHAR(512) NULL,
        created_at      DATETIME2     NOT NULL DEFAULT GETUTCDATE(),

        CONSTRAINT FK_aal_actor FOREIGN KEY (actor_id) REFERENCES Account(AccountID)
    );

    CREATE INDEX IX_aal_actor_created  ON admin_audit_logs (actor_id,     created_at DESC);
    CREATE INDEX IX_aal_action_created ON admin_audit_logs (action_type,  created_at DESC);
    CREATE INDEX IX_aal_target         ON admin_audit_logs (target_type,  target_id);
END;
GO

-- ─── 6. email_jobs ────────────────────────────────────────────────────────────
IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'email_jobs')
BEGIN
    CREATE TABLE email_jobs (
        id               BIGINT         IDENTITY(1,1) PRIMARY KEY,
        sender_admin_id  INT            NOT NULL,
        subject          NVARCHAR(500)  NOT NULL,
        body_html        NVARCHAR(MAX)  NULL,
        body_text        NVARCHAR(MAX)  NULL,
        status           NVARCHAR(20)   NOT NULL DEFAULT 'DRAFT',   -- DRAFT|QUEUED|SENDING|SENT|FAILED|CANCELED
        fail_reason      NVARCHAR(MAX)  NULL,
        recipient_count  INT            NOT NULL DEFAULT 0,
        sent_count       INT            NOT NULL DEFAULT 0,
        failed_count     INT            NOT NULL DEFAULT 0,
        created_at       DATETIME2      NOT NULL DEFAULT GETUTCDATE(),
        updated_at       DATETIME2      NOT NULL DEFAULT GETUTCDATE(),
        queued_at        DATETIME2      NULL,
        sent_at          DATETIME2      NULL,

        CONSTRAINT FK_ej_sender FOREIGN KEY (sender_admin_id) REFERENCES Account(AccountID)
    );

    CREATE INDEX IX_ej_status_created ON email_jobs (status, created_at DESC);
    CREATE INDEX IX_ej_sender_created ON email_jobs (sender_admin_id, created_at DESC);
END;
GO

-- ─── 7. email_job_recipients ──────────────────────────────────────────────────
IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'email_job_recipients')
BEGIN
    CREATE TABLE email_job_recipients (
        id           BIGINT        IDENTITY(1,1) PRIMARY KEY,
        job_id       BIGINT        NOT NULL,
        user_id      INT           NULL,                -- nullable: can send to external email
        email        NVARCHAR(255) NOT NULL,
        status       NVARCHAR(20)  NOT NULL DEFAULT 'QUEUED',   -- QUEUED|SENT|FAILED|SKIPPED
        fail_reason  NVARCHAR(MAX) NULL,
        sent_at      DATETIME2     NULL,

        CONSTRAINT FK_ejr_job  FOREIGN KEY (job_id)  REFERENCES email_jobs(id) ON DELETE CASCADE,
        CONSTRAINT FK_ejr_user FOREIGN KEY (user_id) REFERENCES Account(AccountID) ON DELETE SET NULL
    );

    CREATE INDEX IX_ejr_job_id ON email_job_recipients (job_id);
    CREATE INDEX IX_ejr_status ON email_job_recipients (status);
    CREATE INDEX IX_ejr_user   ON email_job_recipients (user_id);
END;
GO

-- ─── 8. admin_email_rate_events (DB-based rate limiting for admin email sends) ─
IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'admin_email_rate_events')
BEGIN
    CREATE TABLE admin_email_rate_events (
        id          BIGINT    IDENTITY(1,1) PRIMARY KEY,
        admin_id    INT       NOT NULL,
        created_at  DATETIME2 NOT NULL DEFAULT GETUTCDATE(),

        CONSTRAINT FK_aere_admin FOREIGN KEY (admin_id) REFERENCES Account(AccountID) ON DELETE CASCADE
    );

    CREATE INDEX IX_aere_admin_created ON admin_email_rate_events (admin_id, created_at DESC);
END;
GO
