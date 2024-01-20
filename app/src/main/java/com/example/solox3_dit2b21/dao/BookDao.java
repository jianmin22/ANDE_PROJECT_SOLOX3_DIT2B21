package com.example.solox3_dit2b21.dao;

import com.example.solox3_dit2b21.model.Book;

public interface BookDao {
    void getPopularBooks(DataCallback callback);
    void getLatestBooks(DataCallback callback);
    void loadBookDetailsById(String bookId, DataCallback<Book> callback);
}

