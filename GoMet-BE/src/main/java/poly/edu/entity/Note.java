package poly.edu.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Note")
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NoteID")
    private Integer noteId;

    @Column(name = "AccountID")
    private Integer accountId;

    @Column(name = "PostID")
    private Integer postId;

    @Column(name = "Content")
    private String content;

    @Column(name = "CreatedAt")
    private LocalDate createdAt;

    @Column(name = "DeletedAt")
    private LocalDate deletedAt;

    @Column(name = "isActive")
    private Integer isActive;
}