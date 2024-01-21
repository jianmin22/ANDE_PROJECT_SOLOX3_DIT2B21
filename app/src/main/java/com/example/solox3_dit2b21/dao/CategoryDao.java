package com.example.solox3_dit2b21.dao;

import com.example.solox3_dit2b21.model.Category;

import java.util.List;

public interface CategoryDao {
    void loadBookCategory(String categoryId, DataCallback<Category> callback);
    void loadAllCategories(DataCallback<List<Category>> callback);
}
