package com.example.solox3_dit2b21.dao;

import com.example.solox3_dit2b21.model.Book;

import java.util.List;

public interface BookDao {
    void getPopularBooks(DataCallback callback);
    void getLatestBooks(DataCallback callback);
    void loadBookDetailsById(String bookId, DataCallback<Book> callback);
    void fetchAndFilterBooks(String search, String filter, String searchOrder, String filterOrder, DataCallback<List<Book>> callback);
}

