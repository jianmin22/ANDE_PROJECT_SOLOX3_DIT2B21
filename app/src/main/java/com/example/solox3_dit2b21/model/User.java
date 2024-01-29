package com.example.solox3_dit2b21.model;


public class User {
    private String userId;
    private String email;
    private String username;
    private String profilePic;
    private String bio;
    private String createdDate;

    public User() {
    }

    public User(String userId, String email, String username, String profilePic, String bio, String createdDate) {
        this.userId = userId;
        this.email = email;
        this.username = username;
        this.profilePic = profilePic;
        this.bio = bio;
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

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}
