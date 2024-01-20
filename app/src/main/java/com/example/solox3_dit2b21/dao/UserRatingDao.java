package com.example.solox3_dit2b21.dao;

public interface UserRatingDao {
    void loadUserRatingForBook(String bookId, DataCallback<Double> callback);
}
