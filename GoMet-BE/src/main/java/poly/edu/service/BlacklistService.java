package poly.edu.service;

import poly.edu.entity.BlacklistWord;
import java.util.List;

public interface BlacklistService {
    // Check từ cấm
    boolean containsBadWord(String content);

    // Dành cho Admin
    List<BlacklistWord> getAllWords();
    BlacklistWord addWord(String word);
    void deleteWord(Integer id);

    // Cập nhật lại RAM
    void refreshCache();
}