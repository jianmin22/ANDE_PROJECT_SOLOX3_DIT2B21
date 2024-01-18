package com.example.solox3_dit2b21.model;

import java.util.Map;

public class Chapter {
    private String bookId;
    private String title;
    private int chapterOrder;
    private Map<String, SubChapter> subChapters;

    // Default constructor is needed for Firebase deserialization
    public Chapter() {
    }

    // Getters and setters
    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getChapterOrder() {
        return chapterOrder;
    }

    public void setChapterOrder(int chapterOrder) {
        this.chapterOrder = chapterOrder;
    }

    public Map<String, SubChapter> getSubChapters() {
        return subChapters;
    }

    public void setSubChapters(Map<String, SubChapter> subChapters) {
        this.subChapters = subChapters;
    }
}
