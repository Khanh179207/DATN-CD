-- ============================================================
-- V3: Post Moderation Workflow + Anti-Spam tables
-- DB: SQL Server (MSSQL)
-- ============================================================

-- ─── 1. ALTER Post table – add moderation columns ─────────────────────────

IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'Post' AND COLUMN_NAME = 'status'
)
    ALTER TABLE Post ADD status NVARCHAR(30) NULL;
GO

-- Back-fill: posts already approved get APPROVED, deactivated get HIDDEN
UPDATE Post SET status = 'APPROVED'       WHERE isApproved = 1 AND isActive = 1;
GO
UPDATE Post SET status = 'HIDDEN'         WHERE isActive   = 0;
GO
-- Catch-all: remaining posts (pending review state) get PENDING_REVIEW
UPDATE Post SET status = 'PENDING_REVIEW' WHERE status IS NULL;
GO

IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'Post' AND COLUMN_NAME = 'rejection_reason'
)
    ALTER TABLE Post ADD rejection_reason NVARCHAR(MAX) NULL;
GO

IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'Post' AND COLUMN_NAME = 'rejection_code'
)
    ALTER TABLE Post ADD rejection_code NVARCHAR(50) NULL;
GO

IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'Post' AND COLUMN_NAME = 'moderated_by_id'
)
    ALTER TABLE Post ADD moderated_by_id INT NULL;
GO

IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'Post' AND COLUMN_NAME = 'moderated_at'
)
    ALTER TABLE Post ADD moderated_at DATETIME2 NULL;
GO

IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'Post' AND COLUMN_NAME = 'hidden_at'
)
    ALTER TABLE Post ADD hidden_at DATETIME2 NULL;
GO

IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'Post' AND COLUMN_NAME = 'spam_score'
)
    ALTER TABLE Post ADD spam_score INT NULL;
GO
-- Back-fill spam_score for existing rows
UPDATE Post SET spam_score = 0 WHERE spam_score IS NULL;
GO

IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'Post' AND COLUMN_NAME = 'spam_reasons'
)
    ALTER TABLE Post ADD spam_reasons NVARCHAR(MAX) NULL;
GO

IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'Post' AND COLUMN_NAME = 'updated_at'
)
    ALTER TABLE Post ADD updated_at DATETIME2 NULL;
GO

-- Add FK only if it doesn't exist
IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS
    WHERE CONSTRAINT_NAME = 'FK_Post_moderated_by'
)
    ALTER TABLE Post
        ADD CONSTRAINT FK_Post_moderated_by
            FOREIGN KEY (moderated_by_id) REFERENCES Account(AccountID);
GO

-- Indexes on Post
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'IX_Post_status_createdAt')
    CREATE INDEX IX_Post_status_createdAt ON Post (status, createdAt DESC);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'IX_Post_AccountID_createdAt')
    CREATE INDEX IX_Post_AccountID_createdAt ON Post (AccountID, createdAt DESC);
GO

-- ─── 2. moderation_actions ────────────────────────────────────────────────────

IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'moderation_actions')
BEGIN
    CREATE TABLE moderation_actions (
        id            BIGINT        IDENTITY(1,1) PRIMARY KEY,
        post_id       INT            NOT NULL,
        admin_id      INT            NOT NULL,
        action        NVARCHAR(30)   NOT NULL,    -- APPROVE|REJECT|HIDE|UNHIDE|FLAG_SPAM
        reason        NVARCHAR(MAX)  NULL,
        meta_json     NVARCHAR(MAX)  NULL,
        ip            NVARCHAR(45)   NULL,
        user_agent    NVARCHAR(500)  NULL,
        created_at    DATETIME2      NOT NULL DEFAULT GETUTCDATE(),

        CONSTRAINT FK_moderation_actions_post
            FOREIGN KEY (post_id)  REFERENCES Post(PostID)    ON DELETE CASCADE,
        CONSTRAINT FK_moderation_actions_admin
            FOREIGN KEY (admin_id) REFERENCES Account(AccountID)
    );

    CREATE INDEX IX_mod_actions_post_created
        ON moderation_actions (post_id, created_at DESC);

    CREATE INDEX IX_mod_actions_admin_created
        ON moderation_actions (admin_id, created_at DESC);
END;
GO

-- ─── 3. rate_limit_events (DB fallback – no Redis required) ──────────────────

IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'rate_limit_events')
BEGIN
    CREATE TABLE rate_limit_events (
        id           BIGINT        IDENTITY(1,1) PRIMARY KEY,
        user_id      INT           NULL,          -- NULL for anonymous
        ip           NVARCHAR(45)  NOT NULL,
        action_type  NVARCHAR(50)  NOT NULL,       -- CREATE_POST | CREATE_COMMENT
        created_at   DATETIME2     NOT NULL DEFAULT GETUTCDATE(),

        CONSTRAINT FK_rle_user
            FOREIGN KEY (user_id) REFERENCES Account(AccountID) ON DELETE SET NULL
    );

    CREATE INDEX IX_rle_ip_created
        ON rate_limit_events (ip, created_at DESC);

    CREATE INDEX IX_rle_user_created
        ON rate_limit_events (user_id, created_at DESC);

    CREATE INDEX IX_rle_type_created
        ON rate_limit_events (action_type, created_at DESC);
END;
GO
