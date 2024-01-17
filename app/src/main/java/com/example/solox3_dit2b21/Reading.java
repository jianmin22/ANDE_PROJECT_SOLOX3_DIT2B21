package com.example.solox3_dit2b21;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.util.Log;
import java.util.ArrayList;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class Reading extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private TextView currentPageNumber;
    private ImageView previousButton;
    private ImageView nextButton;
    private ViewPager2 viewPager;
    private int currentPage = 0; // The current page index
    private List<String> pages;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);
        // Initialize the ViewPager and other views
        viewPager = findViewById(R.id.viewPager);
        currentPageNumber = findViewById(R.id.currentPageNumber);
        previousButton = findViewById(R.id.previousButton);
        nextButton = findViewById(R.id.nextButton);
        // Initialize Firebase
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Chapter");
        // Set up button listeners
        setupButtonListeners();
        // Fetch data from Firebase
        fetchChapterData();
    }
    private void setupButtonListeners() {
        previousButton.setOnClickListener(v -> {
            if (currentPage > 0) {
                currentPage--;
                viewPager.setCurrentItem(currentPage);
            }
        });

        nextButton.setOnClickListener(v -> {
            if (currentPage < pages.size() - 1) {
                currentPage++;
                viewPager.setCurrentItem(currentPage);
            }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                currentPage = position;
                updatePagination();
            }
        });
    }
    private void updatePagination() {
        currentPageNumber.setText(String.valueOf(currentPage + 1)); // +1 because page numbers are usually 1-based

        previousButton.setEnabled(currentPage > 0);
        nextButton.setEnabled(currentPage < pages.size() - 1);
    }
    private void setupViewPager(List<String> pages) {
        this.pages = pages; // Keep a reference to the list of pages
        ReadingPageAdapter adapter = new ReadingPageAdapter(this, pages);
        viewPager.setAdapter(adapter);
        updatePagination(); // Initial pagination update
    }
    private List<String> splitChapterIntoPages(String chapterContent) {
        // Split logic here (e.g., split by every 1000 characters)
        // This is a simple example and may need to be adjusted based on your content
        List<String> pages = new ArrayList<>();
        int pageSize = 800; // Example page size
        for (int i = 0; i < chapterContent.length(); i += pageSize) {
            pages.add(chapterContent.substring(i, Math.min(chapterContent.length(), i + pageSize)));
        }
        return pages;
    }

    private void fetchChapterData() {
        databaseReference.child("chapter1").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Retrieve data from dataSnapshot and update UI
                Chapter chapter = dataSnapshot.getValue(Chapter.class);

                // Log the retrieved data
                if (chapter != null) {
                    Log.d("ReadingActivity", "Chapter Data Retrieved: " +
                            "BookId: " + chapter.getBookId() +
                            ", ChapterContent: " + chapter.getChapterContent() +
                            ", ChapterId: " + chapter.getChapterId() +
                            ", ChapterNumber: " + chapter.getChapterNumber() +
                            ", ChapterOrder: " + chapter.getChapterOrder() +
                            ", SubChapterOrder: " + chapter.getSubChapterOrder());
                } else {
                    Log.e("ReadingActivity", "Chapter Data is null");
                }

                // Update UI elements with chapter data
                TextView chapterTitle = findViewById(R.id.ChapterTitle);
                if (chapter != null) {
                    chapterTitle.setText("Chapter " + chapter.getChapterNumber() + " " + chapter.getTitle());
                } else {
                    chapterTitle.setText("Chapter Data Not Available");
                }
                List<String> pages = splitChapterIntoPages(chapter.getChapterContent());
                setupViewPager(pages);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
                Log.e("ReadingActivity", "Failed to retrieve chapter data: " + databaseError.getMessage());
            }
        });
    }



}