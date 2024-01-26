package com.example.solox3_dit2b21.dao;

import com.example.solox3_dit2b21.model.Book;
import com.example.solox3_dit2b21.model.UserFavouriteBook;

import java.util.List;

public interface UserFavouriteBookDao {
    void loadNumberOfUserFavouriteBook(String bookId, DataCallback<Integer> callback);
    void loadIsUserFavouriteBook(String bookId, String userId, DataCallback<Boolean> callback);
    void deleteUserFavourite(String bookId, String userId, DataStatusCallback callback);
    void insertUserFavourite(UserFavouriteBook userFavouriteBook, DataStatusCallback callback);
    void getUserFavouriteBookIds(String userId, DataCallback<List<String>> callback);
}
