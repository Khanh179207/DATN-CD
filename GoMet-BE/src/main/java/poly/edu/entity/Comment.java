package poly.edu.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

@Entity
@Table(name = "Comment")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CommentID") // 🔥 Chốt tên cột PascalCase cho khớp 100% với SQL
    private Integer commentID;

    @ManyToOne
    @JoinColumn(name = "AccountID", nullable = false)
    @JsonIgnore // 🔥 Ngăn vòng lặp vô tận khi parse JSON
    private Account account;

    @ManyToOne
    @JoinColumn(name = "PostID", nullable = false)
    @JsonIgnore // 🔥 Ngăn vòng lặp vô tận khi parse JSON
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cmtid")
    @JsonIgnore // 🔥 CỰC KỲ QUAN TRỌNG: Tránh lỗi đệ quy StackOverflow khi 1 comment có nhiều reply lồng nhau
    private Comment parentComment;

    @Column(name = "Rating")
    private Integer rating;

    @Column(name = "Content", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String content;

    @Column(name = "Attachments", columnDefinition = "NVARCHAR(MAX)")
    @Convert(converter = poly.edu.util.StringListConverter.class)
    @Builder.Default
    private List<String> attachments = new ArrayList<>();

    @Column(name = "CreatedAt", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    // --- TƯƠNG TÁC ---
    @Column(name = "Likes")
    @Builder.Default
    private Integer likes = 0; // Con số để hiện trên UI

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CommentLike> commentLikes = new ArrayList<>(); // Danh sách thực thể Like

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Comment> replies = new ArrayList<>();

    @Column(name = "IsActive")
    private Integer isActive = 1; // 1: Hoạt động, 0: Đã ẩn
}