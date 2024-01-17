package com.example.solox3_dit2b21;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.SubMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
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
        this.pages = pages;  // Update the list of pages
        ReadingPageAdapter adapter = new ReadingPageAdapter(this, pages);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);  // Reset to the first page of the new content
        updatePagination();  // Update pagination controls
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
                    List<Chapter> chapters = new ArrayList<>();
                    for (DataSnapshot bookChapterSnapshot : dataSnapshot.getChildren()) {
                        String fetchedBookId = bookChapterSnapshot.child("bookId").getValue(String.class);
                        if (bookId.equals(fetchedBookId)) {
                            for (DataSnapshot chapterSnapshot : bookChapterSnapshot.child("chapters").getChildren()) {
                                Chapter chapter = chapterSnapshot.getValue(Chapter.class);
                                if (chapter != null) {
                                    chapters.add(chapter); // Add chapter to list
                                    if (chapter.getSubChapters() != null && !chapter.getSubChapters().isEmpty()) {
                                        // Assuming you want to display the first subchapter initially
                                        SubChapter firstSubChapter = chapter.getSubChapters().values().iterator().next();

                                        // Update the UI with the first subchapter content
                                        TextView chapterTitle = findViewById(R.id.ChapterTitle);
                                        chapterTitle.setText(firstSubChapter.getTitle());

                                        // Split the first subchapter content into pages
                                        List<String> pages = splitChapterIntoPages(firstSubChapter.getChapterContent());
                                        setupViewPager(pages);
                                    }
                                }
                            }
                            populateDrawerMenu(chapters); // Populate the drawer menu with the chapters
                            return; // Exit loop after processing the found book chapter
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

    private void populateDrawerMenu(List<Chapter> chapters) {
        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        menu.clear(); // Clear any existing items

        for (Chapter chapter : chapters) {
            SubMenu chapterMenu = menu.addSubMenu(chapter.getTitle());
            for (Map.Entry<String, SubChapter> entry : chapter.getSubChapters().entrySet()) {
                SubChapter subChapter = entry.getValue();
                chapterMenu.add(Menu.NONE, Menu.NONE, Menu.NONE, subChapter.getTitle()).setOnMenuItemClickListener(item -> {
                    // Handle subchapter selection here
                    navigateToSubChapter(subChapter);
                    return true;
                });
            }
        }
    }

    private void navigateToSubChapter(SubChapter subChapter) {
        if (subChapter != null) {
            // Update the title
            TextView chapterTitle = findViewById(R.id.ChapterTitle);
            chapterTitle.setText(subChapter.getTitle());

            // Split the subchapter content into pages and update the ViewPager
            List<String> pages = splitChapterIntoPages(subChapter.getChapterContent());
            setupViewPager(pages);  // This method should reset and update the ViewPager

            // Optionally, close the drawer if open
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.backButton) {
            finish();
        } else if (v.getId()==R.id.menuButton) {
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.openDrawer(GravityCompat.START);
        }
    }

}
