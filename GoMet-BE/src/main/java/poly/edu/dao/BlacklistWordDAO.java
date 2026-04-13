package poly.edu.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import poly.edu.entity.BlacklistWord;

import java.util.List;

public interface BlacklistWordDAO extends JpaRepository<BlacklistWord, Integer> {

    // Lấy nguyên mảng chữ (không cần lấy ID hay CreatedAt) cho nó nhẹ
    @Query("SELECT b.word FROM BlacklistWord b")
    List<String> findAllWords();
}