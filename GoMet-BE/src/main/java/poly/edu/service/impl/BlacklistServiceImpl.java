package poly.edu.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import poly.edu.dao.BlacklistWordDAO;
import poly.edu.entity.BlacklistWord;
import poly.edu.service.BlacklistService;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlacklistServiceImpl implements BlacklistService {

    private final BlacklistWordDAO blacklistWordDAO;

    // 🔥 CACHE LƯU TRÊN RAM (Dùng Set để tìm kiếm cực nhanh)
    private Set<String> cachedBadWords = new HashSet<>();

    // Chạy ngay khi Server vừa khởi động xong
    @PostConstruct
    public void init() {
        refreshCache();
    }

    // Nạp lại dữ liệu từ DB lên RAM
    @Override
    public void refreshCache() {
        cachedBadWords = blacklistWordDAO.findAllWords()
                .stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }

    // Hàm quét nội dung siêu tốc trên RAM
    @Override
    public boolean containsBadWord(String content) {
        if (content == null || content.trim().isEmpty()) {
            return false;
        }

        String lowerContent = content.toLowerCase();

        for (String badWord : cachedBadWords) {
            if (lowerContent.contains(badWord)) {
                return true; // Dính 1 từ là chốt đơn luôn
            }
        }
        return false;
    }

    // ==========================================
    // CÁC HÀM DÀNH CHO TRANG QUẢN TRỊ (ADMIN)
    // ==========================================
    @Override
    public List<BlacklistWord> getAllWords() {
        return blacklistWordDAO.findAll();
    }

    @Override
    public BlacklistWord addWord(String word) {
        BlacklistWord newWord = BlacklistWord.builder()
                .word(word.toLowerCase().trim())
                .createdAt(LocalDateTime.now())
                .build();

        BlacklistWord saved = blacklistWordDAO.save(newWord);
        refreshCache(); // 🔥 Admin thêm từ mới -> Nạp lại Cache ngay
        return saved;
    }

    @Override
    public void deleteWord(Integer id) {
        blacklistWordDAO.deleteById(id);
        refreshCache(); // 🔥 Admin xóa từ -> Cập nhật lại Cache ngay
    }
}