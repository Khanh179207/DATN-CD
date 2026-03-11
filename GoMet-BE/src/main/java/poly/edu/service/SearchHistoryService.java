package poly.edu.service;

import poly.edu.entity.SearchHistory;
import java.util.List;

public interface SearchHistoryService {
    List<SearchHistory> getHistoryByAccountId(Integer accountId);
    void saveKeyword(Integer accountId, String keyword);
    void deleteHistoryById(Integer searchId);
    void clearAllHistory(Integer accountId);
}