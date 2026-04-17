USE master;
	GO


-- Bước 2: Đá đít toàn bộ các kết nối đang cắm vào Database này
IF EXISTS(select * from sys.databases where name='DATN_CD')
BEGIN
    ALTER DATABASE DATN_CD SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE DATN_CD;
END
GO


	-- Bước 3: Khởi tạo lại Database
	CREATE DATABASE DATN_CD;
	GO
	USE DATN_CD;
	GO

	-- ==========================================
	-- 1. NHÓM BẢNG CHA (MASTER TABLES)
	-- ==========================================

CREATE TABLE Account (
    AccountID INT IDENTITY(1,1) PRIMARY KEY,
    Username NVARCHAR(100) NOT NULL,
    Email NVARCHAR(255) NOT NULL,
    Password NVARCHAR(255) NOT NULL,
    Avatar NVARCHAR(255),
    Bio NVARCHAR(MAX) NULL,
    Point INT DEFAULT 0,
    isAdmin INT DEFAULT 0,
    isPremium INT DEFAULT 0,
    isActive INT DEFAULT 1,
    CreatedAt DATETIME DEFAULT GETDATE(),
    UpdatedAt DATETIME,
    DeletedAt DATETIME,   
	
 -- MỚI THÊM: Lưu thẳng Email Admin (hứng từ Frontend)
    BanReason NVARCHAR(MAX) NULL,      -- Lưu lý do khóa (Admin tự gõ vào)
    BannedAt DATETIME NULL             -- Đổi dấu chấm phẩy (;) thành bình thường
);                                     -- Bổ sung dấu đóng ngoặc tròn và chấm phẩy ở đây
GO

-- Sửa lại bảng Category cho khớp với các bảng khác của sếp
CREATE TABLE Category (
    CategoryID INT IDENTITY(1,1) PRIMARY KEY,
    CategoryName NVARCHAR(255) NOT NULL,
    CategoryImage NVARCHAR(MAX),
    IsActive INT DEFAULT 1 -- Thêm cái này để Admin có chỗ bấm Ẩn/Hiện như anh em mình bàn
);
GO

	CREATE TABLE Event (
		EventID INT IDENTITY(1,1) PRIMARY KEY,
		EventName NVARCHAR(255) NOT NULL,
		BannerImage NVARCHAR(MAX),
    
		Description NVARCHAR(MAX),
		Reward NVARCHAR(255),
    
		StartAt DATETIME NOT NULL,
		EndAt DATETIME NOT NULL,
		VoteStartAt DATETIME NOT NULL,
		VoteEndAt DATETIME NOT NULL,

		MaxVotes INT DEFAULT 3,
		Winner INT NULL, 

		IsActive INT DEFAULT 1, -- 1: Tồn tại, 0: Đã xóa (Xóa mềm)
        IsForceEnded INT DEFAULT 0, -- 1: Bị ép kết thúc sớm, 0: Bình thường

		CONSTRAINT FK_Event_Account FOREIGN KEY (Winner) REFERENCES Account(AccountID)
	);
	GO


	-- ==========================================
	-- 2. NHÓM BẢNG CHÍNH (POSTS & SỰ KIỆN)
	-- ==========================================

CREATE TABLE Post (
    PostID INT IDENTITY(1,1) PRIMARY KEY,
    AccountID INT NOT NULL,
    CategoryID INT NOT NULL,
    EventID INT NULL, 
    Title NVARCHAR(255) NOT NULL,
    Description NVARCHAR(MAX) NOT NULL,
    Ingredients NVARCHAR(MAX) NOT NULL,
    Media NVARCHAR(255),
    Video NVARCHAR(255),
    Level INT DEFAULT 1,
    CookingTime INT DEFAULT 30,
    Views INT DEFAULT 0,
    LikeCount INT DEFAULT 0,
	TotalPts INT DEFAULT 0,
    isActive INT DEFAULT 1,
    isApproved INT DEFAULT 0,
    CreatedAt DATETIME DEFAULT GETDATE(),

    -- 🔥 MỚI THÊM: Phục vụ kiểm duyệt bài viết
    RejectReason NVARCHAR(MAX) NULL,   -- Lưu lý do từ chối/ẩn bài (để hiện cho User xem)
    RejectedAt DATETIME NULL,          -- Lưu thời gian từ chối

    CONSTRAINT FK_Post_Account FOREIGN KEY (AccountID) REFERENCES Account(AccountID),
    CONSTRAINT FK_Post_Category FOREIGN KEY (CategoryID) REFERENCES Category(CategoryID),
    CONSTRAINT FK_Post_Event FOREIGN KEY (EventID) REFERENCES Event(EventID)
);
GO

	CREATE TABLE EventPosts (
		EventPostID INT IDENTITY(1,1) PRIMARY KEY,
		EventID INT NOT NULL,
		PostID INT NOT NULL,
		VoteCount INT DEFAULT 0,
		CreatedAt DATETIME DEFAULT GETDATE(),

		CONSTRAINT FK_EventPosts_Event FOREIGN KEY (EventID) REFERENCES Event(EventID),
		CONSTRAINT FK_EventPosts_Post FOREIGN KEY (PostID) REFERENCES Post(PostID),
		CONSTRAINT UQ_Event_Post UNIQUE (EventID, PostID)
	);
	GO

	-- ==========================================
	-- 3. NHÓM BẢNG TƯƠNG TÁC (INTERACTIONS)
	-- ==========================================

	CREATE TABLE Votes (
		VoteID INT IDENTITY(1,1) PRIMARY KEY,
		AccountID INT NOT NULL,
		EventPostID INT NOT NULL, 
		CreatedAt DATETIME DEFAULT GETDATE(),

		CONSTRAINT FK_Votes_Account FOREIGN KEY (AccountID) REFERENCES Account(AccountID),
		CONSTRAINT FK_Votes_EventPost FOREIGN KEY (EventPostID) REFERENCES EventPosts(EventPostID),
		CONSTRAINT UQ_Account_Vote_EventPost UNIQUE (AccountID, EventPostID)
	);
	GO

	CREATE TABLE Likes (
		LikeID INT IDENTITY(1,1) PRIMARY KEY,
		AccountID INT NOT NULL,
		PostID INT NOT NULL,
		CreatedAt DATETIME DEFAULT GETDATE(),

		CONSTRAINT FK_Likes_Account FOREIGN KEY (AccountID) REFERENCES Account(AccountID),
		CONSTRAINT FK_Likes_Post FOREIGN KEY (PostID) REFERENCES Post(PostID),
		CONSTRAINT UQ_Account_Post UNIQUE (AccountID, PostID)
	);
	GO


