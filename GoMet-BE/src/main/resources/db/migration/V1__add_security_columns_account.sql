-- ============================================================
-- V1: Add enterprise-security columns to Account table
-- DB: SQL Server (MSSQL) / Spring Boot 4.x
-- ============================================================

-- password_updated_at: tracks when password was last changed (used for session invalidation)
IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'Account' AND COLUMN_NAME = 'password_updated_at'
)
    ALTER TABLE Account ADD password_updated_at DATETIME2 NULL;

-- failed_login_count: incremented on each failed login attempt; reset on success
IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'Account' AND COLUMN_NAME = 'failed_login_count'
)
    ALTER TABLE Account ADD failed_login_count INT NOT NULL DEFAULT 0;

-- lock_until: account locked until this timestamp (progressive lockout)
IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'Account' AND COLUMN_NAME = 'lock_until'
)
    ALTER TABLE Account ADD lock_until DATETIME2 NULL;

-- mfa_enabled: 0 = disabled, 1 = TOTP enabled
IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'Account' AND COLUMN_NAME = 'mfa_enabled'
)
    ALTER TABLE Account ADD mfa_enabled INT NOT NULL DEFAULT 0;

-- mfa_secret_encrypted: AES-256 encrypted TOTP secret (Base64 encoded)
IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'Account' AND COLUMN_NAME = 'mfa_secret_encrypted'
)
    ALTER TABLE Account ADD mfa_secret_encrypted NVARCHAR(512) NULL;

-- mfa_secret_created_at: when the MFA secret was generated
IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'Account' AND COLUMN_NAME = 'mfa_secret_created_at'
)
    ALTER TABLE Account ADD mfa_secret_created_at DATETIME2 NULL;

-- last_login_at: UTC timestamp of last successful login
IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'Account' AND COLUMN_NAME = 'last_login_at'
)
    ALTER TABLE Account ADD last_login_at DATETIME2 NULL;

-- last_login_ip: IP of last successful login (IPv4 or IPv6, max 45 chars)
IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'Account' AND COLUMN_NAME = 'last_login_ip'
)
    ALTER TABLE Account ADD last_login_ip NVARCHAR(45) NULL;

-- last_login_device_id: fingerprint of last device used
IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'Account' AND COLUMN_NAME = 'last_login_device_id'
)
    ALTER TABLE Account ADD last_login_device_id NVARCHAR(255) NULL;

-- bio column may not exist yet (entity has it, but old DB might not)
IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'Account' AND COLUMN_NAME = 'bio'
)
    ALTER TABLE Account ADD bio NVARCHAR(500) NULL;

-- PasswordResetToken: add created_ua_hash column if missing
IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'PasswordResetToken' AND COLUMN_NAME = 'created_ua_hash'
)
    ALTER TABLE PasswordResetToken ADD created_ua_hash NVARCHAR(64) NULL;

-- Index on Account(lock_until) for quick lockout checks
IF NOT EXISTS (
    SELECT 1 FROM sys.indexes
    WHERE object_id = OBJECT_ID('Account') AND name = 'IX_Account_lock_until'
)
    CREATE INDEX IX_Account_lock_until ON Account (lock_until);
