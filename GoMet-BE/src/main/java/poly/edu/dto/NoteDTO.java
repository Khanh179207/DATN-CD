package poly.edu.dto;

import lombok.Data;

@Data
public class NoteDTO {
    private Long userId;
    private Long postId;
    private String content;
}