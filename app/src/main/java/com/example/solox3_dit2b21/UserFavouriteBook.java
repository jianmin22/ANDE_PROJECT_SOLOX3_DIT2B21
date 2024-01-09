package com.example.solox3_dit2b21;

public class UserFavouriteBook {
    private String userId;
    private String bookId;

    // Constructor
    public UserFavouriteBook(String userId, String bookId) {
        this.userId = userId;
        this.bookId = bookId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }
}
