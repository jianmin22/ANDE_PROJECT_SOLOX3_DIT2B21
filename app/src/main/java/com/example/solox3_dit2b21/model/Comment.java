package com.example.solox3_dit2b21.model;

public class Comment {
    private String commentId;
    private String bookId;
    private String commentText;
    private String userId;
    private String date;
    public Comment(){

    }
    public Comment(String commentId, String bookId, String commentText, String userId, String date) {
        this.commentId = commentId;
        this.bookId = bookId;
        this.commentText = commentText;
        this.userId = userId;
        this.date = date;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentsId) {
        this.commentId = commentsId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentsText) {
        this.commentText = commentsText;
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
