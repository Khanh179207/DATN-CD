package poly.edu.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import poly.edu.entity.AccountRole;

import java.util.List;

public interface AccountRoleDAO extends JpaRepository<AccountRole, AccountRole.PK> {

    List<AccountRole> findByAccountId(Integer accountId);

    @Modifying
    @Transactional
    @Query("DELETE FROM AccountRole ar WHERE ar.accountId = :accountId AND ar.roleId = :roleId")
    void deleteByAccountIdAndRoleId(@Param("accountId") Integer accountId, @Param("roleId") Integer roleId);

    boolean existsByAccountIdAndRoleId(Integer accountId, Integer roleId);
}
