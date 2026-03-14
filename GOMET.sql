	-- Bước 1: Trở về database master (Bắt buộc)
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
		Token NVARCHAR(255) NULL, 
		Bio NVARCHAR(MAX) NULL,
		Point INT DEFAULT 0,
		isAdmin INT DEFAULT 0,
		isPremium INT DEFAULT 0,
		isActive INT DEFAULT 1,
		CreatedAt DATETIME DEFAULT GETDATE(),
		UpdatedAt DATETIME,
		DeletedAt DATETIME
	
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
    
		-- Ảnh đại diện
		BannerImage NVARCHAR(MAX),
    
		-- MÔ TẢ & THỂ LỆ
		Description NVARCHAR(MAX),
		Rules NVARCHAR(MAX),
		Reward NVARCHAR(255),
    
		-- THỜI GIAN
		StartAt DATETIME NOT NULL,
		EndAt DATETIME NOT NULL,
		VoteStartAt DATETIME NOT NULL,
		VoteEndAt DATETIME NOT NULL,

		MaxVotes INT DEFAULT 3,
		-- NGƯỜI THẮNG
		Winner INT NULL, 
		CONSTRAINT FK_Event_Account FOREIGN KEY (Winner) REFERENCES Account(AccountID)
	);
	GO

	CREATE TABLE Achievement (
		AchievementID INT IDENTITY(1,1) PRIMARY KEY,
		AchievementName NVARCHAR(255) NOT NULL,
		Description NVARCHAR(MAX) NOT NULL,
		Icon NVARCHAR(255)
	);
	GO

	-- ==========================================
	-- 2. NHÓM BẢNG CHÍNH (POSTS & SỰ KIỆN)
	-- ==========================================

	CREATE TABLE Post (
		PostID INT IDENTITY(1,1) PRIMARY KEY,
		AccountID INT NOT NULL,
		CategoryID INT NOT NULL,
		EventID INT NULL, -- TRẢ LẠI EM NÓ Ở ĐÂY
		Title NVARCHAR(255) NOT NULL,
		Description NVARCHAR(MAX) NOT NULL,
		Ingredients NVARCHAR(MAX) NOT NULL,
		Media NVARCHAR(255),
		Video NVARCHAR(255),
		Level INT DEFAULT 1,
		CookingTime INT DEFAULT 30,
		Views INT DEFAULT 0,
		LikeCount INT DEFAULT 0,
		isActive INT DEFAULT 1,
		isApproved INT DEFAULT 0,
		CreatedAt DATETIME DEFAULT GETDATE(),

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
    PostID INT NULL,             
    cmtid INT NULL,             
    Content NVARCHAR(MAX) NOT NULL,
    CreatedAt DATETIME DEFAULT GETDATE(),

    -- Các khóa ngoại
    CONSTRAINT FK_Comment_Account FOREIGN KEY (AccountID) REFERENCES Account(AccountID),
    CONSTRAINT FK_Comment_Post FOREIGN KEY (PostID) REFERENCES Post(PostID),
    -- Khóa ngoại tự tham chiếu (Self-reference) để làm cây bình luận
    CONSTRAINT FK_Comment_ParentComment FOREIGN KEY (cmtid) REFERENCES Comment(CommentID)
);


	CREATE TABLE Rating (
		RatingID INT IDENTITY(1,1) PRIMARY KEY,
		AccountID INT NOT NULL,
		PostID INT NOT NULL,
		Rate INT NOT NULL,
		CreatedAt DATETIME DEFAULT GETDATE(),

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
		Status INT DEFAULT 0,
		FollowedAt DATETIME DEFAULT GETDATE(),
		UnFollowedAt DATETIME,

		CONSTRAINT FK_Follow_Follower FOREIGN KEY (FollowerID) REFERENCES Account(AccountID),
		CONSTRAINT FK_Follow_Followee FOREIGN KEY (FolloweeID) REFERENCES Account(AccountID)
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
		PostID INT NULL, 
		isRead INT DEFAULT 0,
		ReadAt DATETIME,
		CreatedAt DATETIME DEFAULT GETDATE(),

		CONSTRAINT FK_Notification_Account FOREIGN KEY (AccountID) REFERENCES Account(AccountID),
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

	-- Bảng trung gian nối Account với Achievement
	CREATE TABLE UserAchievement (
		UAID INT IDENTITY(1,1) PRIMARY KEY,
		AccountID INT NOT NULL,
		AchievementID INT NOT NULL,
		ReceivedAt DATETIME DEFAULT GETDATE(),

		CONSTRAINT FK_UA_Account FOREIGN KEY (AccountID) REFERENCES Account(AccountID),
		CONSTRAINT FK_UA_Achievement FOREIGN KEY (AchievementID) REFERENCES Achievement(AchievementID)
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
		AccountID INT NOT NULL,            -- Người gửi ticket
    
		-- Phân loại Ticket (Dùng code để quy định)
		-- VD: 'BUG' (Báo lỗi), 'REPORT' (Báo cáo vi phạm), 'FEEDBACK' (Góp ý)
		TicketType NVARCHAR(50) NOT NULL,  
    
		-- Dành riêng cho loại 'REPORT' (Nếu họ báo cáo một bài viết)
		TargetPostID INT NULL,             
    
		-- Nội dung chính
		Title NVARCHAR(255) NOT NULL,
		Description NVARCHAR(MAX) NOT NULL,
		Attachment NVARCHAR(500) NULL,     -- Ảnh đính kèm (Lỗi, hoặc bằng chứng vi phạm)
    
		-- Trạng thái xử lý (Quy trình Admin)
		-- VD: 0 = 'Chưa xem', 1 = 'Đang xử lý', 2 = 'Đã giải quyết', 3 = 'Từ chối'
		Status INT DEFAULT 0,              
    
		-- Tracking thời gian
		CreatedAt DATETIME DEFAULT GETDATE(),
		ResolvedAt DATETIME NULL,
		ProcessedAt DATETIME NULL,  -- Thời gian Admin xử lý xong
    
		-- Khóa ngoại
		CONSTRAINT FK_Ticket_Account FOREIGN KEY (AccountID) REFERENCES Account(AccountID),
		CONSTRAINT FK_Ticket_Post FOREIGN KEY (TargetPostID) REFERENCES Post(PostID)
	);
	GO
	-- ==========================================
	-- 6. TRIGGERS TỰ ĐỘNG CẬP NHẬT
	-- ==========================================

	GO
	CREATE TRIGGER TRG_UpdateLikeCount_Insert ON Likes AFTER INSERT AS
	BEGIN
		UPDATE Post SET LikeCount = LikeCount + 1 FROM Post p JOIN inserted i ON p.PostID = i.PostID;
	END;
	GO
	CREATE TRIGGER TRG_UpdateLikeCount_Delete ON Likes AFTER DELETE AS
	BEGIN
		UPDATE Post SET LikeCount = LikeCount - 1 FROM Post p JOIN deleted d ON p.PostID = d.PostID;
	END;
	GO

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

	-- ==========================================
	-- 7. DỮ LIỆU MẪU (MOCK DATA) ĐÃ CẬP NHẬT
	-- ==========================================

	INSERT INTO Account (Username, Email, Password, isAdmin) VALUES 
	('admin', 'admin@gomet.com', 'admin123', 1),
	('user1', 'user1@gmail.com', '123456', 0),
	('user2', 'user2@gmail.com', '123456', 0);

	INSERT INTO Category (CategoryName) VALUES (N'Món Việt'), (N'Món Âu'), (N'Món Chay');

	-- Dữ liệu Event đầy đủ mô tả, thời gian vote
	INSERT INTO Event (EventName, BannerImage, Description, Rules, Reward, StartAt, EndAt, VoteStartAt, VoteEndAt) VALUES 
	(N'Siêu Đầu Bếp Tháng 3', 'https://images.unsplash.com/photo-1556761175-5973dc0f32e7?q=80', N'Thi nấu ăn dành riêng cho tháng 3', N'- Nộp tối đa 3 bài<br>- Không sao chép', N'Huy hiệu Siêu Đầu Bếp', '2026-03-01 00:00:00', '2026-03-20 23:59:59', '2026-03-15 00:00:00', '2026-03-25 23:59:59');

	INSERT INTO Post (AccountID, CategoryID, Title, Description, Ingredients, isApproved, isActive) VALUES 
	(2, 1, N'Phở Bò Nam Định', N'Nấu chuẩn vị gia truyền', N'Xương bò, bánh phở, thịt bò', 1, 1),
	(3, 1, N'Bún Chả Hà Nội', N'Ngon như ngoài hàng', N'Thịt nạc vai, bún, đu đủ', 1, 1);

	INSERT INTO EventPosts (EventID, PostID) VALUES (1, 1), (1, 2);
	GO

	SELECT * FROM Post;

	SELECT * FROM event;

	SELECT * FROM Votes;

	SELECT * FROM Likes;

	SELECT * FROM account;

	SELECT * FROM Subscription;

	SELECT * FROM PaymentTransaction;

	SELECT * FROM Ticket;

	SELECT * FROM Comment;

