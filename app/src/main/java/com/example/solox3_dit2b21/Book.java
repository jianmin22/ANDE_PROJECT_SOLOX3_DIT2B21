package com.example.solox3_dit2b21;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Book {
    private String bookId;
    private String title;
    private String description;
    private String categoryId;
    private double rating;
    private String image;
    private String publishedDate;
    private String createdDate;
    private String lastUpdated;
    public Book() {
    }


    public Book(String bookId, String title, String description, String categoryId, double rating,
                String image, String publishedDate, String createdDate, String lastUpdated) {
        this.bookId = bookId;
        this.title = title;
        this.description = description;
        this.categoryId = categoryId;
        this.rating = rating;
        this.image = image;
        this.publishedDate = publishedDate;
        this.createdDate = createdDate;
        this.lastUpdated = lastUpdated;
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

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
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
}
