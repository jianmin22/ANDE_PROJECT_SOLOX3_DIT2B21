package com.example.solox3_dit2b21;


public class UserRating {
    private String userRatingId;
    private double rating;
    private String date;

    // Constructor
    public UserRating(String userRatingId, double rating, String date) {
        this.userRatingId = userRatingId;
        this.rating = rating;
        this.date = date;
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
}
