package poly.edu.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "Post")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PostID") // 🔥 Bắt buộc để tránh lệch pha
    private Integer postID;

    @ManyToOne
    @JoinColumn(name = "AccountID", nullable = false)
    private Account account;

    @ManyToOne
    @JoinColumn(name = "CategoryID", nullable = false)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "EventID")
    private Event event;

    @Column(name = "Title", nullable = false)
    private String title;

    @Column(name = "Description", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(name = "Ingredients", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String ingredients;

    @Column(name = "Media")
    private String media;

    @Column(name = "Video")
    private String video;

    @Column(name = "Level", nullable = false)
    private Integer level;

    @Column(name = "CookingTime", nullable = false)
    private Integer cookingTime;

    @Column(name = "Views", nullable = false)
    private Integer views;

    @Column(name = "isActive", nullable = false)
    private Integer isActive;

    @Column(name = "isApproved", nullable = false)
    private Integer isApproved;

    // Trong Post.java
    @Column(name = "CreatedAt", nullable = false)
    private java.time.LocalDateTime createdAt; // Đổi từ LocalDate sang LocalDateTime

    @Column(name = "LikeCount")
    @Builder.Default // 🔥 Thêm cái này để hết báo Warning lúc Build Maven
    private Integer likeCount = 0;

    // Relationships
    @OneToMany(mappedBy = "post")
    @JsonIgnore // 🔥 Nên có để tránh loop JSON khi gọi từ Comment
    private List<Comment> comments;

    // ❌ ĐÃ XÓA KHÚC NÀY ❌
    // @OneToMany(mappedBy = "post")
    // private List<Rating> ratings;

    @OneToMany(mappedBy = "post")
    @JsonIgnore // 🔥 Thêm JsonIgnore để tối ưu hiệu năng API
    private List<Favorite> favorites;

    @OneToMany(mappedBy = "post")
    @JsonIgnore
    private List<History> histories;

    @OneToMany(mappedBy = "post")
    private List<CookingSteps> cookingSteps;

    @OneToMany(mappedBy = "post")
    @JsonIgnore
    private List<Notification> notifications;

    @OneToMany(mappedBy = "targetPost")
    @JsonIgnore
    private List<Ticket> tickets;
}