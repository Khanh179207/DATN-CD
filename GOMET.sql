CREATE DATABASE DATN_CD;
GO

USE DATN_CD;
GO

CREATE TABLE Account (
    AccountID INT IDENTITY(1,1) PRIMARY KEY,
    Username NVARCHAR(100) NOT NULL,
    Email NVARCHAR(255) NOT NULL,
    Password NVARCHAR(255) NOT NULL,
    Avatar NVARCHAR(255),
    Token NVARCHAR(255) NOT NULL,
    Point INT NOT NULL,
    isAdmin INT NOT NULL,
    isPremium INT NOT NULL,
    isActive INT NOT NULL,
    CreatedAt DATE NOT NULL,
    UpdatedAt DATE,
    DeletedAt DATE
);

GO

CREATE TABLE Category (
    CategoryID INT IDENTITY(1,1) PRIMARY KEY,
    CategoryName NVARCHAR(255) NOT NULL
);

GO

CREATE TABLE Event (
    EventID INT IDENTITY(1,1) PRIMARY KEY,
    EventName NVARCHAR(255) NOT NULL,
    Winner INT,
    StartAt DATE NOT NULL,
    EndAt DATE NOT NULL
);

GO

CREATE TABLE Post (
    PostID INT IDENTITY(1,1) PRIMARY KEY,
    AccountID INT NOT NULL,
    CategoryID INT NOT NULL,
    EventID INT,
    Title NVARCHAR(255) NOT NULL,
    Description NVARCHAR(MAX) NOT NULL,
    Ingredients NVARCHAR(MAX) NOT NULL,
    Media NVARCHAR(255),
    Level INT NOT NULL,
    CookingTime INT NOT NULL,
    Views INT NOT NULL,
    isActive INT NOT NULL,
    isApproved INT NOT NULL,
    CreatedAt DATE NOT NULL,

    CONSTRAINT FK_Post_Account FOREIGN KEY (AccountID) REFERENCES Account(AccountID),
    CONSTRAINT FK_Post_Category FOREIGN KEY (CategoryID) REFERENCES Category(CategoryID),
    CONSTRAINT FK_Post_Event FOREIGN KEY (EventID) REFERENCES Event(EventID)
);

GO

CREATE TABLE EventPosts (
    EventPostID INT IDENTITY(1,1) PRIMARY KEY,
    EventID INT NOT NULL,
    PostID INT NOT NULL,
    CreatedAt DATE NOT NULL,

    CONSTRAINT FK_EventPosts_Event FOREIGN KEY (EventID) REFERENCES Event(EventID),
    CONSTRAINT FK_EventPosts_Post FOREIGN KEY (PostID) REFERENCES Post(PostID)
);

GO

CREATE TABLE CookingSteps (
    StepID INT IDENTITY(1,1) PRIMARY KEY,
    PostID INT NOT NULL,
    StepNumber INT NOT NULL,
    Content NVARCHAR(MAX) NOT NULL,
    Image NVARCHAR(255),
    Video NVARCHAR(255),

    CONSTRAINT FK_CookingSteps_Post FOREIGN KEY (PostID) REFERENCES Post(PostID)
);

GO

CREATE TABLE Comment (
    CommentID INT IDENTITY(1,1) PRIMARY KEY,
    AccountID INT NOT NULL,
    PostID INT NOT NULL,
    Content NVARCHAR(MAX) NOT NULL,

    CONSTRAINT FK_Comment_Account FOREIGN KEY (AccountID) REFERENCES Account(AccountID),
    CONSTRAINT FK_Comment_Post FOREIGN KEY (PostID) REFERENCES Post(PostID)
);

GO

CREATE TABLE Rating (
    RatingID INT IDENTITY(1,1) PRIMARY KEY,
    AccountID INT NOT NULL,
    PostID INT NOT NULL,
    Rate INT NOT NULL,

    CONSTRAINT FK_Rating_Account FOREIGN KEY (AccountID) REFERENCES Account(AccountID),
    CONSTRAINT FK_Rating_Post FOREIGN KEY (PostID) REFERENCES Post(PostID)
);

GO

