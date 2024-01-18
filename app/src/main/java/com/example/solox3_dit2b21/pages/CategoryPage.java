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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.solox3_dit2b21.R;
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
                // Navigate to Bookshelf activity
                // Replace CategoryPage.class with the correct Activity class for Bookshelf
                // if (this is not instance of BookshelfActivity) {
                //Intent intent = new Intent(Home.this, BookshelfActivity.class);
                //startActivity(intent);
                // }
            } else if (itemId == R.id.navigation_home) {
                Intent intent = new Intent(CategoryPage.this, Home.class);
                startActivity(intent);
            } else if (itemId == R.id.navigation_category) {

            } else if (itemId == R.id.navigation_profile) {
                // Navigate to Profile activity
                // Replace CategoryPage.class with the correct Activity class for Profile
                // if (this is not instance of ProfileActivity) {
                //Intent intent = new Intent(Home.this, ProfileActivity.class);
                //startActivity(intent);
                // }
            }

            return true;
        });

        // To select a default item (e.g., home) when the activity starts
        bottomNavigationView.setSelectedItemId(R.id.navigation_category);
    }

    private void loadCategories() {
        DatabaseReference categoriesRef = FirebaseDatabase.getInstance().getReference("Category");

        categoriesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Category> categories = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Category category = snapshot.getValue(Category.class);
                    if (category != null) {
                        categories.add(category);
                    }
                }

                displayCategories(categories);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
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
            loadCategoryImage(category.getCategoryImageUrl(), categoryImage);
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
                    navigateToFilterResult(category.getCategoryId()); // You can pass the category ID or other relevant data
                }
            });



            // Add the categoryItemView to GridLayout
            categoryGridLayout.addView(categoryItemView);
        }
    }
    private void navigateToFilterResult(String categoryId) {
        // Implement the logic to navigate to the filter_result page
        // You can use Intent to start the new activity and pass the category ID
        // Example:
         Intent intent = new Intent(CategoryPage.this, SearchFilterResults.class);
         intent.putExtra("CATEGORY_ID", categoryId);
         startActivity(intent);
    }


    private void loadCategoryImage(String imageUrl, ImageView imageView) {
        Glide.with(imageView.getContext())
                .load(imageUrl)
                .into(imageView);
    }
}
