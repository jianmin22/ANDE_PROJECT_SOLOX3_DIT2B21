package com.example.solox3_dit2b21;

import java.util.Date;

public class SearchHistory {
    private String userId;
    private String search;
    private String lastSearch;

    // Constructor
    public SearchHistory(String userId, String search, String lastSearch) {
        this.userId = userId;
        this.search = search;
        this.lastSearch = lastSearch;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getLastSearch() {
        return lastSearch;
    }

    public void setLastSearch(String lastSearch) {
        this.lastSearch = lastSearch;
    }
}

