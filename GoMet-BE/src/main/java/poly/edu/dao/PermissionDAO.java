package poly.edu.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import poly.edu.entity.Permission;

import java.util.Set;

public interface PermissionDAO extends JpaRepository<Permission, Integer> {

    @Query(value = """
        SELECT DISTINCT p.code
        FROM permissions p
        JOIN role_permissions rp ON rp.permission_id = p.id
        JOIN account_roles ar     ON ar.role_id = rp.role_id
        WHERE ar.account_id = :accountId
        """, nativeQuery = true)
    Set<String> findPermissionCodesForAccount(@Param("accountId") Integer accountId);
}
