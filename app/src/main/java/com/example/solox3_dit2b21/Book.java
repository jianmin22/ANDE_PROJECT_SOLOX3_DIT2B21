package com.example.solox3_dit2b21;

import java.util.Date;

public class Book {
    private String bookId;
    private String title;
    private String description;
    private String categoryId;
    private double rating;
    private String image;
    private Date publishedDate;
    private Date createdDate;
    private Date lastUpdated;

    public Book(String bookId, String title, String description, String categoryId, double rating,
                String image, Date publishedDate, Date createdDate, Date lastUpdated) {
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

    public Date getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(Date publishedDate) {
        this.publishedDate = publishedDate;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