CREATE TABLE Comment (
    CommentID INT IDENTITY(1,1) PRIMARY KEY,
    AccountID INT NOT NULL,
    PostID INT NOT NULL,
    
    Content NVARCHAR(MAX) NULL, 
    Rating INT DEFAULT 0, 
    Attachments NVARCHAR(MAX) NULL, 
    Likes INT DEFAULT 0,
    cmtid INT NULL,
    CreatedAt DATETIME DEFAULT GETDATE(),
	 IsActive INT,


    CONSTRAINT FK_Comment_Account FOREIGN KEY (AccountID) REFERENCES Account(AccountID),
    CONSTRAINT FK_Comment_Post FOREIGN KEY (PostID) REFERENCES Post(PostID),
    CONSTRAINT FK_Comment_Parent FOREIGN KEY (cmtid) REFERENCES Comment(CommentID)
);
GO
    CREATE TABLE CommentLike (
        LikeID INT IDENTITY(1,1) PRIMARY KEY,
        AccountID INT NOT NULL,
        CommentID INT NOT NULL,
        CreatedAt DATETIME DEFAULT GETDATE(),


        CONSTRAINT FK_CommentLike_Account FOREIGN KEY (AccountID) REFERENCES Account(AccountID),
        CONSTRAINT FK_CommentLike_Comment FOREIGN KEY (CommentID) REFERENCES Comment(CommentID),
        CONSTRAINT UQ_CommentLike UNIQUE (AccountID, CommentID)
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
    Status INT DEFAULT 0,
    FollowedAt DATETIME DEFAULT GETDATE(),

    CONSTRAINT FK_Follow_Follower FOREIGN KEY (FollowerID) REFERENCES Account(AccountID),
    CONSTRAINT FK_Follow_Followee FOREIGN KEY (FolloweeID) REFERENCES Account(AccountID),
    -- RÀNG BUỘC: Đảm bảo một cặp (Follower, Followee) chỉ xuất hiện 1 lần duy nhất
    CONSTRAINT UQ_Follower_Followee UNIQUE (FollowerID, FolloweeID)
);
GO

	-- ==========================================
	-- 4. NHÓM CHỨC NĂNG NGƯỜI DÙNG & TIỆN ÍCH
	-- ==========================================

	CREATE TABLE Note (
		NoteID INT IDENTITY(1,1) PRIMARY KEY,
		AccountID INT NOT NULL,
		PostID INT NOT NULL,
		Content NVARCHAR(MAX) NOT NULL,
		CreatedAt DATETIME DEFAULT GETDATE(),
		DeletedAt DATETIME,
		isActive INT DEFAULT 1,

		CONSTRAINT FK_Note_Account FOREIGN KEY (AccountID) REFERENCES Account(AccountID),
		CONSTRAINT FK_Note_Post FOREIGN KEY (PostID) REFERENCES Post(PostID)
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

	-- 🔥 ĐÃ THÊM BẢNG SHOPPING LIST
	CREATE TABLE ShoppingList (
		ShoppingID INT IDENTITY(1,1) PRIMARY KEY,
		AccountID INT NOT NULL,
		PostID INT NULL,
		IngredientName NVARCHAR(255) NOT NULL,
		IsBought INT DEFAULT 0,
		CreatedAt DATETIME DEFAULT GETDATE(),

		CONSTRAINT FK_ShoppingList_Account FOREIGN KEY (AccountID) REFERENCES Account(AccountID),
		CONSTRAINT FK_ShoppingList_Post FOREIGN KEY (PostID) REFERENCES Post(PostID)
	);
	GO

	CREATE TABLE MealPlan (
		PlanID INT IDENTITY(1,1) PRIMARY KEY,
		AccountID INT NOT NULL,
		PostID INT NULL,
		CustomMealName NVARCHAR(255),
		PlanDate DATE NOT NULL,
		MealType NVARCHAR(50), 
		Notes NVARCHAR(MAX),
		IsCompleted INT DEFAULT 0,
		CreatedAt DATETIME DEFAULT GETDATE(),

		CONSTRAINT FK_MealPlan_Account FOREIGN KEY (AccountID) REFERENCES Account(AccountID),
		CONSTRAINT FK_MealPlan_Post FOREIGN KEY (PostID) REFERENCES Post(PostID)
	);
	GO

	-- 🔥 ĐÃ THÊM BẢNG SEARCH HISTORY
	CREATE TABLE SearchHistory (
		SearchID INT IDENTITY(1,1) PRIMARY KEY,
		AccountID INT NOT NULL,
		Keyword NVARCHAR(255) NOT NULL,
		SearchedAt DATETIME DEFAULT GETDATE(),

		CONSTRAINT FK_SearchHistory_Account FOREIGN KEY (AccountID) REFERENCES Account(AccountID)
	);
	GO

	-- ==========================================
	-- 5. NHÓM HỆ THỐNG, LOGS, MESSAGES
	-- ==========================================

	-- 🔥 ĐÃ THÊM CỘT POST_ID VÀO BẢNG NOTIFICATION
CREATE TABLE Notification (
	NotificationID INT IDENTITY(1,1) PRIMARY KEY,
	Title NVARCHAR(255) NOT NULL,
	Content NVARCHAR(MAX) NOT NULL,
	Type NVARCHAR(100) NOT NULL,

	IsGlobal BIT DEFAULT 0,
	AccountID INT,
	ActorID INT NULL, 
	Link NVARCHAR(500) NULL,
	PostID INT NULL, 
	isRead INT DEFAULT 0,
	ReadAt DATETIME,
	ParentNotificationID INT NULL,
	CreatedAt DATETIME DEFAULT GETDATE(),

	CONSTRAINT FK_Notification_Account FOREIGN KEY (AccountID) REFERENCES Account(AccountID),
	CONSTRAINT FK_Notification_Actor FOREIGN KEY (ActorID) REFERENCES Account(AccountID),
	CONSTRAINT FK_Notification_Post FOREIGN KEY (PostID) REFERENCES Post(PostID)
);
GO


	CREATE TABLE History (
		HistoryID INT IDENTITY(1,1) PRIMARY KEY,
		AccountID INT NOT NULL,
		PostID INT NOT NULL,
		LastViewedAt DATETIME DEFAULT GETDATE(),

		CONSTRAINT FK_History_Account FOREIGN KEY (AccountID) REFERENCES Account(AccountID),
		CONSTRAINT FK_History_Post FOREIGN KEY (PostID) REFERENCES Post(PostID)
	);
	GO


	-- Bảng lưu lịch sử giao dịch thanh toán
	CREATE TABLE PaymentTransaction (
		TransactionID INT IDENTITY(1,1) PRIMARY KEY,
		AccountID INT NOT NULL,
		OrderCode NVARCHAR(50) NOT NULL UNIQUE, -- Mã giao dịch (VD: GOMET9999)
		Amount INT NOT NULL,                    -- Số tiền phải trả
		PlanType INT NOT NULL,                  -- 1: Tháng, 2: Năm, 3: Trọn đời (Khớp với bảng Subscription)
		Status NVARCHAR(20) DEFAULT 'PENDING',  -- Trạng thái: PENDING, PAID, CANCELLED
		CreatedAt DATETIME DEFAULT GETDATE(),
		PaidAt DATETIME NULL,                   -- Thời gian nhận được tiền

		CONSTRAINT FK_Transaction_Account FOREIGN KEY (AccountID) REFERENCES Account(AccountID)
	);
	GO

	CREATE TABLE Subscription (
		SubID INT IDENTITY(1,1) PRIMARY KEY,
		AccountID INT NOT NULL,
		PlanType INT NOT NULL,
		StartAt DATETIME2 NOT NULL,
		EndAt DATETIME2 NOT NULL,
		isActive INT DEFAULT 1,
		TransactionID INT NULL, -- Sửa dấu ; thành dấu ,

		CONSTRAINT FK_Sub_Account FOREIGN KEY (AccountID) REFERENCES Account(AccountID), -- Thêm dấu , ở cuối
		CONSTRAINT FK_Sub_Transaction FOREIGN KEY (TransactionID) REFERENCES PaymentTransaction(TransactionID)
	);
	GO

	-- TIN NHẮN
	CREATE TABLE Conversation (
		ConversationID INT IDENTITY(1,1) PRIMARY KEY,
		UserOneID INT NOT NULL,
		UserTwoID INT NOT NULL,
		CreatedAt DATETIME DEFAULT GETDATE(),

		CONSTRAINT FK_Conv_UserOne FOREIGN KEY (UserOneID) REFERENCES Account(AccountID),
		CONSTRAINT FK_Conv_UserTwo FOREIGN KEY (UserTwoID) REFERENCES Account(AccountID)
	);
	GO

CREATE TABLE Message (
    MessageID INT IDENTITY(1,1) PRIMARY KEY,
    ConversationID INT NOT NULL,
    SenderID INT NOT NULL,
    Content NVARCHAR(MAX) NOT NULL,
    IsRead BIT DEFAULT 0, -- Đã đổi thành BIT
    CreatedAt DATETIME DEFAULT GETDATE(),
	ParentID INT NULL,


	CONSTRAINT FK_Msg_Parent FOREIGN KEY (ParentID) REFERENCES Message(MessageID),
    CONSTRAINT FK_Msg_Conv FOREIGN KEY (ConversationID) REFERENCES Conversation(ConversationID),
    CONSTRAINT FK_Msg_Sender FOREIGN KEY (SenderID) REFERENCES Account(AccountID)
);
GO
CREATE TABLE MessageReaction (
    ReactionID INT IDENTITY(1,1) PRIMARY KEY,
    MessageID INT NOT NULL,
    AccountID INT NOT NULL,
    Emoji NVARCHAR(10) NOT NULL, 
    CreatedAt DATETIME DEFAULT GETDATE(),

    CONSTRAINT FK_Reaction_Msg FOREIGN KEY (MessageID) REFERENCES Message(MessageID),
    CONSTRAINT FK_Reaction_User FOREIGN KEY (AccountID) REFERENCES Account(AccountID),
    CONSTRAINT UQ_Reaction_Msg_User UNIQUE (MessageID, AccountID)
);
GO


	CREATE TABLE Ticket (
		TicketID INT IDENTITY(1,1) PRIMARY KEY,
		AccountID INT NOT NULL,          
		TicketType NVARCHAR(50) NOT NULL,  
		TargetPostID INT NULL,             
		Title NVARCHAR(255) NOT NULL,
		Description NVARCHAR(MAX) NOT NULL,
		Attachment NVARCHAR(500) NULL, 

		Status INT DEFAULT 0,              
		CreatedAt DATETIME DEFAULT GETDATE(),
		ResolvedAt DATETIME NULL,
		ProcessedAt DATETIME NULL,  
		-- Thêm cột lưu thông tin Admin xử lý vào bảng Ticket
		AdminID INT NULL,
		AdminName NVARCHAR(255) NULL,
		AdminNote NVARCHAR(MAX) NULL, -- Để Admin phản hồi Bug/Góp ý
    
		CONSTRAINT FK_Ticket_Account FOREIGN KEY (AccountID) REFERENCES Account(AccountID),
		CONSTRAINT FK_Ticket_Post FOREIGN KEY (TargetPostID) REFERENCES Post(PostID)
	);
	GO



CREATE TABLE dbo.Appeals (
    AppealID        INT IDENTITY(1,1) PRIMARY KEY,
    Email           NVARCHAR(254) NOT NULL,    -- Email User nhập vào form
    AccountID       INT NULL,                  -- Backend tự tìm qua Email và nhét ID vào đây
    
    Reason          NVARCHAR(MAX) NOT NULL,    -- Lý do User xin gỡ Ban
    Status          NVARCHAR(50) NOT NULL DEFAULT 'Pending', -- 3 trạng thái: Pending, Resolved, Rejected
    Note            NVARCHAR(MAX) NULL,        -- Lời nhắn của Admin từ chối/duyệt (Sẽ gửi thông báo cho user)
    
    CreatedAt       DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    ResolvedAt      DATETIME2 NULL,            -- Thời gian Admin đóng đơn (Đồng bộ với Ticket)
	AdminID INT NULL,
	AdminName NVARCHAR(255) NULL,
    CONSTRAINT FK_Appeals_Account FOREIGN KEY (AccountID) REFERENCES Account(AccountID)
);
GO


CREATE TABLE BlacklistWord (
    WordID INT IDENTITY(1,1) PRIMARY KEY,
    Word NVARCHAR(100) NOT NULL UNIQUE,
    CreatedAt DATETIME DEFAULT GETDATE()
);
GO	

CREATE TABLE SystemConfig (
    ConfigKey VARCHAR(50) PRIMARY KEY,  -- Mã cấu hình (Ví dụ: HERO_BANNER, PREMIUM_PRICE_1)
    ConfigValue NVARCHAR(MAX) NOT NULL, -- Giá trị (Link ảnh, Số tiền, Đoạn text...)
    ConfigGroup NVARCHAR(50),           -- Nhóm để dễ phân loại (GIAO_DIEN, GOI_CUOC, QUANG_CAO)
    Description NVARCHAR(255),          -- Chú thích cho Admin dễ hiểu
    UpdatedAt DATETIME DEFAULT GETDATE()
);
GO

CREATE TABLE ModerationLog (
    LogID INT IDENTITY(1,1) PRIMARY KEY,
    TargetID INT NOT NULL,
    TargetType NVARCHAR(50) NOT NULL,
    Action NVARCHAR(50) NOT NULL,
    AdminID INT NOT NULL,
    AdminName NVARCHAR(255),
    Reason NVARCHAR(500),
    CreatedAt DATETIME DEFAULT GETDATE()
);
GO

CREATE TABLE InteractionLog (
    LogID INT PRIMARY KEY IDENTITY(1,1),
    PostID INT NOT NULL,
	ReferenceID INT,
    Type NVARCHAR(10), -- 'VIEW', 'LIKE', 'RATE'
    Value INT,         -- View/Like: 1, Rate: số sao (1-5)
    CreatedAt DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (PostID) REFERENCES Post(PostID)
);
GO

CREATE TABLE PasswordResetToken (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    accountId INT NOT NULL,
    tokenHash VARCHAR(64) NOT NULL,
    expiresAt DATETIMEOFFSET NOT NULL,
    usedAt DATETIMEOFFSET NULL,
    createdAt DATETIMEOFFSET NOT NULL,
    requestIp VARCHAR(45) NULL,
    
    -- Ràng buộc khóa ngoại để đảm bảo dữ liệu chuẩn
    CONSTRAINT FK_PasswordReset_Account FOREIGN KEY (accountId) 
    REFERENCES Account(AccountID) ON DELETE CASCADE
);
GO

-- Tạo Index để tìm kiếm Token nhanh hơn
CREATE INDEX IX_PasswordResetToken_Hash ON PasswordResetToken(tokenHash);
CREATE INDEX IX_PasswordResetToken_Ip ON PasswordResetToken(requestIp, createdAt);
CREATE INDEX IX_InteractionLog_Post_Time ON InteractionLog (PostID, CreatedAt) INCLUDE (Type, Value); 
GO

	-- ==========================================
	-- 6. TRIGGERS TỰ ĐỘNG CẬP NHẬT
	-- ==========================================

	-- Trigger khi có người Vote
	CREATE TRIGGER TRG_UpdateVoteCount_Insert ON Votes AFTER INSERT AS
	BEGIN
		UPDATE EventPosts 
		SET VoteCount = VoteCount + 1 
		FROM EventPosts ep JOIN inserted i ON ep.EventPostID = i.EventPostID;
	END;
	GO

	-- Trigger khi có người hủy Vote (nếu sếp cho phép)
	CREATE TRIGGER TRG_UpdateVoteCount_Delete ON Votes AFTER DELETE AS
	BEGIN
		UPDATE EventPosts 
		SET VoteCount = VoteCount - 1 
		FROM EventPosts ep JOIN deleted d ON ep.EventPostID = d.EventPostID;
	END;
	GO

	-- TRIGGER CHUẨN ĐỂ QUẢN LÝ ĐIỂM SỐ
CREATE OR ALTER TRIGGER trg_UpdatePostStats
ON InteractionLog
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;

    -- 1. XỬ LÝ LƯỢT LIKE / UNLIKE (1 Like = 5 Điểm)
    UPDATE p
    SET p.LikeCount = p.LikeCount + i.Value,
        p.TotalPts = p.TotalPts + (i.Value * 5)
    FROM Post p
    INNER JOIN inserted i ON p.PostID = i.PostID
    WHERE i.Type = 'LIKE';

    -- 2. XỬ LÝ LƯỢT VIEW (1 View = 1 Điểm)
    UPDATE p
    SET p.Views = p.Views + i.Value,
        p.TotalPts = p.TotalPts + (i.Value * 1)
    FROM Post p
    INNER JOIN inserted i ON p.PostID = i.PostID
    WHERE i.Type = 'VIEW';

    -- 3. XỬ LÝ LƯỢT RATING (Ví dụ: 1 sao = 10 Điểm)
    UPDATE p
    SET p.TotalPts = p.TotalPts + (i.Value * 10)
    FROM Post p
    INNER JOIN inserted i ON p.PostID = i.PostID
    WHERE i.Type = 'RATING';

END;
GO

---- TRIGGER xóa điểm rating của tổng điểm
CREATE OR ALTER TRIGGER trg_HandleCommentDeletion_UpdatePts
ON Comment
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    -- Kiểm tra xem có thao tác cập nhật cột IsActive hay không
    IF UPDATE(IsActive)
    BEGIN
        -- 1. TRỪ ĐIỂM khi comment bị XÓA (từ trạng thái Bình thường -> Xóa)
        -- (1/NULL biến thành 0/-1)
        UPDATE p
        SET p.TotalPts = p.TotalPts - (ISNULL(d.Rating, 0) * 10)
        FROM Post p
        INNER JOIN deleted d ON p.PostID = d.PostID
        INNER JOIN inserted i ON d.CommentID = i.CommentID
        WHERE (d.IsActive IS NULL OR d.IsActive = 1)  -- Trạng thái cũ là đang sống
          AND (i.IsActive = 0 OR i.IsActive = -1)     -- Trạng thái mới là đã chết/xóa
          AND ISNULL(d.Rating, 0) > 0;                -- Chỉ trừ khi comment đó thực sự có rating

        -- 2. (Tùy chọn) CỘNG LẠI ĐIỂM nếu Admin KHÔI PHỤC comment
        -- (0/-1 biến thành 1/NULL)
        UPDATE p
        SET p.TotalPts = p.TotalPts + (ISNULL(i.Rating, 0) * 10)
        FROM Post p
        INNER JOIN deleted d ON p.PostID = d.PostID
        INNER JOIN inserted i ON d.CommentID = i.CommentID
        WHERE (d.IsActive = 0 OR d.IsActive = -1)     -- Trạng thái cũ là đã xóa
          AND (i.IsActive IS NULL OR i.IsActive = 1)  -- Trạng thái mới là khôi phục
          AND ISNULL(i.Rating, 0) > 0;
    END
END;
GO

	-- ==========================================
	-- 7. Stored Procedure
	-- ==========================================

	-- PROCEDURE xóa các log có lượt VIEW và LIKE đã cũ hơn 1 năm
	CREATE OR ALTER PROCEDURE CleanupOldInteractionLogs
AS
BEGIN
    SET NOCOUNT ON;
    DISABLE TRIGGER ALL ON InteractionLog;
    -- 2. DỌN RÁC: Xóa các lượt VIEW và LIKE đã cũ hơn 1 năm (365 ngày)
    DELETE FROM InteractionLog 
    WHERE CreatedAt < DATEADD(year, -1, GETDATE())
      AND Type IN ('VIEW', 'LIKE');
    ENABLE TRIGGER ALL ON InteractionLog;
    -- In ra thông báo (dùng để check log hệ thống)
    PRINT 'Đã dọn dẹp thành công Log VIEW và LIKE cũ hơn 1 năm và BẢO TOÀN View/Like tổng!';
END;
GO


-- 1. DỮ LIỆU TÀI KHOẢN (Đa dạng phân quyền)
-- ==========================================================
INSERT INTO Account (Username, Email, Password, Avatar, Point, isAdmin, isPremium, isActive, Bio, CreatedAt) VALUES
(N'admin_gomet', 'admin@gomet.vn', '123', 'https://i.pravatar.cc/150?u=1', 9999, 1, 1, 1, N'Quản trị viên hệ thống.', '2025-01-01'),
(N'Nguyễn Khánh', 'Piggaming2007@gmail.com', '123', 'https://i.pravatar.cc/150?u=2', 5000, 0, 1, 1, N'Bếp trưởng nhà hàng 5 sao.', '2026-01-10'),
(N'Gia Hưng', 'tvghung@gmail.com', '123', 'https://i.pravatar.cc/150?u=3', 4200, 0, 1, 1, N'Chuyên gia món bánh Pháp.', '2026-01-15'),
(N'Vân Như', 'nhulvts00916@fpt.edu.vn', '123', 'https://i.pravatar.cc/150?u=4', 3800, 0, 1, 1, N'Ẩm thực đường phố Việt Nam.', '2026-01-20'),
(N'Quốc Bảo', 'baorqisme@gmail.com', '123', 'https://i.pravatar.cc/150?u=5', 500, 0, 1, 1, N'Người yêu nấu ăn.', '2026-02-01'),
(N'Nam Anh', 'namanh15092007@gmail.com', '123', 'https://i.pravatar.cc/150?u=6', 450, 0, 1, 1, N'Mẹ bỉm sữa chăm con.', '2026-02-05'),
(N'DCMinh Khánh', 'khanhdcmts00903@fpt.edu.vn', '123', 'https://i.pravatar.cc/150?u=7', 300, 0, 1, 1, N'Thích ăn chay.', '2026-02-10'),
(N'user1', 'test@gmail.com', '123', 'https://i.pravatar.cc/150?u=8', 250, 0, 1, 1, N'Gymmer - Health Care.', '2026-02-12'),
(N'user_em', 'em@gmail.com', '123', 'https://i.pravatar.cc/150?u=9', 150, 0, 0, 1, N'Tập tành nấu nướng.', '2026-02-15'),
(N'user_giang', 'giang@gmail.com', '123', 'https://i.pravatar.cc/150?u=10', 100, 0, 0, 1, N'Sinh viên xa nhà.', '2026-02-20'),
(N'user_hoa', 'hoa@gmail.com', '123', 'https://i.pravatar.cc/150?u=11', 80, 0, 0, 1, N'Eat Clean.', '2026-02-22'),
(N'ghost_1', 'ghost1@gmail.com', '123', 'https://i.pravatar.cc/150?u=12', 0, 0, 0, 1, N'Kẻ ẩn danh.', '2026-03-01'),
(N'ghost_2', 'ghost2@gmail.com', '123', 'https://i.pravatar.cc/150?u=13', 0, 0, 0, 1, N'Xem thôi không nấu.', '2026-03-05'),
(N'banned_user', 'badguy@gmail.com', '123', 'https://i.pravatar.cc/150?u=14', 10, 0, 0, 0, N'Tài khoản bị khóa.', '2026-01-01'),
(N'scammer', 'scam@gmail.com', '123', 'https://i.pravatar.cc/150?u=15', 5, 0, 0, 0, N'Vi phạm chính sách.', '2026-01-05');
GO

-- 2. DANH MỤC & THÀNH TỰU
-- ==========================================================
INSERT INTO Category (CategoryName, CategoryImage, IsActive) VALUES
(N'Món Việt', 'https://cdn-icons-png.flaticon.com/512/5328/5328065.png', 1),
(N'Món Á', 'https://cdn-icons-png.flaticon.com/512/2276/2276931.png', 1),
(N'Món Âu', 'https://cdn-icons-png.flaticon.com/512/5328/5328003.png', 1),
(N'Món Chay', 'https://cdn-icons-png.flaticon.com/512/2918/2918148.png', 1),
(N'Tráng Miệng', 'https://cdn-icons-png.flaticon.com/512/2550/2550300.png', 1),
(N'Món Chính', 'https://images.unsplash.com/photo-1544025162-d76694265947?w=200', 1),
(N'Hải sản', 'https://images.unsplash.com/photo-1519708227418-c8fd9a32b7a2?w=200', 1),
(N'Ăn vặt', 'https://images.unsplash.com/photo-1599487488170-d11ec9c172f0?w=200', 1),
(N'Gym Diet', 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=200', 1);

GO

-- 3. SỰ KIỆN (Trạng thái: Đang diễn ra và Đã kết thúc)
-- ==========================================================
INSERT INTO Event (EventName, BannerImage, Description, Reward, StartAt, EndAt, VoteStartAt, VoteEndAt, MaxVotes, Winner, IsActive) VALUES
(N'Vua Bếp Việt 2026', 'https://images.unsplash.com/photo-1555939594-58d7cb561ad1', N'Tìm kiếm công thức món Việt sáng tạo.', N'Huy hiệu Vàng & 1.000.000đ', '2026-03-01', '2026-04-01', '2026-04-02', '2026-04-10', 3, NULL, 1),
(N'Giải Cứu Món Chay', 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd', N'Thử thách nấu món chay ngon.', N'Gói Premium 1 năm', '2026-02-01', '2026-03-15', '2026-03-16', '2026-03-20', 1, 2, 1),
(N'Món Ngon Giải Nhiệt', 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=800', N'Sáng tạo món ăn mùa hè.', N'Gói Premium trọn đời', '2026-04-01', '2026-05-01', '2026-05-02', '2026-05-10', 2, NULL, 1);
GO

-- 4. BÀI VIẾT (Gồm bài đã duyệt, chưa duyệt và bài tham gia sự kiện)
-- ==========================================================
INSERT INTO Post (AccountID, CategoryID, EventID, Title, Description, Ingredients, Media, Level, CookingTime, Views, LikeCount, isActive, isApproved, CreatedAt) VALUES
-- Bài đã duyệt (isApproved = 1)
(2, 1, NULL, N'Bún Chả Hà Nội', N'Hương vị thịt nướng than hoa đặc trưng.', N'Thịt ba chỉ, thịt băm, nước mắm, đu đủ xanh, bún.', 'https://images.unsplash.com/photo-1526318896980-cf78c088247c?w=800', 2, 60, 150, 45, 1, 1, GETDATE()),
(3, 5, NULL, N'Bánh Flan Siêu Mịn', N'Công thức không bị rỗ, tan ngay trong miệng.', N'Trứng gà, sữa tươi, đường, vani.', 'https://images.unsplash.com/photo-1570476922354-81227cdbb76c?w=800', 1, 45, 300, 89, 1, 1, GETDATE()),
(4, 6, NULL, N'Mì Quảng Tôm Thịt', N'Món ăn đậm đà xứ Quảng.', N'Mì quảng, tôm, thịt heo, đậu phộng, bánh tráng.', 'https://images.unsplash.com/photo-1624300627238-d67064ec34a0?w=800', 2, 90, 210, 34, 1, 1, GETDATE()),
(5, 9, NULL, N'Salad Ức Gà Sữa Chua', N'Món ăn Eat Clean nhẹ nhàng.', N'Ức gà, xà lách, sữa chua không đường, hạt điều.', 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=800', 1, 20, 500, 120, 1, 1, GETDATE()),
(2, 7, NULL, N'Mực Nhồi Thịt Sốt Cà', N'Mực tươi giòn quyện sốt cà chua đậm đà.', N'Mực ống, thịt băm, mộc nhĩ, cà chua.', 'https://images.unsplash.com/photo-1599487488170-d11ec9c172f0?w=800', 2, 40, 120, 25, 1, 1, GETDATE()),

-- Bài chưa duyệt (isApproved = 0)
(3, 8, NULL, N'Khoai Tây Chiên Bơ Tỏi', N'Món ăn vặt giòn tan thơm nức.', N'Khoai tây, bơ, tỏi băm, muối.', 'https://images.unsplash.com/photo-1518013431117-eb1465fd5752?w=800', 1, 25, 10, 0, 1, 0, GETDATE()),
(4, 5, NULL, N'Nước Ép Cam Dứa', N'Thức uống vitamin cho ngày mới.', N'Cam tươi, dứa mật, mật ong.', 'https://images.unsplash.com/photo-1613478223719-2ab802602423?w=800', 1, 10, 5, 2, 1, 0, GETDATE()),
(5, 1, NULL, N'Canh Kiểm Chay Nam Bộ', N'Món chay truyền thống ngọt bùi.', N'Khoai lang, bí đỏ, đậu phộng, nước cốt dừa.', 'https://images.unsplash.com/photo-1547592166-23ac45744acd?w=800', 2, 50, 15, 1, 1, 0, GETDATE()),

-- Bài tham gia sự kiện (EventID = 1 hoặc 2)
(6, 4, 1, N'Lẩu Nấm Chay Thập Cẩm', N'Dự thi Vua Bếp Chay 2026.', N'Nấm kim châm, nấm đùi gà, đậu hũ, rau cải.', 'https://images.unsplash.com/photo-1552611052-33e04de081de?w=800', 2, 45, 80, 15, 1, 1, GETDATE()),
(7, 5, 2, N'Trà Trái Cây Nhiệt Đới', N'Dự thi Món Ngon Giải Nhiệt.', N'Trà lài, xoài, dưa hấu, chanh dây.', 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=800', 1, 15, 150, 40, 1, 1, GETDATE());

GO

-- 5. CÁC BƯỚC NẤU ĂN (CookingSteps)
-- ==========================================================
INSERT INTO CookingSteps (PostID, StepNumber, Content, Image) VALUES
-- 26. Bún Chả Hà Nội
(1, 1, N'Sơ chế thịt ba chỉ thái miếng mỏng và thịt nạc vai băm nhỏ để làm chả viên.', 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=200'),
(1, 2, N'Ướp thịt với nước mắm, đường, hành khô băm, nước hàng và tiêu trong 30 phút.', 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=200'),
(1, 3, N'Kẹp thịt vào vỉ nướng trên than hoa cho đến khi vàng đều hai mặt và có mùi thơm.', 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=200'),
(1, 4, N'Pha nước mắm chua ngọt từ mắm, đường, giấm, nước lọc và thêm tỏi ớt băm.', 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=200'),
(1, 5, N'Làm dưa góp từ đu đủ xanh và cà rốt, trộn chút muối, đường, giấm để ăn kèm.', 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=200'),

-- 27. Bánh Flan Siêu Mịn
(2, 1, N'Thắng đường với một ít nước cho đến khi chuyển màu cánh gián, đổ vào đáy khuôn.', 'https://images.unsplash.com/photo-1551024506-0bccd828d307?w=200'),
(2, 2, N'Khuấy nhẹ trứng gà với đường, chú ý không làm nổi bọt khí để bánh được mịn.', 'https://images.unsplash.com/photo-1551024506-0bccd828d307?w=200'),
(2, 3, N'Đun sữa tươi đến khi ấm nóng rồi đổ từ từ vào hỗn hợp trứng, vừa đổ vừa khuấy.', 'https://images.unsplash.com/photo-1551024506-0bccd828d307?w=200'),
(2, 4, N'Lọc hỗn hợp qua rây ít nhất 2 lần để loại bỏ lợn cợn trứng thừa.', 'https://images.unsplash.com/photo-1551024506-0bccd828d307?w=200'),
(2, 5, N'Hấp cách thủy ở lửa nhỏ nhất trong 30-40 phút, dùng khăn phủ miệng nồi tránh đọng nước.', 'https://images.unsplash.com/photo-1551024506-0bccd828d307?w=200'),

-- 28. Mì Quảng Tôm Thịt
(3, 1, N'Sơ chế tôm bỏ chỉ lưng, thịt heo cắt miếng vừa ăn, ướp gia vị đậm đà.', 'https://images.unsplash.com/photo-1547592166-23ac45744acd?w=200'),
(3, 2, N'Phi thơm nén (hoặc tỏi), cho thịt và tôm vào xào săn, thêm dầu điều cho màu đẹp.', 'https://images.unsplash.com/photo-1547592166-23ac45744acd?w=200'),
(3, 3, N'Đổ nước dùng vào đun sôi, nêm nếm hơi đậm so với nước dùng phở thông thường.', 'https://images.unsplash.com/photo-1547592166-23ac45744acd?w=200'),
(3, 4, N'Trần mì quảng qua nước sôi, xếp ra bát cùng rau sống, hoa chuối.', 'https://images.unsplash.com/photo-1547592166-23ac45744acd?w=200'),
(3, 5, N'Chan nước dùng xấp mặt mì, thêm đậu phộng rang và bánh tráng nướng bẻ nhỏ.', 'https://images.unsplash.com/photo-1547592166-23ac45744acd?w=200'),

-- 29. Salad Ức Gà Sữa Chua
(4, 1, N'Ức gà rửa sạch, luộc chín với chút gừng rồi xé miếng nhỏ hoặc cắt hạt lựu.', 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=200'),
(4, 2, N'Rửa sạch xà lách, cà chua bi, dưa leo rồi để thật ráo nước.', 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=200'),
(4, 3, N'Pha sốt: trộn sữa chua không đường với mật ong, nước cốt chanh và ít tiêu.', 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=200'),
(4, 4, N'Cho rau củ và gà vào tô lớn, rưới hỗn hợp sốt sữa chua lên trên.', 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=200'),
(4, 5, N'Trộn đều, bày ra đĩa và rắc thêm hạt điều rang lên trên cùng để tạo độ giòn.', 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=200'),

-- 30. Mực Nhồi Thịt Sốt Cà
(5, 1, N'Mực làm sạch, để nguyên con. Thịt băm trộn với mộc nhĩ, hành khô và gia vị.', 'https://images.unsplash.com/photo-1599487488170-d11ec9c172f0?w=200'),
(5, 2, N'Nhồi thịt vào bụng mực, dùng tăm ghim chặt đầu mực để thịt không bị rơi ra.', 'https://images.unsplash.com/photo-1599487488170-d11ec9c172f0?w=200'),
(5, 3, N'Hấp mực sơ qua trong 5 phút để mực săn lại và thịt bên trong chín tái.', 'https://images.unsplash.com/photo-1599487488170-d11ec9c172f0?w=200'),
(5, 4, N'Xào cà chua băm nhuyễn với chút dầu ăn thành hỗn hợp sốt sệt, nêm gia vị.', 'https://images.unsplash.com/photo-1599487488170-d11ec9c172f0?w=200'),
(5, 5, N'Cho mực vào chảo sốt cà chua, đun nhỏ lửa cho mực thấm vị rồi cắt miếng vừa ăn.', 'https://images.unsplash.com/photo-1599487488170-d11ec9c172f0?w=200'),

-- 31. Khoai Tây Chiên Bơ Tỏi
(6, 1, N'Khoai tây gọt vỏ, cắt thanh dài, ngâm nước muối để không bị thâm.', 'https://images.unsplash.com/photo-1518013431117-eb1465fd5752?w=200'),
(6, 2, N'Luộc sơ khoai trong nước sôi khoảng 3 phút rồi vớt ra để thật khô.', 'https://images.unsplash.com/photo-1518013431117-eb1465fd5752?w=200'),
(6, 3, N'Chiên khoai ngập dầu lần 1 ở lửa vừa, vớt ra để nguội bớt.', 'https://images.unsplash.com/photo-1518013431117-eb1465fd5752?w=200'),
(6, 4, N'Chiên lần 2 ở lửa lớn để lớp vỏ đạt độ giòn tan nhất định.', 'https://images.unsplash.com/photo-1518013431117-eb1465fd5752?w=200'),
(6, 5, N'Đun chảy bơ lạt, phi thơm tỏi băm rồi cho khoai đã chiên vào đảo nhanh tay.', 'https://images.unsplash.com/photo-1518013431117-eb1465fd5752?w=200'),

-- 32. Nước Ép Cam Dứa
(7, 1, N'Cam vắt lấy nước cốt, bỏ hạt để tránh bị đắng.', 'https://images.unsplash.com/photo-1613478223719-2ab802602423?w=200'),
(7, 2, N'Dứa gọt vỏ, bỏ mắt, cắt miếng nhỏ để chuẩn bị ép.', 'https://images.unsplash.com/photo-1613478223719-2ab802602423?w=200'),
(7, 3, N'Cho dứa vào máy ép lấy nước cốt nguyên chất.', 'https://images.unsplash.com/photo-1613478223719-2ab802602423?w=200'),
(7, 4, N'Hòa trộn nước cam và nước dứa theo tỷ lệ 1:1, thêm chút mật ong nếu thích ngọt.', 'https://images.unsplash.com/photo-1613478223719-2ab802602423?w=200'),
(7, 5, N'Thêm đá viên vào ly, trang trí bằng một lát cam tươi và thưởng thức.', 'https://images.unsplash.com/photo-1613478223719-2ab802602423?w=200'),

-- 33. Canh Kiểm Chay Nam Bộ
(8, 1, N'Sơ chế khoai lang, bí đỏ, mướp khía cắt miếng vuông vừa ăn.', 'https://images.unsplash.com/photo-1547592166-23ac45744acd?w=200'),
(8, 2, N'Luộc chín đậu phộng và rang vàng sả cây đập dập.', 'https://images.unsplash.com/photo-1547592166-23ac45744acd?w=200'),
(8, 3, N'Nấu nước dão dừa (nước cốt dừa lần 2) cùng các loại khoai và bí.', 'https://images.unsplash.com/photo-1547592166-23ac45744acd?w=200'),
(8, 4, N'Khi rau củ mềm, cho đậu hũ và bún tàu vào nấu cùng, nêm đường muối.', 'https://images.unsplash.com/photo-1547592166-23ac45744acd?w=200'),
(8, 5, N'Đổ nước cốt dừa đặc vào sau cùng, đun sôi lại rồi tắt bếp để giữ độ béo.', 'https://images.unsplash.com/photo-1547592166-23ac45744acd?w=200'),

-- 34. Lẩu Nấm Chay Thập Cẩm
(9, 1, N'Rửa sạch tất cả các loại nấm, ngâm nước muối loãng rồi để ráo.', 'https://images.unsplash.com/photo-1552611052-33e04de081de?w=200'),
(9, 2, N'Hầm su su, cà rốt và củ cải trắng trong 45 phút để lấy nước dùng ngọt.', 'https://images.unsplash.com/photo-1552611052-33e04de081de?w=200'),
(9, 3, N'Phi thơm hành boaro, đổ nước dùng vào nồi lẩu, nêm gia vị chay vừa miệng.', 'https://images.unsplash.com/photo-1552611052-33e04de081de?w=200'),
(9, 4, N'Xếp nấm, đậu hũ và các loại rau xanh ra đĩa lớn.', 'https://images.unsplash.com/photo-1552611052-33e04de081de?w=200'),
(9, 5, N'Khi nước lẩu sôi, nhúng nấm vào và ăn kèm với bún tươi hoặc mì sợi.', 'https://images.unsplash.com/photo-1552611052-33e04de081de?w=200'),

-- 35. Trà Trái Cây Nhiệt Đới
(10, 1, N'Ủ trà lài với nước nóng 80 độ C trong 5 phút rồi lọc bỏ bã trà.', 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=200'),
(10, 2, N'Sơ chế trái cây: xoài, dưa hấu cắt hạt lựu, chanh dây lấy cốt.', 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=200'),
(10, 3, N'Hòa tan đường hoặc mật ong vào nước trà tùy theo khẩu vị.', 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=200'),
(10, 4, N'Cho nước trà vào bình lắc cùng chanh dây và đá viên, lắc mạnh tay.', 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=200'),
(10, 5, N'Rót trà ra ly, thêm các loại trái cây tươi đã cắt lên trên mặt.', 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=200');
GO

-- 6. CMT (Bình luận đa tầng)
-- ==========================================================
-- Lưu ý: cmtid là cột lưu ID của bình luận cha
INSERT INTO Comment (AccountID, PostID, Content, Rating, IsActive) VALUES 
(8, 2, N'Món Steak này mình làm thử rồi, mềm và ngon lắm!', 5, 1),
(10, 3, N'Nước sốt vang hơi nhạt so với khẩu vị của mình.', 4, 1),
(7, 5, N'Tiramisu này làm tặng người yêu hết sảy luôn.', 5, 1),
(11, 9, N'Món chay mà nhìn bắt mắt quá, sẽ thử vào rằm tới.', 5, 1),
(9, 10, N'Chân gà giòn sần sật, công thức mắm tắc rất chuẩn!', 5, 1);
GO

-- 7. TƯƠNG TÁC (Likes, CommentLike, Favorite, Follow, Votes)
-- ==========================================================
INSERT INTO Likes (AccountID, PostID) VALUES (1, 1), (4, 1), (2, 2);

INSERT INTO CommentLike (AccountID, CommentID) VALUES (1, 1), (2, 1);
INSERT INTO Favorite (AccountID, PostID) VALUES (3, 1), (4, 2), (3, 3);
INSERT INTO Follow (FollowerID, FolloweeID) VALUES (5, 2), (6, 2), (7, 2), (8, 2), (9, 2), (10, 2), (2, 3), (3, 2);
GO

-- Bài tham gia sự kiện
INSERT INTO EventPosts (EventID, PostID, VoteCount) VALUES 
(2, 10, 0),
(2, 9, 0),
(2, 8, 0), 
(2, 7, 0),
(2, 4, 0);
GO
INSERT INTO Votes (AccountID, EventPostID) VALUES (5, 1), (8, 1), (9, 1), (5, 2), (10, 2);
GO

-- 8. TIỆN ÍCH (Note, Shopping, MealPlan)
-- ==========================================
INSERT INTO Note (AccountID, PostID, Content) VALUES (3, 1, N'Lần sau nên cho ít muối hơn một chút.');
INSERT INTO ShoppingList (AccountID, PostID, IngredientName, IsBought) VALUES 
(3, 1, N'Thịt bò thăn', 1), (3, 1, N'Rượu vang đỏ', 0), (4, 2, N'Bánh phở', 0);

INSERT INTO MealPlan (AccountID, PostID, CustomMealName, PlanDate, MealType, Notes) VALUES 
(3, 1, N'Kỷ niệm ngày cưới', '2026-03-25', N'Dinner', N'Nấu món Âu sang chảnh.'),
(3, 4, N'Thứ 2 thanh tịnh', '2026-03-23', N'Lunch', N'Ăn chay tốt cho sức khỏe.');
GO

-- 9. HỆ THỐNG (Notification, Search, History, Payment)
-- ==========================================================
INSERT INTO Notification (Title, Content, Type, AccountID, Link, PostID, isRead) VALUES 
(N'Bài viết đã được duyệt', N'Chúc mừng! Bài Steak của bạn đã được Admin phê duyệt.', 'SYSTEM', 2, '/post/1', 1, 0),
(N'Lượt thích mới', N'nguyenvana đã thích bài viết của bạn.', 'LIKE', 2, '/post/1', 1, 1);

INSERT INTO SearchHistory (AccountID, Keyword) VALUES (3, N'Bún bò'), (3, N'Món chay ngon');
INSERT INTO History (AccountID, PostID) VALUES (3, 1), (3, 2);

-- Thanh toán Premium
INSERT INTO PaymentTransaction (AccountID, OrderCode, Amount, PlanType, Status, PaidAt) VALUES 
(2, 'GOMET_PRE_001', 99000, 1, 'PAID', '2026-01-10'),
(3, 'GOMET_PRE_002', 990000, 2, 'PAID', '2026-02-15');

INSERT INTO Subscription (AccountID, PlanType, StartAt, EndAt, isActive, TransactionID) VALUES 
(2, 1, '2026-01-10', '2026-02-10', 0, 1), -- Hết hạn
(3, 2, '2026-02-15', '2027-02-15', 1, 2); -- Đang dùng
GO

-- 10. TIN NHẮN (Conversations & Messages)
-- ==========================================================
INSERT INTO Conversation (UserOneID, UserTwoID) VALUES (2, 3); -- Đầu bếp Quang & Nguyễn Văn A
INSERT INTO Message (ConversationID, SenderID, Content, IsRead) VALUES 
(1, 3, N'Chào Chef, cho em hỏi công thức Steak này dùng bò nội được không?', 1),
(1, 2, N'Được bạn nhé, nhưng nhớ đập dập thịt cho mềm.', 0);
GO

-- 11. HỖ TRỢ (Tickets)
-- ==========================================================
INSERT INTO Ticket (AccountID, TicketType, TargetPostID, Title, Description, Status, CreatedAt) VALUES 
(4, 'REPORT', 1, N'Nội dung sai sự thật', N'Ảnh này lấy trên mạng chứ không phải chủ bài viết nấu.', 0, GETDATE()),
(3, 'SUPPORT', NULL, N'Lỗi thanh toán', N'Tôi đã chuyển khoản nhưng chưa lên Premium.', 1, '2026-03-20');
GO

INSERT INTO BlacklistWord (Word) VALUES 
(N'cờ bạc'), (N'lô đề'), (N'phản động'), (N'chửi thề'), (N'18+'), (N'đánh bài');
GO

INSERT INTO Appeals (Email, AccountID, Reason, Status) VALUES ('badguy@gmail.com', 14, N'Xin mở khóa.', 'Pending');
GO


INSERT INTO SystemConfig (ConfigKey, ConfigValue, ConfigGroup, Description, UpdatedAt)
VALUES 
-- Bài viết nổi bật
('HERO_POST_1', '9', 'SYSTEM', N'ID bài viết nổi bật vị trí 1', GETDATE()),
('HERO_POST_2', '12', 'SYSTEM', N'ID bài viết nổi bật vị trí 2', GETDATE()),
('HERO_POST_3', '1', 'SYSTEM', N'ID bài viết nổi bật vị trí 3', GETDATE()),

-- Bảng giá Premium
('PREMIUM_PRICE_1_MONTH', '50000', 'PRICING', N'Giá gói Premium 1 Tháng', GETDATE()),
('PREMIUM_PRICE_12_MONTHS', '500000', 'PRICING', N'Giá gói Premium 1 Năm', GETDATE()),
('PREMIUM_PRICE_LIFETIME', '999000', 'PRICING', N'Giá gói Premium Vĩnh Viễn', GETDATE()),

-- Vận hành & Sự kiện (Hybrid)
('FREE_ACCESS_EVENT', 'FALSE', 'SYSTEM', N'Nút gạt cưỡng bức: TRUE để mở khóa MIỄN PHÍ ngay lập tức', GETDATE()),
('HOLIDAY_START', '2026-04-30T00:00', 'SYSTEM', N'Thời gian bắt đầu tự động mở khóa (YYYY-MM-DDTHH:mm)', GETDATE()),
('HOLIDAY_END', '2026-05-01T23:59', 'SYSTEM', N'Thời gian kết thúc tự động mở khóa (YYYY-MM-DDTHH:mm)', GETDATE()),

-- Quảng cáo Popup
('ADS_BANNER_IMG', 'https://res.cloudinary.com/drblrjxan/image/upload/v1/system/ads_default.jpg', 'ADS', N'Link ảnh Banner quảng cáo Popup', GETDATE()),
('ADS_TARGET_URL', 'https://gomet.id.vn/premium-info', 'ADS', N'Đường dẫn khi User click vào ảnh quảng cáo', GETDATE());
GO