CREATE TABLE Favorite (
    FavoriteID INT IDENTITY(1,1) PRIMARY KEY,
    AccountID INT NOT NULL,
    PostID INT NOT NULL,

    CONSTRAINT FK_Favorite_Account FOREIGN KEY (AccountID) REFERENCES Account(AccountID),
    CONSTRAINT FK_Favorite_Post FOREIGN KEY (PostID) REFERENCES Post(PostID)
);

GO

CREATE TABLE Follow (
    FollowID INT IDENTITY(1,1) PRIMARY KEY,
    FollowerID INT NOT NULL,
    FolloweeID INT NOT NULL,
    Status INT NOT NULL,
    FollowedAt DATE NOT NULL,
    UnFollowedAt DATE,

    CONSTRAINT FK_Follow_Follower FOREIGN KEY (FollowerID) REFERENCES Account(AccountID),
    CONSTRAINT FK_Follow_Followee FOREIGN KEY (FolloweeID) REFERENCES Account(AccountID)
);

GO

CREATE TABLE History (
    HistoryID INT IDENTITY(1,1) PRIMARY KEY,
    AccountID INT NOT NULL,
    PostID INT NOT NULL,
    LastViewedAt DATETIME NOT NULL,

    CONSTRAINT FK_History_Account FOREIGN KEY (AccountID) REFERENCES Account(AccountID),
    CONSTRAINT FK_History_Post FOREIGN KEY (PostID) REFERENCES Post(PostID)
);

GO

CREATE TABLE Note (
    NoteID INT IDENTITY(1,1) PRIMARY KEY,
    AccountID INT NOT NULL,
    PostID INT NOT NULL,
    Content NVARCHAR(MAX) NOT NULL,
    CreatedAt DATE NOT NULL,
    DeletedAt DATE,
    isActive INT NOT NULL,

    CONSTRAINT FK_Note_Account FOREIGN KEY (AccountID) REFERENCES Account(AccountID),
    CONSTRAINT FK_Note_Post FOREIGN KEY (PostID) REFERENCES Post(PostID)
);

GO

CREATE TABLE Notification (
    NotificationID INT IDENTITY(1,1) PRIMARY KEY,
    Title NVARCHAR(255) NOT NULL,
    Content NVARCHAR(MAX) NOT NULL,
    Type NVARCHAR(100) NOT NULL,
    AccountID INT NOT NULL,
    PostID INT,
    isRead INT NOT NULL,
    ReadAt DATE,
    CreatedAt DATE NOT NULL,

    CONSTRAINT FK_Notification_Account FOREIGN KEY (AccountID) REFERENCES Account(AccountID),
    CONSTRAINT FK_Notification_Post FOREIGN KEY (PostID) REFERENCES Post(PostID)
);

GO

CREATE TABLE Achievement (
    AchievementID INT IDENTITY(1,1) PRIMARY KEY,
    AchievementName NVARCHAR(255) NOT NULL,
    Description NVARCHAR(MAX) NOT NULL,
    Icon NVARCHAR(255)
);

GO

CREATE TABLE UserAchievement (
    UAID INT IDENTITY(1,1) PRIMARY KEY,
    AccountID INT NOT NULL,
    AchievementID INT NOT NULL,
    ReceivedAt DATE NOT NULL,

    CONSTRAINT FK_UserAchievement_Account FOREIGN KEY (AccountID) REFERENCES Account(AccountID),
    CONSTRAINT FK_UserAchievement_Achievement FOREIGN KEY (AchievementID) REFERENCES Achievement(AchievementID)
);

GO

CREATE TABLE Subscription (
    SubID INT IDENTITY(1,1) PRIMARY KEY,
    AccountID INT NOT NULL,
    PlanType INT NOT NULL,
    StartAt DATE NOT NULL,
    EndAt DATE NOT NULL,
    isActive INT NOT NULL,

    CONSTRAINT FK_Subscription_Account FOREIGN KEY (AccountID) REFERENCES Account(AccountID)
);

GO

CREATE TABLE Error (
    ErrorID INT IDENTITY(1,1) PRIMARY KEY,
    AccountID INT,
    ErrorName NVARCHAR(255) NOT NULL,
    Description NVARCHAR(MAX) NOT NULL,
    CreatedAt DATE NOT NULL,

    CONSTRAINT FK_Error_Account FOREIGN KEY (AccountID) REFERENCES Account(AccountID)
);

