package com.example.solox3_dit2b21.model;


import java.util.HashMap;
import java.util.Map;

public class User {
    private String userId;
    private String email;
    private String username;
    private String profilePic;
    private String bio;
    private String createdDate;
    private String lastUpdated;

    public User() {
    }

    public User(String userId, String email, String username, String profilePic, String bio, String createdDate, String lastUpdated) {
        this.userId = userId;
        this.email = email;
        this.username = username;
        this.profilePic = profilePic;
        this.bio = bio;
        this.createdDate = createdDate;
        this.lastUpdated = lastUpdated;
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

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("email", email);
        result.put("username", username);
        result.put("profilePic", profilePic);
        result.put("bio", bio);
        result.put("createdDate", createdDate);
        result.put("lastUpdated", lastUpdated);
        return result;
    }
}
