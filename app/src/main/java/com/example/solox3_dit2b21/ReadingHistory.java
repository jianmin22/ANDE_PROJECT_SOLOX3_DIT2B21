package com.example.solox3_dit2b21;


public class ReadingHistory {
    private String userId;
    private String bookId;
    private String chapterId;
    private String lastRead;

    // Constructor
    public ReadingHistory(String userId, String bookId, String chapterId, String lastRead) {
        this.userId = userId;
        this.bookId = bookId;
        this.chapterId = chapterId;
        this.lastRead = lastRead;
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

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    public String getLastRead() {
        return lastRead;
    }

    public void setLastRead(String lastRead) {
        this.lastRead = lastRead;
    }
}
