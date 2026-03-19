-- SQL Script to create Appeals table
-- Execute this in SQL Server Management Studio

CREATE TABLE Appeals (
    appealID INT PRIMARY KEY IDENTITY(1,1),
    email NVARCHAR(100) NOT NULL,
    reason NVARCHAR(2000) NOT NULL,
    status NVARCHAR(20) NOT NULL DEFAULT 'Pending', -- Pending, Review, Resolved, Rejected
    adminNote NVARCHAR(1000),
    createdAt DATETIME NOT NULL DEFAULT GETDATE(),
    updatedAt DATETIME,
    ipAddress NVARCHAR(50),
    reviewedByAdminID INT,
    
    CONSTRAINT FK_Appeals_Admin FOREIGN KEY (reviewedByAdminID) 
        REFERENCES Accounts(accountID)
        ON DELETE SET NULL
);

-- Create indexes for better query performance
CREATE INDEX IDX_Appeals_Email ON Appeals(email);
CREATE INDEX IDX_Appeals_Status ON Appeals(status);
CREATE INDEX IDX_Appeals_CreatedAt ON Appeals(createdAt DESC);

-- Add this column to Accounts table if not exists to track ban status
-- ALTER TABLE Accounts ADD isBanned INT DEFAULT 0;

-- Seed test data (optional)
INSERT INTO Appeals (email, reason, status, createdAt) VALUES
('test@example.com', 'Tôi bị ban nhầm vì lỗi hệ thống', 'Pending', GETDATE()),
('user@example.com', 'Tôi không vi phạm quy định cộng đồng', 'Review', DATEADD(day, -1, GETDATE()));
