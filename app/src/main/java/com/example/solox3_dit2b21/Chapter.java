package com.example.solox3_dit2b21;

public class Chapter {
    private String title;
    private String chapterId;
    private String bookId;
    private int chapterNumber;
    private String chapterContent;
    private int chapterOrder;
    private int subChapterOrder;
    public Chapter(){

    }
    public Chapter(String title,String chapterId, String bookId, int chapterNumber, String chapterContent,
                   int chapterOrder, int subChapterOrder) {
        this.title=title;
        this.chapterId = chapterId;
        this.bookId = bookId;
        this.chapterNumber = chapterNumber;
        this.chapterContent = chapterContent;
        this.chapterOrder = chapterOrder;
        this.subChapterOrder = subChapterOrder;
    }

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public int getChapterNumber() {
        return chapterNumber;
    }

    public void setChapterNumber(int chapterNumber) {
        this.chapterNumber = chapterNumber;
    }

    public String getChapterContent() {
        return chapterContent;
    }

    public void setChapterContent(String chapterContent) {
        this.chapterContent = chapterContent;
    }

    public int getChapterOrder() {
        return chapterOrder;
    }

    public void setChapterOrder(int chapterOrder) {
        this.chapterOrder = chapterOrder;
    }

    public int getSubChapterOrder() {
        return subChapterOrder;
    }

    public void setSubChapterOrder(int subChapterOrder) {
        this.subChapterOrder = subChapterOrder;
    }
}
