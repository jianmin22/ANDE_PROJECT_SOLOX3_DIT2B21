package com.example.solox3_dit2b21.pages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.SubMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.solox3_dit2b21.R;
import com.example.solox3_dit2b21.Utils.AuthUtils;
import com.example.solox3_dit2b21.Utils.CurrentDateUtils;
import com.example.solox3_dit2b21.dao.ChapterDao;
import com.example.solox3_dit2b21.dao.DataCallback;
import com.example.solox3_dit2b21.dao.ReadingHistoryDao;
import com.example.solox3_dit2b21.daoimpl.FirebaseChapterDao;
import com.example.solox3_dit2b21.daoimpl.FirebaseReadingHistoryDao;
import com.example.solox3_dit2b21.model.SubChapter;
import com.example.solox3_dit2b21.model.Chapter;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Reading extends AppCompatActivity implements View.OnClickListener {

    private TextView currentPageNumber;
    private ImageView previousButton;
    private ImageView nextButton;
    private ViewPager2 viewPager;
    private int currentPage = 0; // The current page index
    private List<String> pages;
    private String bookId;
    private ChapterDao chapterDao;
    private Chapter currentChapter;

    private SubChapter currentSubChapter;
    private ReadingHistoryDao readingHistoryDao;
    private String getCurrentDateTime;
    FirebaseAuth auth;
    FirebaseUser user;
    private String userId;
    protected void onStart() {
        super.onStart();
        AuthUtils.redirectToLoginIfNotAuthenticated(this);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userId = user.getUid();
        chapterDao = new FirebaseChapterDao();
        readingHistoryDao = new FirebaseReadingHistoryDao();
        CurrentDateUtils currentDateUtil = new CurrentDateUtils();
        getCurrentDateTime = currentDateUtil.getCurrentDateTime();
        // Retrieve the bookId passed from the previous activity
        Intent intent = getIntent();
        bookId = intent.getStringExtra("bookId");
        int lastReadChapterOrder = intent.getIntExtra("lastReadChapterOrder", -1);
        int lastReadSubChapterOrder = intent.getIntExtra("lastReadSubChapterOrder", -1);
        Log.d("lastReadChapterOrder: ", String.valueOf(lastReadChapterOrder));
        Log.d("lastReadSubChapterOrder: ", String.valueOf(lastReadSubChapterOrder));
        // Initialize the ViewPager and other views
        viewPager = findViewById(R.id.viewPager);
        currentPageNumber = findViewById(R.id.currentPageNumber);
        previousButton = findViewById(R.id.previousButton);
        nextButton = findViewById(R.id.nextButton);

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
        if (bookId != null && !bookId.isEmpty()) {
            chapterDao.fetchChapters(bookId, new DataCallback<List<Chapter>>() {
                @Override
                public void onDataReceived(List<Chapter> chapters) {
                    if (!chapters.isEmpty()) {
                        currentChapter = chapters.get(0); // Set the currentChapter as the first chapter

                        if (currentChapter.getSubChapters() != null && !currentChapter.getSubChapters().isEmpty()) {
                            SubChapter firstSubChapter = currentChapter.getSubChapters().values().iterator().next();
                            TextView chapterTitle = findViewById(R.id.ChapterTitle);
                            chapterTitle.setText(firstSubChapter.getTitle());
                            List<String> pages = splitChapterIntoPages(firstSubChapter.getChapterContent());
                            setupViewPager(pages);
                        }
                        populateDrawerMenu(chapters);
                    } else {
                        Log.e("ReadingActivity", "No matching bookId found in chapters");
                    }
                }

                @Override
                public void onError(Exception exception) {
                    Log.e("ReadingActivity", "Failed to retrieve chapter data", exception);
                }
            });
        } else {
            Log.e("ReadingActivity", "No bookId provided");
        }
    }



    private void populateDrawerMenu(List<Chapter> chapters) {
        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        menu.clear(); // Clear any existing items

        for (Chapter chapter : chapters) {
            // Check if subChapters is null or empty and initialize if necessary
            if (chapter.getSubChapters() == null) {
                chapter.setSubChapters(new HashMap<>()); // Initialize with empty Map if null
            }

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
            currentSubChapter = subChapter; // Set the currentSubChapter

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

    private void updateReadingHistory() {
        if (currentChapter != null && currentSubChapter != null) {
            readingHistoryDao.updateOrCreateReadingHistory(userId, bookId, currentChapter, currentSubChapter, getCurrentDateTime, new DataCallback<Boolean>() {
                @Override
                public void onDataReceived(Boolean result) {
                    if (result) {
                        Log.d("ReadingHistory", "Reading history updated successfully.");
                    }
                }

                @Override
                public void onError(Exception exception) {
                    Log.e("ReadingHistory", "Failed to update reading history.", exception);
                }
            });
        }
    }



    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.backButton) {
            updateReadingHistory();
            finish();
        } else if (v.getId()==R.id.menuButton) {
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.openDrawer(GravityCompat.START);
        }
    }

}
