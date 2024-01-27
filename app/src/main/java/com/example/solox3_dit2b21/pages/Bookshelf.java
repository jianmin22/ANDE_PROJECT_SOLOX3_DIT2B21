package com.example.solox3_dit2b21.pages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.solox3_dit2b21.R;
import com.example.solox3_dit2b21.Utils.AuthUtils;
import com.example.solox3_dit2b21.dao.DataCallback;
import com.example.solox3_dit2b21.daoimpl.FirebaseBookDao;
import com.example.solox3_dit2b21.daoimpl.FirebaseCommentDao;
import com.example.solox3_dit2b21.daoimpl.FirebaseUserRatingDao;
import com.example.solox3_dit2b21.model.Book;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class Bookshelf extends AppCompatActivity {

    private TextView tabFavouriteBooks, tabReadingHistory;
    int selectedColor, unselectedColor;
    private BottomNavigationView bottomNavigationView;
    FirebaseAuth auth;
    FirebaseUser user;
    private RecyclerView recyclerViewBookshelf;
    private BookshelfAdapter bookshelfAdapter;
    private List<Book> bookshelfBooks = new ArrayList<>();
    private FirebaseBookDao bookDao = new FirebaseBookDao();
    private FirebaseCommentDao commentDao = new FirebaseCommentDao();
    private FirebaseUserRatingDao userRatingDao = new FirebaseUserRatingDao();
    @Override
    protected void onStart() {
        super.onStart();
        AuthUtils.redirectToLoginIfNotAuthenticated(this);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookshelf);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_home) {
                Intent intent = new Intent(this, Home.class);
                startActivity(intent);
            } else if (itemId == R.id.navigation_category) {
                Intent intent = new Intent(this, CategoryPage.class);
                startActivity(intent);
            } else if (itemId == R.id.navigation_profile) {
                Intent intent = new Intent(this, Profile.class);
                startActivity(intent);
            }

            return true;
        });

        // To select a default item (e.g. profile) when the activity starts
        bottomNavigationView.setSelectedItemId(R.id.navigation_bookshelf);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        String userId = user.getUid();

        tabFavouriteBooks = findViewById(R.id.tabFavouriteBooks);
        tabReadingHistory = findViewById(R.id.tabReadingHistory);

        selectedColor = ContextCompat.getColor(this, R.color.selected_color);
        unselectedColor = ContextCompat.getColor(this, R.color.unselected_color);

        tabFavouriteBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectedTab(tabFavouriteBooks, tabReadingHistory);
                bindDataForFavouriteBooks(userId);
            }
        });

        tabReadingHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectedTab(tabReadingHistory, tabFavouriteBooks);
                // insert bindDataForReadingHistory
            }
        });

        recyclerViewBookshelf = findViewById(R.id.recycler_view_bookshelf);

        bindDataForFavouriteBooks(userId);
        setUIRef();
    }

    private void setSelectedTab(TextView selectedTab, TextView unselectedTab) {
        selectedTab.setTextColor(selectedColor);
        selectedTab.setElevation(4);
        selectedTab.setShadowLayer(4, 0, 2, Color.BLACK);

        unselectedTab.setTextColor(unselectedColor);
        unselectedTab.setElevation(0);
        unselectedTab.setShadowLayer(0, 0, 0, Color.TRANSPARENT);
    }

    private void setUIRef() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3, LinearLayoutManager.VERTICAL, false);
        recyclerViewBookshelf.setLayoutManager(layoutManager);
        bookshelfAdapter = new BookshelfAdapter(bookshelfBooks, new BookshelfAdapter.MyRecyclerViewItemClickListener()
        {
            @Override
            public void onItemClicked(Book book)
            {
                Intent intent = new Intent(Bookshelf.this, AuthorBookDetails.class);
                intent.putExtra("bookId", book.getBookId());
                startActivity(intent);
            }
        });

        recyclerViewBookshelf.setAdapter(bookshelfAdapter);
    }

    private void bindDataForFavouriteBooks(String userId) {

        bookDao.getUserFavouriteBooks(new DataCallback<List<Book>>() {
            @Override
            public void onDataReceived(List<Book> books) {
                bookshelfBooks.clear();
                bookshelfBooks.addAll(books);
                bookshelfAdapter.notifyDataSetChanged();

                TextView noBooksFound = findViewById(R.id.noBooksFound);

                if (books.size() == 0) {
                    if (noBooksFound.getText().toString().trim().equals("")) {
                        noBooksFound.setText("No books yet. Favourite a book!");
                        noBooksFound.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                    }
                } else if (books.size() != 0 && !noBooksFound.getText().toString().trim().equals("")) {
                    noBooksFound.setText("");
                    noBooksFound.setTextSize(TypedValue.COMPLEX_UNIT_SP, 0);
                }
            }

            @Override
            public void onError(Exception exception) {
                Log.e("bindDataForFavouriteBooks", "Error fetching favourite books", exception);
                Toast.makeText(Bookshelf.this, "Error fetching favourite books.", Toast.LENGTH_SHORT).show();
            }
        }, userId);
    }
}