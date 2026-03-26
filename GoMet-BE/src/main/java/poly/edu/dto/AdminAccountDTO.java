package poly.edu.dto;

import lombok.Data;

@Data
public class AdminAccountDTO {
    private Integer accountID;
    private String  username;
    private String  email;
    private String  password;
    private String  avatar;
    private Integer isAdmin;
    private Integer isPremium;
    private Integer isActive;
    private Integer point;
    private String  createdAt;
    private String  role;

    // Lưu vết admin khóa
    private String  banReason;
    private String  bannedAt;

    // 🔥 3 BIẾN MỚI THÊM VÀO ĐỂ HIỂN THỊ THỐNG KÊ
    private Integer postCount;
    private Integer followerCount;
    private Integer totalLikes;
}