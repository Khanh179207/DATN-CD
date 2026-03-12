-- ============================================================
-- V2: Create enterprise-security tables
-- DB: SQL Server (MSSQL)
-- ALL tables use Instant → DATETIME2, BIGINT PK IDENTITY
-- ============================================================

-- ─── trusted_devices ─────────────────────────────────────────
IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'trusted_devices')
BEGIN
    CREATE TABLE trusted_devices (
        id                BIGINT IDENTITY(1,1) PRIMARY KEY,
        user_id           INT NOT NULL,
        device_id         NVARCHAR(255) NOT NULL,       -- client-generated fingerprint
        device_name       NVARCHAR(255) NULL,
        user_agent_hash   NVARCHAR(64)  NOT NULL,       -- SHA-256 of raw UA
        first_seen_at     DATETIME2     NOT NULL,
        last_seen_at      DATETIME2     NOT NULL,
        last_ip           NVARCHAR(45)  NULL,
        is_trusted        BIT           NOT NULL DEFAULT 1,
        revoked_at        DATETIME2     NULL,

        CONSTRAINT FK_trusted_devices_account
            FOREIGN KEY (user_id) REFERENCES Account(AccountID) ON DELETE CASCADE
    );

    CREATE UNIQUE INDEX UX_trusted_devices_user_device
        ON trusted_devices (user_id, device_id);

    CREATE INDEX IX_trusted_devices_device_id
        ON trusted_devices (device_id);
END;
GO

-- ─── login_attempts ──────────────────────────────────────────
IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'login_attempts')
BEGIN
    CREATE TABLE login_attempts (
        id              BIGINT IDENTITY(1,1) PRIMARY KEY,
        user_id         INT           NULL,             -- NULL when account not found
        identifier      NVARCHAR(255) NOT NULL,          -- email or username used
        ip              NVARCHAR(45)  NOT NULL,
        user_agent_hash NVARCHAR(64)  NULL,
        attempt_type    NVARCHAR(50)  NOT NULL,          -- e.g. PASSWORD, OTP, MFA, GOOGLE
        success         BIT           NOT NULL DEFAULT 0,
        failure_reason  NVARCHAR(100) NULL,
        created_at      DATETIME2     NOT NULL
    );

    CREATE INDEX IX_login_attempts_user_created
        ON login_attempts (user_id, created_at);

    CREATE INDEX IX_login_attempts_identifier_created
        ON login_attempts (identifier, created_at);

    CREATE INDEX IX_login_attempts_ip_created
        ON login_attempts (ip, created_at);
END;
GO

-- ─── magic_link_tokens ───────────────────────────────────────
IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'magic_link_tokens')
BEGIN
    CREATE TABLE magic_link_tokens (
        id              BIGINT IDENTITY(1,1) PRIMARY KEY,
        user_id         INT           NOT NULL,
        token_hash      NVARCHAR(64)  NOT NULL,          -- SHA-256 hex, unique
        purpose         NVARCHAR(50)  NOT NULL,          -- VERIFY_LOGIN, THIS_WASNT_ME
        expires_at      DATETIME2     NOT NULL,
        used_at         DATETIME2     NULL,
        created_at      DATETIME2     NOT NULL,
        created_ip      NVARCHAR(45)  NULL,
        created_ua_hash NVARCHAR(64)  NULL,

        CONSTRAINT FK_magic_link_tokens_account
            FOREIGN KEY (user_id) REFERENCES Account(AccountID) ON DELETE CASCADE
    );

    CREATE UNIQUE INDEX UX_magic_link_tokens_hash
        ON magic_link_tokens (token_hash);

    CREATE INDEX IX_magic_link_tokens_user_created
        ON magic_link_tokens (user_id, created_at);
END;
GO

-- ─── refresh_tokens ──────────────────────────────────────────
IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'refresh_tokens')
BEGIN
    CREATE TABLE refresh_tokens (
        id                  BIGINT IDENTITY(1,1) PRIMARY KEY,
        user_id             INT           NOT NULL,
        token_hash          NVARCHAR(64)  NOT NULL,      -- SHA-256 of raw refresh token
        device_id           NVARCHAR(255) NULL,
        ip                  NVARCHAR(45)  NULL,
        user_agent          NVARCHAR(512) NULL,
        created_at          DATETIME2     NOT NULL,
        expires_at          DATETIME2     NOT NULL,
        revoked_at          DATETIME2     NULL,
        replaced_by_token_id BIGINT       NULL,           -- chain for reuse detection
        last_used_at        DATETIME2     NULL,

        CONSTRAINT FK_refresh_tokens_account
            FOREIGN KEY (user_id) REFERENCES Account(AccountID) ON DELETE CASCADE
    );

    CREATE UNIQUE INDEX UX_refresh_tokens_hash
        ON refresh_tokens (token_hash);

    CREATE INDEX IX_refresh_tokens_user_created
        ON refresh_tokens (user_id, created_at);

    CREATE INDEX IX_refresh_tokens_device_id
        ON refresh_tokens (device_id);
END;
GO

-- ─── backup_codes (TOTP fallback) ────────────────────────────
IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'backup_codes')
BEGIN
    CREATE TABLE backup_codes (
        id          BIGINT IDENTITY(1,1) PRIMARY KEY,
        user_id     INT          NOT NULL,
        code_hash   NVARCHAR(64) NOT NULL,              -- SHA-256 of raw backup code
        created_at  DATETIME2    NOT NULL,
        used_at     DATETIME2    NULL,

        CONSTRAINT FK_backup_codes_account
            FOREIGN KEY (user_id) REFERENCES Account(AccountID) ON DELETE CASCADE
    );

    CREATE INDEX IX_backup_codes_user
        ON backup_codes (user_id);
END;
GO

-- ─── audit_logs ──────────────────────────────────────────────
IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'audit_logs')
BEGIN
    CREATE TABLE audit_logs (
        id              BIGINT IDENTITY(1,1) PRIMARY KEY,
        user_id         INT           NULL,              -- nullable: unauthenticated events
        event_type      NVARCHAR(100) NOT NULL,
        event_meta_json NVARCHAR(MAX) NULL,              -- JSON blob of extra context
        ip              NVARCHAR(45)  NULL,
        user_agent      NVARCHAR(512) NULL,
        created_at      DATETIME2     NOT NULL
    );

    CREATE INDEX IX_audit_logs_user_created
        ON audit_logs (user_id, created_at);

    CREATE INDEX IX_audit_logs_event_type
        ON audit_logs (event_type, created_at);
END;
GO
