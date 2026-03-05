IF OBJECT_ID('SystemSetting', 'U') IS NULL
BEGIN
    CREATE TABLE SystemSetting (
        [key] NVARCHAR(50) NOT NULL PRIMARY KEY,
        [value] NVARCHAR(255) NOT NULL,
        updatedAt DATETIME NOT NULL CONSTRAINT DF_SystemSetting_UpdatedAt DEFAULT GETDATE()
    );
END;

IF NOT EXISTS (SELECT 1 FROM SystemSetting WHERE [key] = 'MAINTENANCE_MODE')
BEGIN
    INSERT INTO SystemSetting([key], [value], updatedAt)
    VALUES ('MAINTENANCE_MODE', '0', GETDATE());
END;

IF NOT EXISTS (SELECT 1 FROM SystemSetting WHERE [key] = 'MAINTENANCE_MESSAGE')
BEGIN
    INSERT INTO SystemSetting([key], [value], updatedAt)
    VALUES ('MAINTENANCE_MESSAGE', N'Hệ thống đang bảo trì. Vui lòng quay lại sau.', GETDATE());
END;

IF OBJECT_ID('WeeklyCertificate', 'U') IS NULL
BEGIN
    CREATE TABLE WeeklyCertificate (
        id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        weekStart DATE NOT NULL,
        weekEnd DATE NOT NULL,
        userId INT NOT NULL,
        [rank] INT NOT NULL,
        score INT NOT NULL,
        title NVARCHAR(200) NOT NULL,
        issuedAt DATETIME NOT NULL CONSTRAINT DF_WeeklyCertificate_IssuedAt DEFAULT GETDATE(),
        certificateCode NVARCHAR(50) NOT NULL,
        CONSTRAINT UQ_WeeklyCertificate_Code UNIQUE (certificateCode),
        CONSTRAINT UQ_WeeklyCertificate_Week_User UNIQUE (weekStart, userId),
        CONSTRAINT FK_WeeklyCertificate_Account FOREIGN KEY (userId) REFERENCES Account(accountID)
    );
END;

IF NOT EXISTS (
    SELECT 1
    FROM sys.check_constraints
    WHERE [name] = 'CK_WeeklyCertificate_Rank_Positive'
)
BEGIN
    ALTER TABLE WeeklyCertificate
    ADD CONSTRAINT CK_WeeklyCertificate_Rank_Positive CHECK ([rank] >= 1);
END;