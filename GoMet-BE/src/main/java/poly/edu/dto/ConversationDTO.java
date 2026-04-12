package poly.edu.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class ConversationDTO {
    private Integer id;          // ConversationID
    private Integer partnerID;
    private String name;        // Tên của đối phương
    private String avatar;      // Ảnh của đối phương
    private String lastMessage; // Nội dung tin nhắn cuối
    private String time;        // Thời gian nhắn
    private boolean read;       // Trạng thái đã đọc
    private boolean online;     // Trạng thái online
    private boolean following;
    private boolean followed;
}