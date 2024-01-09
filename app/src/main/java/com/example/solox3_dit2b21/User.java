package com.example.solox3_dit2b21;

import java.util.Date;

public class User {
    private String userId;
    private String email;
    private String password;
    private int publishedNumber;
    private int commentNumber;
    private int averageRating;
    private String image;
    private Date createdDate;

    public User(String userId, String email, String password, int publishedNumber,
                int commentNumber, int averageRating, String image, Date createdDate) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.publishedNumber = publishedNumber;
        this.commentNumber = commentNumber;
        this.averageRating = averageRating;
        this.image = image;
        this.createdDate = createdDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPublishedNumber() {
        return publishedNumber;
    }

    public void setPublishedNumber(int publishedNumber) {
        this.publishedNumber = publishedNumber;
    }

    public int getCommentNumber() {
        return commentNumber;
    }

    public void setCommentNumber(int commentNumber) {
        this.commentNumber = commentNumber;
    }

    public int getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(int averageRating) {
        this.averageRating = averageRating;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}
