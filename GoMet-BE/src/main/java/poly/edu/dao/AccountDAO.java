package poly.edu.dao;

import poly.edu.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountDAO extends JpaRepository<Account, Integer> {

    Optional<Account> findByUsername(String username);

    Optional<Account> findByUsernameIgnoreCase(String username);

    Optional<Account> findByEmail(String email);

    List<Account> findByIsAdmin(Integer isAdmin);

    List<Account> findByUsernameContainingIgnoreCase(String keyword);
}
