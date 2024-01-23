package com.example.solox3_dit2b21.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Book {
    private String bookId;
    private String title;
    private String description;
    private String categoryId;
    private String image;
    private String publishedDate;
    private String createdDate;
    private String lastUpdated;
    private String authorId;

    private String isPublished;

    private int numberOfReads;
    public Book() {
    }


    public Book(String bookId, String title, String description, String categoryId, double rating,
                String image, String publishedDate, int numberOfReads, String createdDate, String lastUpdated, String authorId, String isPublished) {
        this.bookId = bookId;
        this.title = title;
        this.description = description;
        this.categoryId = categoryId;
        this.image = image;
        this.publishedDate = publishedDate;
        this.numberOfReads=numberOfReads;
        this.createdDate = createdDate;
        this.lastUpdated = lastUpdated;
        this.authorId=authorId;
        this.isPublished=isPublished;
    }

    public String getIsPublished() {
        return isPublished;
    }

    public void setIsPublished(String isPublished) {
        this.isPublished = isPublished;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

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

    public String getDescription() {
        return description;
    }

    public int getNumberOfReads() {
        return numberOfReads;
    }

    public void setNumberOfReads(int numberOfReads) {
        this.numberOfReads = numberOfReads;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("bookId", bookId);
        result.put("title", title);
        result.put("description", description);
        result.put("categoryId", categoryId);
        result.put("image", image);
        result.put("publishedDate", publishedDate);
        result.put("createdDate", createdDate);
        result.put("lastUpdated", lastUpdated);
        result.put("authorId", authorId);
        result.put("isPublished", isPublished);
        result.put("numberOfReads", numberOfReads);
        return result;
    }
}
