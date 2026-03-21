USE DATN_CD;
GO

UPDATE Category SET CategoryName = N'Món Việt' WHERE CategoryID = 1;
UPDATE Category SET CategoryName = N'Món Âu' WHERE CategoryID = 2;
UPDATE Category SET CategoryName = N'Món Chay' WHERE CategoryID = 3;

UPDATE Event SET 
    EventName = N'Siêu Đầu Bếp Tháng 3', 
    Description = N'Thi nấu ăn dành riêng cho tháng 3', 
    Reward = N'Huy hiệu Siêu Đầu Bếp',
    Rules = N'- Nộp tối đa 3 bài<br>- Không sao chép'
WHERE EventID = 1;

UPDATE Post SET 
    Title = N'Phở Bò Nam Định', 
    Description = N'Nấu chuẩn vị gia truyền', 
    Ingredients = N'Xương bò, bánh phở, thịt bò' 
WHERE PostID = 1;

UPDATE Post SET 
    Title = N'Bún Chả Hà Nội', 
    Description = N'Ngon như ngoài hàng', 
    Ingredients = N'Thịt nạc vai, bún, đu đủ' 
WHERE PostID = 2;
GO
