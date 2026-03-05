package poly.edu.dto;

import lombok.*;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleDTO {
    private Integer     id;
    private String      name;
    private String      description;
    private Set<String> permissions;
}
