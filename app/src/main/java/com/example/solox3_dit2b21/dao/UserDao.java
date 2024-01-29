package com.example.solox3_dit2b21.dao;

import com.example.solox3_dit2b21.model.User;

import java.util.List;

public interface UserDao {

    void getUser(String userId, DataCallback<User> callback);
    void getUsername(String userId, DataCallback<String> callback);
    void insertUser(User user, DataStatusCallback callback);
    void updateUser(User user, DataStatusCallback callback);
}

