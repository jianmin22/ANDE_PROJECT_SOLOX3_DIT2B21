package com.example.solox3_dit2b21.daoimpl;

import com.example.solox3_dit2b21.dao.CategoryDao;
import com.example.solox3_dit2b21.dao.DataCallback;
import com.example.solox3_dit2b21.model.Category;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class FirebaseCategoryDao implements CategoryDao {
    @Override
    public void loadBookCategory(String categoryId, DataCallback<Category> callback) {
        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference().child("Category").child(categoryId);
        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Category category = dataSnapshot.getValue(Category.class);
                    callback.onDataReceived(category);
                } else {
                    callback.onDataReceived(null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    @Override
    public void loadAllCategories(DataCallback<List<Category>> callback) {
        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference().child("Category");
        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<Category> categoryList = new ArrayList<>();
                    for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                        Category category = categorySnapshot.getValue(Category.class);
                        categoryList.add(category);
                    }
                    callback.onDataReceived(categoryList);
                } else {
                    callback.onDataReceived(null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

}
