package com.example.solox3_dit2b21.model;

public class SubChapter {
    private String title;
    private String chapterContent;
    private int subChapterOrder;

    // Default constructor is needed for Firebase deserialization
    public SubChapter() {
    }
    public SubChapter(String title, int order, String content) {
        this.title = title;
        this.subChapterOrder = order;
        this.chapterContent = content;
    }
    // Getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getChapterContent() {
        return chapterContent;
    }

    public void setChapterContent(String chapterContent) {
        this.chapterContent = chapterContent;
    }

    public int getSubChapterOrder() {
        return subChapterOrder;
    }

    public void setSubChapterOrder(int subChapterOrder) {
        this.subChapterOrder = subChapterOrder;
    }
}
