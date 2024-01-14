package com.example.solox3_dit2b21;

import java.util.Date;

public class SearchHistory {
    private String searchId;
    private String userId;
    private String search;
    private String lastSearch;

    public SearchHistory(){
        
    }
    public SearchHistory(String searchId,String userId, String search, String lastSearch) {
        this.searchId=searchId;
        this.userId = userId;
        this.search = search;
        this.lastSearch = lastSearch;
    }

    public String getSearchId() {
        return searchId;
    }

    public void setSearchId(String searchId) {
        this.searchId = searchId;
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

