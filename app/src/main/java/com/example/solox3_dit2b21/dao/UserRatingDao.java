package com.example.solox3_dit2b21.dao;

import com.example.solox3_dit2b21.model.UserRating;

public interface UserRatingDao {
    void loadUserRatingForBook(String bookId, DataCallback<Double> callback);
    void deleteUserRating(String bookId, String userId, DataStatusCallback callback);
    void updateUserRating(String bookId, String userId, double newRating, DataStatusCallback callback);
    void insertUserRating(UserRating userRating, DataStatusCallback callback);
}
