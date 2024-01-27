package com.example.solox3_dit2b21.dao;

import com.example.solox3_dit2b21.model.Book;
import com.example.solox3_dit2b21.model.BookWithReadingHistory;

import java.util.List;

public interface BookDao {
    void getPopularBooks(DataCallback callback);
    void getLatestBooks(DataCallback callback);
    void getUserBooks(DataCallback callback, String userId, Boolean published);
    void getUserFavouriteBooks(DataCallback callback, String userId);
    void getUserBookIds(DataCallback callback, String userId);

    void loadBookDetailsById(String bookId, DataCallback<Book> callback);
    void fetchSearchAndFilterBooks(String search, String filter, String searchOrder, String filterOrder, DataCallback<List<Book>> callback);
    void insertBook(Book book, DataStatusCallback callback);
    void updateBookDetails(Book book, DataStatusCallback callback);
    void updateBookIsPublished(Book book, DataStatusCallback callback);
    void getTotalUserPublished(DataCallback callback, String userId);

    void getUserReadingHistoryBooks(String userId, DataCallback<List<BookWithReadingHistory>> callback);
}

