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
import com.example.solox3_dit2b21.model.BookWithReadingHistory;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class Bookshelf extends AppCompatActivity {

    private TextView tabFavouriteBooks, tabReadingHistory;
    private String noFavouriteBooks = "No books yet. Favourite a book!";
    private String noReadingHistory = "No books yet. Read a book!";
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
                bindDataForReadingHistory(userId);
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
        bookshelfAdapter = new BookshelfAdapter(bookshelfBooks,false, new BookshelfAdapter.MyRecyclerViewItemClickListener()
        {
            @Override
            public void onItemClicked(Book book)
            {
                Intent intent = new Intent(Bookshelf.this, AuthorBookDetails.class);
                intent.putExtra("bookId", book.getBookId());
                startActivity(intent);
            }

            @Override
            public void onReadingHistoryItemClicked(Book book) {
                Log.d("Bookshelf", "Reading history item clicked, but action is not defined.");
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
                bookshelfAdapter = new BookshelfAdapter(bookshelfBooks, false, new BookshelfAdapter.MyRecyclerViewItemClickListener() {
                    @Override
                    public void onItemClicked(Book book) {
                        Intent intent = new Intent(Bookshelf.this, AuthorBookDetails.class);
                        intent.putExtra("bookId", book.getBookId());
                        startActivity(intent);
                    }

                    @Override
                    public void onReadingHistoryItemClicked(Book book) {
                        // This method will not be used for favorite books
                    }
                });
                recyclerViewBookshelf.setAdapter(bookshelfAdapter);
                updateNoBooksFoundMessage(books.isEmpty());
            }

            @Override
            public void onError(Exception exception) {
                Log.e("bindDataForFavouriteBooks", "Error fetching favourite books", exception);
                Toast.makeText(Bookshelf.this, "Error fetching favourite books.", Toast.LENGTH_SHORT).show();
            }
        }, userId);
    }


    private void bindDataForReadingHistory(String userId) {
        bookDao.getUserReadingHistoryBooks(userId, new DataCallback<List<BookWithReadingHistory>>() {
            @Override
            public void onDataReceived(List<BookWithReadingHistory> booksWithHistory) {
                bookshelfBooks.clear();
                for (BookWithReadingHistory bookWithHistory : booksWithHistory) {
                    bookshelfBooks.add(bookWithHistory.getBook()); // Add the book part of BookWithReadingHistory to the list
                }
                // Update the adapter and RecyclerView
                bookshelfAdapter = new BookshelfAdapter(bookshelfBooks, true, new BookshelfAdapter.MyRecyclerViewItemClickListener() {
                    @Override
                    public void onItemClicked(Book book) {
                        // This method will not be used for reading history
                    }

                    @Override
                    public void onReadingHistoryItemClicked(Book book) {
                        int lastReadChapterOrder = -1; // Default value indicating no chapter order found
                        int lastReadSubChapterOrder = -1; // Default value indicating no subchapter order found
                        for (BookWithReadingHistory bookWithHistory : booksWithHistory) {
                            if (bookWithHistory.getBook().getBookId().equals(book.getBookId())) {
                                lastReadChapterOrder = bookWithHistory.getLastReadChapterOrder();
                                lastReadSubChapterOrder = bookWithHistory.getLastReadSubChapterOrder();
                                Log.d("onReadingHistoryItemClicked: ", "lastReadChapterOrder: " + lastReadChapterOrder + ", lastReadSubChapterOrder: " + lastReadSubChapterOrder);
                                break; // Found the matching book, no need to continue
                            }
                        }
                        // Navigate directly to the reading page with the last read chapter and subchapter
                        Intent intent = new Intent(Bookshelf.this, Reading.class);
                        intent.putExtra("bookId", book.getBookId());
                        intent.putExtra("lastReadChapterOrder", lastReadChapterOrder);
                        intent.putExtra("lastReadSubChapterOrder", lastReadSubChapterOrder);
                        startActivity(intent);
                    }
                });
                recyclerViewBookshelf.setAdapter(bookshelfAdapter);
                updateNoBooksFoundMessage(booksWithHistory.isEmpty());
            }

            @Override
            public void onError(Exception exception) {
                Log.e("bindDataForReadingHistory", "Error fetching reading history", exception);
                Toast.makeText(Bookshelf.this, "Error fetching reading history.", Toast.LENGTH_SHORT).show();
            }
        });
    }




    private void updateNoBooksFoundMessage(boolean isEmpty) {
        TextView noBooksFound = findViewById(R.id.noBooksFound);
        if (isEmpty) {
            noBooksFound.setText(noReadingHistory);
            noBooksFound.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        } else {
            noBooksFound.setText("");
            noBooksFound.setTextSize(TypedValue.COMPLEX_UNIT_SP, 0);
        }
    }
}