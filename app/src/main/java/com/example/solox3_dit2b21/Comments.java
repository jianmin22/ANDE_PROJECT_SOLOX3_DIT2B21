package com.example.solox3_dit2b21;

import java.util.Date;

public class Comments {
    private String commentsId;
    private String bookId;
    private String commentsText;
    private String userId;
    private String date;

    // Constructor
    public Comments(String commentsId, String bookId, String commentsText, String userId, String date) {
        this.commentsId = commentsId;
        this.bookId = bookId;
        this.commentsText = commentsText;
        this.userId = userId;
        this.date = date;
    }

    public String getCommentsId() {
        return commentsId;
    }

    public void setCommentsId(String commentsId) {
        this.commentsId = commentsId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getCommentsText() {
        return commentsText;
    }

    public void setCommentsText(String commentsText) {
        this.commentsText = commentsText;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
