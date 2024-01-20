package com.example.solox3_dit2b21.dao;

public interface UserFavouriteBookDao {
    void loadNumberOfUserFavouriteBook(String bookId, DataCallback<Integer> callback);
}
