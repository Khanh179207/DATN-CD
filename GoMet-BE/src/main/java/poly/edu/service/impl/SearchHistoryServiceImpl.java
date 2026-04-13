package poly.edu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poly.edu.dao.AccountDAO;
import poly.edu.dao.SearchHistoryDAO;
import poly.edu.entity.Account;
import poly.edu.entity.SearchHistory;
import poly.edu.service.SearchHistoryService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchHistoryServiceImpl implements SearchHistoryService {

    private final SearchHistoryDAO searchHistoryDAO;
    private final AccountDAO accountDAO;

    @Override
    public List<SearchHistory> getHistoryByAccountId(Integer accountId) {
        // Trả về tối đa 5 kết quả mới nhất
        return searchHistoryDAO.findTop5ByAccount_AccountIDOrderBySearchedAtDesc(accountId);
    }

    @Override
    @Transactional
    public void saveKeyword(Integer accountId, String keyword) {
        String trimmedKeyword = keyword.trim();
        if (trimmedKeyword.isEmpty()) return;

        // Tìm kiếm từ khóa đã tồn tại
        SearchHistory history = searchHistoryDAO
                .findByAccount_AccountIDAndKeyword(accountId, trimmedKeyword)
                .orElseGet(() -> {
                    // Tạo mới nếu chưa có
                    SearchHistory newHistory = new SearchHistory();
                    Account account = accountDAO.findById(accountId)
                            .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));
                    newHistory.setAccount(account);
                    newHistory.setKeyword(trimmedKeyword);
                    return newHistory;
                });

        // Cập nhật thời gian hiện tại
        history.setSearchedAt(LocalDateTime.now());
        searchHistoryDAO.save(history);

        // Sau khi lưu, kiểm tra tổng số bản ghi của account này
        long count = searchHistoryDAO.countByAccount_AccountID(accountId);
        if (count > 5) {
            // Xóa bản ghi cũ nhất
            searchHistoryDAO.deleteOldestByAccountId(accountId);
        }
    }

    @Override
    @Transactional
    public void deleteHistoryById(Integer searchId) {
        searchHistoryDAO.deleteById(searchId);
    }

    @Override
    @Transactional
    public void clearAllHistory(Integer accountId) {
        searchHistoryDAO.deleteByAccount_AccountID(accountId);
    }
}