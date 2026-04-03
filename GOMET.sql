
	
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
	AccountID INT NOT NULL,
	ActorID INT NULL, 
	Link NVARCHAR(500) NULL,
	PostID INT NULL, 
	isRead INT DEFAULT 0,
	ReadAt DATETIME,
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
		IsRead INT DEFAULT 0,
		CreatedAt DATETIME DEFAULT GETDATE(),

		CONSTRAINT FK_Msg_Conv FOREIGN KEY (ConversationID) REFERENCES Conversation(ConversationID),
		CONSTRAINT FK_Msg_Sender FOREIGN KEY (SenderID) REFERENCES Account(AccountID)
	);
	GO

	USE DATN_CD;
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
(N'admin_gomet', 'admin@gomet.vn', '123', 'https://i.pravatar.cc/150?u=1', 9999, 1, 1, 1, N'Quản trị viên hệ thống GoMet.', '2025-01-01'),
(N'chef_quang', 'quangchef@gmail.com', '123', 'https://i.pravatar.cc/150?u=2', 2500, 0, 1, 1, N'Đầu bếp chuyên nghiệp với 10 năm kinh nghiệm món Âu.', '2026-01-10'),
(N'nguyenvana', 'vana@gmail.com', '123', 'https://i.pravatar.cc/150?u=3', 450, 0, 1, 1, N'Yêu nấu ăn và chia sẻ công thức gia đình.', '2026-02-15'),
(N'tranthib', 'thib@gmail.com', '123', 'https://i.pravatar.cc/150?u=4', 120, 0, 0, 1, N'Sinh viên tập tành vào bếp.', '2026-03-01'),
(N'le_minh', 'minhle@gmail.com', '123', 'https://i.pravatar.cc/150?u=5', 50, 0, 0, 1, N'Thích ăn ngon nhưng lười nấu.', '2026-03-10'),
(N'pham_dung', 'dungpham@gmail.com', '123', 'https://i.pravatar.cc/150?u=6', 0, 0, 0, 0, N'Tài khoản bị khóa để demo.', '2026-03-15');
GO

-- 2. DANH MỤC & THÀNH TỰU
-- ==========================================================
INSERT INTO Category (CategoryName, CategoryImage, IsActive) VALUES
(N'Món Việt', 'https://cdn-icons-png.flaticon.com/512/5328/5328065.png', 1),
(N'Món Á', 'https://cdn-icons-png.flaticon.com/512/2276/2276931.png', 1),
(N'Món Âu', 'https://cdn-icons-png.flaticon.com/512/5328/5328003.png', 1),
(N'Món Chay', 'https://cdn-icons-png.flaticon.com/512/2918/2918148.png', 1),
(N'Tráng Miệng', 'https://cdn-icons-png.flaticon.com/512/2550/2550300.png', 1);

