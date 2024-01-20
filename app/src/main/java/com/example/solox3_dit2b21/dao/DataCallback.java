package com.example.solox3_dit2b21.dao;

import com.example.solox3_dit2b21.model.Book;

import java.util.List;

public interface DataCallback<T> {
    void onDataReceived(T data);
    void onError(Exception exception);
}