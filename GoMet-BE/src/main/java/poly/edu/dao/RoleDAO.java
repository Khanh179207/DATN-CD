package poly.edu.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import poly.edu.entity.Role;

import java.util.List;
import java.util.Optional;

public interface RoleDAO extends JpaRepository<Role, Integer> {

    Optional<Role> findByName(String name);

    @Query("SELECT DISTINCT r FROM Role r LEFT JOIN FETCH r.permissions WHERE r.id IN :ids")
    List<Role> findAllWithPermissions(@Param("ids") List<Integer> ids);
}
