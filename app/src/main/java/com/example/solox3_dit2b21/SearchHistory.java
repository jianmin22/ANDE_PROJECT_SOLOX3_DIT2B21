package com.example.solox3_dit2b21;

import java.util.Date;

public class SearchHistory {
    private String userId;
    private String search;
    private Date lastSearch;

    // Constructor
    public SearchHistory(String userId, String search, Date lastSearch) {
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

    public Date getLastSearch() {
        return lastSearch;
    }

    public void setLastSearch(Date lastSearch) {
        this.lastSearch = lastSearch;
    }
}

