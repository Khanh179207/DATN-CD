package poly.edu.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "InteractionLog")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InteractionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer logID;

    @Column(name = "PostID")
    private Integer postID;

    @Column(name = "ReferenceID")
    private Integer referenceId;

    private String type;
    private Integer value;
    private LocalDateTime createdAt = LocalDateTime.now();

    public InteractionLog(Integer postID, String type, Integer value) {
        this.postID = postID;
        this.type = type;
        this.value = value;
        this.createdAt = java.time.LocalDateTime.now();
    }

}