GO

CREATE TABLE Report (
    ReportID INT IDENTITY(1,1) PRIMARY KEY,
    AccountID INT NOT NULL,
    PostID INT NOT NULL,
    Reason NVARCHAR(MAX) NOT NULL,
    CreatedAt DATE NOT NULL,

    CONSTRAINT FK_Report_Account FOREIGN KEY (AccountID) REFERENCES Account(AccountID),
    CONSTRAINT FK_Report_Post FOREIGN KEY (PostID) REFERENCES Post(PostID)
);

GO

-- ==========================================
-- 1. Tạo bảng SearchHistory
-- ==========================================
CREATE TABLE SearchHistory (
    SearchID INT IDENTITY(1,1) PRIMARY KEY,
    AccountID INT NOT NULL,
    Keyword NVARCHAR(255) NOT NULL,
    SearchedAt DATETIME DEFAULT GETDATE(),

    -- Nối khóa ngoại tới bảng Account
    CONSTRAINT FK_SearchHistory_Account FOREIGN KEY (AccountID) REFERENCES Account(AccountID)
);
GO

-- ==========================================
-- 2. Tạo bảng ShoppingList
-- ==========================================
CREATE TABLE ShoppingList (
    ShoppingID INT IDENTITY(1,1) PRIMARY KEY,
    AccountID INT,
    PostID INT,
    IngredientName NVARCHAR(255),
    IsBought INT DEFAULT 0, -- 0: Chưa mua, 1: Đã mua
    CreatedAt DATE DEFAULT GETDATE(),

    -- Nối khóa ngoại tới bảng Account và Post
    CONSTRAINT FK_ShoppingList_Account FOREIGN KEY (AccountID) REFERENCES Account(AccountID),
    CONSTRAINT FK_ShoppingList_Post FOREIGN KEY (PostID) REFERENCES Post(PostID)
);
GO

INSERT INTO Account (Username, Email, Password, Avatar, Token, Point, isAdmin, isPremium, isActive, CreatedAt)
VALUES
('user1', 'user1@gmail.com', '123456', NULL, 'token1', 10, 0, 0, 1, GETDATE()),
('user2', 'user2@gmail.com', '123456', NULL, 'token2', 20, 0, 1, 1, GETDATE()),
('admin', 'admin@gmail.com', 'admin123', NULL, 'token3', 100, 1, 1, 1, GETDATE());	
GO

INSERT INTO Category (CategoryName)
VALUES
(N'Món Việt'),
(N'Món Âu'),
(N'Món Chay');

GO

INSERT INTO Event (EventName, StartAt, EndAt)
VALUES
(N'Tết 2026', '2026-01-01', '2026-01-31'),
(N'Mùa Hè', '2026-06-01', '2026-06-30'),
(N'Noel', '2026-12-01', '2026-12-25');

GO

INSERT INTO Post (
    AccountID, CategoryID, EventID, Title, Description, Ingredients,
    Media, Level, CookingTime, Views, isActive, isApproved, CreatedAt
)
VALUES
(1, 1, 1, N'Phở Bò', N'Hướng dẫn nấu phở bò', N'Bánh phở, thịt bò', NULL, 2, 3, 100, 1, 1, GETDATE()),
(2, 2, 2, N'Steak', N'Cách làm steak', N'Thịt bò, bơ', NULL, 3, 1, 50, 1, 1, GETDATE()),
(1, 3, NULL, N'Đậu hũ chiên', N'Món chay đơn giản', N'Đậu hũ', NULL, 1, 1, 30, 1, 1, GETDATE());

GO

INSERT INTO EventPosts (EventID, PostID, CreatedAt)
VALUES
(1, 1, GETDATE()),
(2, 2, GETDATE()),
(3, 1, GETDATE());

GO

INSERT INTO CookingSteps (PostID, StepNumber, Content)
VALUES
(1, 1, N'Sơ chế nguyên liệu'),
(1, 2, N'Nấu nước dùng'),
(2, 1, N'Áp chảo thịt');

GO

INSERT INTO Comment (AccountID, PostID, Content)
VALUES
(2, 1, N'Bài viết rất hay'),
(1, 2, N'Làm thử thấy ngon'),
(3, 1, N'Admin xác nhận chuẩn');

