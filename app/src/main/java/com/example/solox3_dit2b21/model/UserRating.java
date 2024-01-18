package com.example.solox3_dit2b21.model;


public class UserRating {
    private String userRatingId;
    private double rating;
    private String date;

    private String bookId;

    private String userId;
    public UserRating() {
    }

    public UserRating(String userRatingId, double rating, String date, String bookId, String userId) {
        this.userRatingId = userRatingId;
        this.rating = rating;
        this.date = date;
        this.bookId=bookId;
        this.userId=userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserRatingId() {
        return userRatingId;
    }

    public void setUserRatingId(String userRatingId) {
        this.userRatingId = userRatingId;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }
}
