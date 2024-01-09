package com.example.solox3_dit2b21;

public class Chapter {
    private String chapterId;
    private String bookId;
    private int chapterNumber;
    private String chapterContent;
    private int chapterOrder;
    private int subChapterOrder;

    public Chapter(String chapterId, String bookId, int chapterNumber, String chapterContent,
                   int chapterOrder, int subChapterOrder) {
        this.chapterId = chapterId;
        this.bookId = bookId;
        this.chapterNumber = chapterNumber;
        this.chapterContent = chapterContent;
        this.chapterOrder = chapterOrder;
        this.subChapterOrder = subChapterOrder;
    }


}
