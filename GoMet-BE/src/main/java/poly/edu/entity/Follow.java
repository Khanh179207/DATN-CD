package poly.edu.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "Follow")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer followID;

    @ManyToOne
    @JoinColumn(name = "FollowerID", nullable = false)
    private Account follower;

    @ManyToOne
    @JoinColumn(name = "FolloweeID", nullable = false)
    private Account followee;

    @Column(nullable = false)
    private Integer status;

    @Column(nullable = false)
    private LocalDate followedAt;

    private LocalDate unFollowedAt;
}
