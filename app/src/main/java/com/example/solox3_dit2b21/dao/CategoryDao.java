package com.example.solox3_dit2b21.dao;

import com.example.solox3_dit2b21.model.Category;

public interface CategoryDao {
    void loadBookCategory(String categoryId, DataCallback<Category> callback);
}
