IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'admin_email_rate_events')
BEGIN
    CREATE TABLE admin_email_rate_events (
        id         BIGINT IDENTITY(1,1) PRIMARY KEY,
        admin_id   INT          NOT NULL,
        created_at DATETIME2    NOT NULL DEFAULT SYSUTCDATETIME(),
        CONSTRAINT FK_aere_admin FOREIGN KEY (admin_id) REFERENCES Account(accountID) ON DELETE CASCADE
    );

    CREATE INDEX IX_aere_admin_created ON admin_email_rate_events (admin_id, created_at DESC);
END;