-- 3. SỰ KIỆN (Trạng thái: Đang diễn ra và Đã kết thúc)
-- ==========================================================
INSERT INTO Event (EventName, BannerImage, Description, Reward, StartAt, EndAt, VoteStartAt, VoteEndAt, MaxVotes, Winner, IsActive) VALUES
(N'Vua Bếp Việt 2026', 'https://images.unsplash.com/photo-1555939594-58d7cb561ad1', N'Tìm kiếm công thức món Việt sáng tạo.', N'Huy hiệu Vàng & 1.000.000đ', '2026-03-01', '2026-04-01', '2026-04-02', '2026-04-10', 3, NULL, 1),
(N'Giải Cứu Món Chay', 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd', N'Thử thách nấu món chay ngon.', N'Gói Premium 1 năm', '2026-02-01', '2026-03-15', '2026-03-16', '2026-03-20', 1, 2, 1);
GO

-- 4. BÀI VIẾT (Gồm bài đã duyệt, chưa duyệt và bài tham gia sự kiện)
-- ==========================================================
INSERT INTO Post (AccountID, CategoryID, EventID, Title, Description, Ingredients, Media, Level, CookingTime, Views, LikeCount, isActive, isApproved, CreatedAt) VALUES
(2, 3, NULL, N'Steak Bò Mỹ Sốt Vang Đỏ', N'Món Âu sang trọng cho tối lãng mạn.', N'Bò Mỹ 300g, Rượu vang đỏ, Lá hương thảo, Bơ.', 'https://images.unsplash.com/photo-1546241072-48010ad28c2c', 3, 45, 1500, 0, 1, 1, DATEADD(HOUR, -2, GETDATE())),
(3, 1, 1, N'Phở Bò Nam Định Chuẩn Vị', N'Nước dùng trong, ngọt thanh từ xương.', N'Bánh phở, Xương ống, Gừng, Quế, Hồi.', 'https://images.unsplash.com/photo-1582878826629-29b7ad1cdc43', 2, 180, 850, 0, 1, 1, DATEADD(DAY, -1, GETDATE())),
(4, 5, NULL, N'Bánh Tiramisu Không Cần Lò', N'Công thức siêu dễ cho người mới.', N'Bánh sâm panh, Cà phê, Mascarpone.', 'https://images.unsplash.com/photo-1571877227200-a0d98ea607e9', 1, 30, 320, 0, 1, 1, DATEADD(MINUTE, -30, GETDATE())),
(3, 4, 2, N'Gỏi Cuốn Chay Ngũ Sắc', N'Món chay thanh đạm, bắt mắt.', N'Bánh tráng, Bún, Đậu phụ, Rau thơm.', 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd', 1, 20, 210, 8, 1, 1, '2026-03-10'),
(5, 1, NULL, N'Bún Chả Hà Nội', N'Đang chờ phê duyệt...', N'Thịt ba chỉ, bún, mắm.', 'https://images.unsplash.com/photo-1562967914-6c189891c92a', 2, 60, 5, 0, 1, 0, GETDATE());
GO

-- 5. CÁC BƯỚC NẤU ĂN (CookingSteps)
-- ==========================================================
INSERT INTO CookingSteps (PostID, StepNumber, Content, Image) VALUES
(1, 1, N'Ướp thịt bò với muối, tiêu và tỏi băm.', 'https://i.imgur.com/example1.jpg'),
(1, 2, N'Áp chảo thịt với bơ và lá hương thảo cho vàng đều các mặt.', NULL),
(1, 3, N'Làm sốt vang đỏ từ nước dùng bò và rượu.', NULL),
(2, 1, N'Ninh xương bò trong 5 tiếng để lấy nước dùng ngọt.', NULL),
(3, 1, N'Nhúng bánh sâm panh vào cà phê rồi xếp lớp.', NULL);
GO

-- 6. BÌNH LUẬN & TRẢ LỜI (Bình luận đa tầng)
-- ==========================================================
-- Lưu ý: cmtid là cột lưu ID của bình luận cha
INSERT INTO Comment (AccountID, PostID, Content, Rating, Likes, cmtid, CreatedAt) VALUES
(4, 1, N'Nhìn ngon quá sếp ơi!', 5, 5, NULL, DATEADD(HOUR, -1, GETDATE())), -- CommentID 1
(2, 1, N'Cảm ơn bạn nhé, chúc bạn thành công!', 0, 2, 1, DATEADD(MINUTE, -30, GETDATE())), -- Reply cho ID 1
(5, 1, N'Cho mình hỏi mua rượu vang loại nào thì ngon?', 0, 0, 1, DATEADD(MINUTE, -10, GETDATE())), -- Reply cho ID 1
(3, 2, N'Phở này nhìn nước dùng hơi đậm màu nhỉ?', 4, 1, NULL, '2026-03-20');
GO

-- 7. TƯƠNG TÁC (Likes, CommentLike, Favorite, Follow, Votes)
-- ==========================================================
INSERT INTO Likes (AccountID, PostID) VALUES (1, 1), (3, 1), (4, 1), (5, 1), (2, 2);
INSERT INTO CommentLike (AccountID, CommentID) VALUES (1, 1), (2, 1);
INSERT INTO Favorite (AccountID, PostID) VALUES (3, 1), (4, 2), (3, 3);


-- Bài tham gia sự kiện
INSERT INTO EventPosts (EventID, PostID, VoteCount) VALUES (1, 2, 15), (2, 4, 10);
INSERT INTO Votes (AccountID, EventPostID) VALUES (1, 1), (4, 1), (5, 2);
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


	SELECT * FROM Post;

	SELECT * FROM Cookingsteps;

	SELECT * FROM event;

	SELECT * FROM Votes;

	SELECT * FROM Likes;

	SELECT * FROM account;

	SELECT * FROM Appeals;

	SELECT * FROM Comment;

	SELECT * FROM Follow;

	SELECT * FROM Paymenttransaction;

	SELECT * FROM Subscription;

	SELECT * FROM Appeals;
	
	SELECT * FROM ModerationLog;
