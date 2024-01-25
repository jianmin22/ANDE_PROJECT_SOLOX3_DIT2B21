package com.example.solox3_dit2b21.dao;

import com.example.solox3_dit2b21.model.SearchHistory;

import java.util.List;

public interface SearchHistoryDao {
    void getUserSearchHistory(String userId, DataCallback<List<SearchHistory>> callback);
    void insertOrUpdateSearchHistory(String userId, String search, DataStatusCallback callback);
    void removeSearchHistory(String searchId, DataStatusCallback callback);
}
