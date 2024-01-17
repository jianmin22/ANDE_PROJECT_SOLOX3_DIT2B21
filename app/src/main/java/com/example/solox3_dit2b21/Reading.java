package com.example.solox3_dit2b21;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Reading extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference databaseReference;
    private TextView currentPageNumber;
    private ImageView previousButton;
    private ImageView nextButton;
    private ViewPager2 viewPager;
    private int currentPage = 0; // The current page index
    private List<String> pages;
    private String bookId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);

        // Retrieve the bookId passed from the previous activity
        bookId = getIntent().getStringExtra("bookId");

        // Initialize the ViewPager and other views
        viewPager = findViewById(R.id.viewPager);
        currentPageNumber = findViewById(R.id.currentPageNumber);
        previousButton = findViewById(R.id.previousButton);
        nextButton = findViewById(R.id.nextButton);

        // Initialize Firebase
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        // Update the reference to point to the specific book's chapters
        // Use the bookId to dynamically refer to the correct book chapters
        if (bookId != null && !bookId.isEmpty()) {
            databaseReference = firebaseDatabase.getReference("Chapter");

        } else {
            Log.e("ReadingActivity", "No bookId provided");
            return; // Exit if no bookId is provided
        }

        // Set up button listeners
        setupButtonListeners();

        // Fetch data from Firebase
        fetchChapters();
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
        currentPageNumber.setText(String.valueOf(currentPage + 1));
        previousButton.setEnabled(currentPage > 0);
        nextButton.setEnabled(currentPage < pages.size() - 1);
    }

    private void setupViewPager(List<String> pages) {
        this.pages = pages;
        ReadingPageAdapter adapter = new ReadingPageAdapter(this, pages);
        viewPager.setAdapter(adapter);
        updatePagination();
    }

    private List<String> splitChapterIntoPages(String chapterContent) {
        List<String> pages = new ArrayList<>();
        int pageSize = 800;
        for (int i = 0; i < chapterContent.length(); i += pageSize) {
            pages.add(chapterContent.substring(i, Math.min(chapterContent.length(), i + pageSize)));
        }
        return pages;
    }

    private void fetchChapters() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot bookChapterSnapshot : dataSnapshot.getChildren()) {
                        String fetchedBookId = bookChapterSnapshot.child("bookId").getValue(String.class);
                        if (bookId.equals(fetchedBookId)) {
                            // Found the correct book chapter entry
                            for (DataSnapshot chapterSnapshot : bookChapterSnapshot.child("chapters").getChildren()) {
                                Chapter chapter = chapterSnapshot.getValue(Chapter.class);
                                if (chapter != null && chapter.getSubChapters() != null) {
                                    // Process the subchapters of the first chapter
                                    Map<String, SubChapter> subChapters = chapter.getSubChapters();
                                    if (subChapters.size() > 0) {
                                        Map.Entry<String, SubChapter> firstEntry = subChapters.entrySet().iterator().next();
                                        SubChapter firstSubChapter = firstEntry.getValue();

                                        // Update the UI with the first subchapter content
                                        TextView chapterTitle = findViewById(R.id.ChapterTitle);
                                        chapterTitle.setText(firstSubChapter.getTitle());

                                        // Split the first subchapter content into pages
                                        List<String> pages = splitChapterIntoPages(firstSubChapter.getChapterContent());
                                        setupViewPager(pages);
                                    }
                                    return; // Exit loop after processing the found book chapter
                                }
                            }
                        }
                    }
                    Log.e("ReadingActivity", "No matching bookId found in chapters");
                } else {
                    Log.e("ReadingActivity", "No chapters available");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("ReadingActivity", "Failed to retrieve chapter data: " + databaseError.getMessage());
            }
        });
    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.backButton) {
            finish();
        }
    }

}
