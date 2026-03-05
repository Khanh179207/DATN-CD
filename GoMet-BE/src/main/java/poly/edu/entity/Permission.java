package poly.edu.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "permissions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 80)
    private String code;

    @Column(length = 255)
    private String description;

    // ─── Well-known codes ─────────────────────────────────────────────────────
    public static final String USER_READ          = "USER_READ";
    public static final String USER_WRITE         = "USER_WRITE";
    public static final String USER_BAN           = "USER_BAN";
    public static final String USER_BAN_PERMANENT = "USER_BAN_PERMANENT";
    public static final String POST_MODERATE      = "POST_MODERATE";
    public static final String REPORT_HANDLE      = "REPORT_HANDLE";
    public static final String EMAIL_SEND         = "EMAIL_SEND";
    public static final String AUDIT_READ         = "AUDIT_READ";
    public static final String ROLE_MANAGE        = "ROLE_MANAGE";
    public static final String SETTINGS_WRITE     = "SETTINGS_WRITE";
    public static final String STATS_VIEW         = "STATS_VIEW";
}
