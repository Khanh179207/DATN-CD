package poly.edu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder; // QUAN TRỌNG: Phải có cái này
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder // THIẾU CÁI NÀY LÀ LỖI .builder() NGAY SẾP ƠI
@NoArgsConstructor
@AllArgsConstructor
public class CommentLikeResponse {
    private boolean success;
    private boolean isLiked;
    private String message;
}