GO

INSERT INTO Rating (AccountID, PostID, Rate)
VALUES
(2, 1, 9),
(1, 2, 8),
(3, 1, 10);

GO

INSERT INTO Favorite (AccountID, PostID)
VALUES
(1, 1),
(1, 2),
(2, 1);

GO

INSERT INTO Follow (FollowerID, FolloweeID, Status, FollowedAt)
VALUES
(1, 2, 0, GETDATE()),
(2, 1, 0, GETDATE()),
(1, 3, 0, GETDATE());

GO

INSERT INTO History (AccountID, PostID, LastViewedAt)
VALUES
(1, 1, GETDATE()),
(2, 2, GETDATE()),
(3, 1, GETDATE());

GO

INSERT INTO Note (AccountID, PostID, Content, CreatedAt, isActive)
VALUES
(1, 1, N'Ghi chú 1', GETDATE(), 1),
(2, 2, N'Ghi chú 2', GETDATE(), 1),
(1, 3, N'Ghi chú 3', GETDATE(), 1);

GO

INSERT INTO Notification (Title, Content, Type, AccountID, PostID, isRead, CreatedAt)
VALUES
(N'Duyệt bài', N'Bài viết đã được duyệt', N'SYSTEM', 1, 1, 0, GETDATE()),
(N'Thông báo', N'Có người theo dõi bạn', N'FOLLOW', 2, NULL, 0, GETDATE()),
(N'Sự kiện', N'Bài viết tham gia sự kiện', N'EVENT', 1, 1, 1, GETDATE());

GO

INSERT INTO Achievement (AchievementName, Description, Icon)
VALUES
(N'Người mới', N'Tạo bài viết đầu tiên', NULL),
(N'Đầu bếp', N'10 bài viết', NULL),
(N'Ngôi sao', N'1000 lượt xem', NULL);

GO

INSERT INTO UserAchievement (AccountID, AchievementID, ReceivedAt)
VALUES
(1, 1, GETDATE()),
(1, 2, GETDATE()),
(2, 1, GETDATE());

GO

INSERT INTO Subscription (AccountID, PlanType, StartAt, EndAt, isActive)
VALUES
(1, 1, GETDATE(), DATEADD(MONTH, 1, GETDATE()), 1),
(2, 3, GETDATE(), DATEADD(MONTH, 3, GETDATE()), 1),
(3, 12, GETDATE(), DATEADD(MONTH, 12, GETDATE()), 1);

GO

INSERT INTO Error (AccountID, ErrorName, Description, CreatedAt)
VALUES
(1, N'Lỗi đăng nhập', N'Sai mật khẩu', GETDATE()),
(2, N'Lỗi upload', N'File quá lớn', GETDATE()),
(NULL, N'Lỗi hệ thống', N'Unknown error', GETDATE());

GO

INSERT INTO Report (AccountID, PostID, Reason, CreatedAt)
VALUES
(2, 1, N'Nội dung không phù hợp', GETDATE()),
(1, 2, N'Spam', GETDATE()),
(3, 1, N'Sai công thức', GETDATE());

Select * from Account;
INSERT INTO account (Username, Email, Password, Avatar, Token, Point, isAdmin, isPremium, isActive, CreatedAt)
VALUES 
('Khanh', 'Khanh123@gmail.com', '321123', NULL, 'token3', 100, 1, 1, 1, GETDATE());

DELETE FROM account 
WHERE accountID = 5;

INSERT INTO Post (
    AccountID, CategoryID, EventID, Title, Description, Ingredients,
    Media, Level, CookingTime, Views, isActive, isApproved, CreatedAt
)
VALUES
(1, 1, 1, N'Phở Bò', N'Hướng dẫn nấu phở bò', N'Bánh phở, thịt bò', NULL, 2, 3, 100, 1, 1, GETDATE()),
(2, 2, 2, N'Steak', N'Cách làm steak', N'Thịt bò, bơ', NULL, 3, 1, 50, 1, 1, GETDATE()),
(1, 3, NULL, N'Đậu hũ chiên', N'Món chay đơn giản', N'Đậu hũ', NULL, 1, 1, 30, 1, 1, GETDATE());

GO
Select *from SearchHistory;