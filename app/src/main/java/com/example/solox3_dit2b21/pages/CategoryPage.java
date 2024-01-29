package com.example.solox3_dit2b21.pages;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.solox3_dit2b21.R;
import com.example.solox3_dit2b21.Utils.LoadImageURL;
import com.example.solox3_dit2b21.dao.CategoryDao;
import com.example.solox3_dit2b21.dao.DataCallback;
import com.example.solox3_dit2b21.daoimpl.FirebaseCategoryDao;
import com.example.solox3_dit2b21.model.Category;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class CategoryPage extends AppCompatActivity {

    private GridLayout categoryGridLayout;
    private BottomNavigationView bottomNavigationView;

    CategoryDao categoryDao = new FirebaseCategoryDao();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_page);

        categoryGridLayout = findViewById(R.id.categoryGridLayout);

        // Load categories from Firebase
        loadCategories();
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_bookshelf) {
                Intent intent = new Intent(CategoryPage.this, Bookshelf.class);
                startActivity(intent);
            } else if (itemId == R.id.navigation_home) {
                Intent intent = new Intent(CategoryPage.this, Home.class);
                startActivity(intent);
            } if (itemId == R.id.navigation_profile) {
                Intent intent = new Intent(CategoryPage.this, Profile.class);
                startActivity(intent);
            }

            return true;
        });

        // To select a default item (e.g., home) when the activity starts
        bottomNavigationView.setSelectedItemId(R.id.navigation_category);
    }

    private void loadCategories() {
        DatabaseReference categoriesRef = FirebaseDatabase.getInstance().getReference("Category");

        categoryDao.loadAllCategories(new DataCallback<List<Category>>() {
            @Override
            public void onDataReceived(List<Category> categories) {
                if(categories!=null){
                    displayCategories(categories);
                }else{
                    Toast.makeText(getApplicationContext(), "Failed to load categories", Toast.LENGTH_LONG).show();
                    finish();
                }

            }

            public void onError(Exception exception) {
                Log.e("Firebase", "Failed to get book details", exception);
            }
        });
    }

    private void displayCategories(List<Category> categories) {
        categoryGridLayout.removeAllViews();

        for (final Category category : categories) {
            View categoryItemView = LayoutInflater.from(this).inflate(R.layout.categoryitemholder, null);

            ImageView categoryImage = categoryItemView.findViewById(R.id.CategoryImage);
            TextView categoryNameTextView = categoryItemView.findViewById(R.id.CategoryNameTextView);

            categoryNameTextView.setText(category.getCategoryName());
            LoadImageURL.loadImageURL(category.getCategoryImageUrl(), categoryImage);
            Log.d(category.getCategoryImageUrl(), "displayCategories: ");
            // Set margins for categoryItemView
            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
            int marginInDp = 10; // Adjust the margin value as needed
            int marginInPixels = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    marginInDp,
                    getResources().getDisplayMetrics()
            );

            layoutParams.setMargins(marginInPixels, marginInPixels, marginInPixels, marginInPixels);
            categoryItemView.setLayoutParams(layoutParams);

            // Add an OnClickListener to handle category item clicks
            categoryItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle category item click here
                    Bundle getData = getIntent().getExtras();
                    String filter = category.getCategoryId();

                    if (getData != null) {
                        String isFilterExist = getData.getString("filter");
                        if (isFilterExist != null) {
                            filter += "," + isFilterExist;
                        }
                    }
                    navigateToFilterResult(filter); // You can pass the category ID or other relevant data
                }
            });



            // Add the categoryItemView to GridLayout
            categoryGridLayout.addView(categoryItemView);
        }
    }
    private void navigateToFilterResult(String categoryId) {
        Intent intent = new Intent(CategoryPage.this, SearchFilterResults.class);
        intent.putExtra("filter", categoryId);
        String searchOrder="2";
        String filterOrder="1";
        Bundle getData = getIntent().getExtras();
        if (getData != null) {
            String search = getData.getString("search");
            String searchOrderPassed = getData.getString("searchOrder");
            if(search != null){
                searchOrder="1";
                intent.putExtra("search", search);
                if(searchOrderPassed.equals("1")){
                    filterOrder="2";
                    searchOrder="1";
                }
            }
        }
        intent.putExtra("searchOrder",searchOrder);
        intent.putExtra("filterOrder", filterOrder);
        startActivity(intent);
    }

}
