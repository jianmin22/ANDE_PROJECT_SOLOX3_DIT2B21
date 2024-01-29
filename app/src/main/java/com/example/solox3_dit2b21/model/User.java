package com.example.solox3_dit2b21.model;


public class User {
    private String userId;
    private String email;
    private String username;
    private String profilePic;
    private String description;
    private String createdDate;

    public User() {
    }

    public User(String userId, String email, String username, String profilePic, String description, String createdDate) {
        this.userId = userId;
        this.email = email;
        this.username = username;
        this.profilePic = profilePic;
        this.description = description;
        this.createdDate = createdDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}
