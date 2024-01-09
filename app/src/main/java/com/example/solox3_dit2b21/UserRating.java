package com.example.solox3_dit2b21;

import java.util.Date;

public class UserRating {
    private String userRatingId;
    private double rating;
    private Date date;

    // Constructor
    public UserRating(String userRatingId, double rating, Date date) {
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
