package poly.edu.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "account_roles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(AccountRole.PK.class)
public class AccountRole {

    @Id
    @Column(name = "account_id")
    private Integer accountId;

    @Id
    @Column(name = "role_id")
    private Integer roleId;

    @Column(name = "assigned_by")
    private Integer assignedBy;

    @Column(name = "assigned_at", nullable = false)
    @Builder.Default
    private Instant assignedAt = Instant.now();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PK implements Serializable {
        private Integer accountId;
        private Integer roleId;
    }
}
