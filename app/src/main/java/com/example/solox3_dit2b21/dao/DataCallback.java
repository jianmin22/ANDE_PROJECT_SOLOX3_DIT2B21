package com.example.solox3_dit2b21.dao;

public interface DataCallback<T> {
    void onDataReceived(T data);
    void onError(Exception exception);
}