package com.example.solox3_dit2b21.model;

public class BookWithReadingHistory {
    private Book book;
    private int lastReadChapterOrder;
    private int lastReadSubChapterOrder;

    // Constructor, Getters and Setters
    public BookWithReadingHistory(Book book, int lastReadChapterOrder, int lastReadSubChapterOrder) {
        this.book = book;
        this.lastReadChapterOrder = lastReadChapterOrder;
        this.lastReadSubChapterOrder = lastReadSubChapterOrder;
    }

    public Book getBook() {
        return book;
    }

    public int getLastReadChapterOrder() {
        return lastReadChapterOrder;
    }

    public int getLastReadSubChapterOrder() {
        return lastReadSubChapterOrder;
    }


}
