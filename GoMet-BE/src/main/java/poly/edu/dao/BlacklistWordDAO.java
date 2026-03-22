package poly.edu.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import poly.edu.entity.BlacklistWord;

public interface BlacklistWordDAO extends JpaRepository<BlacklistWord, Integer> {
}