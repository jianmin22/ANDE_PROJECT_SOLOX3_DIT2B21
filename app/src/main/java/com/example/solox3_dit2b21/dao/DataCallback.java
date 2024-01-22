package com.example.solox3_dit2b21.dao;

public interface DataCallback<T> {
    String onDataReceived(T data);
    void onError(Exception exception);
}