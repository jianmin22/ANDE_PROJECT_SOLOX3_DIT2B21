package com.example.solox3_dit2b21;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class Category_Page extends AppCompatActivity {

    private GridLayout categoryGridLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_page);

        categoryGridLayout = findViewById(R.id.categoryGridLayout);

        // Load categories from Firebase
        loadCategories();
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

        for (Category category : categories) {
            View categoryItemView = LayoutInflater.from(this).inflate(R.layout.categoryitemholder, null);

            ImageView categoryImage = categoryItemView.findViewById(R.id.CategoryImage);
            TextView categoryNameTextView = categoryItemView.findViewById(R.id.CategoryNameTextView);

            categoryNameTextView.setText(category.getCategoryName());
            loadBookImage(category.getCategoryImageUrl(), categoryImage);

            // Set margins for categoryItemView
            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
            int marginInDp = 10; // Adjust the margin value as needed
            int marginInPixels = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    marginInDp,
                    getResources().getDisplayMetrics()
            );

            layoutParams.setMargins(marginInDp, marginInDp, marginInDp, marginInDp);
            categoryItemView.setLayoutParams(layoutParams);

            // Add the categoryItemView to GridLayout
            categoryGridLayout.addView(categoryItemView);
        }
    }


    private void loadBookImage(String imageUrl, ImageView imageView) {
        Glide.with(imageView.getContext())
                .load(imageUrl)
                .into(imageView);
    }
}
