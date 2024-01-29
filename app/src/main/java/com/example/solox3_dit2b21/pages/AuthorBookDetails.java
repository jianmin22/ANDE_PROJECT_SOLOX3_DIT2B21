package com.example.solox3_dit2b21.pages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.solox3_dit2b21.R;
import com.example.solox3_dit2b21.Utils.AuthUtils;
import com.example.solox3_dit2b21.Utils.LoadImageURL;
import com.example.solox3_dit2b21.dao.BookDao;
import com.example.solox3_dit2b21.dao.CategoryDao;
import com.example.solox3_dit2b21.dao.CommentDao;
import com.example.solox3_dit2b21.dao.DataCallback;
import com.example.solox3_dit2b21.dao.UserFavouriteBookDao;
import com.example.solox3_dit2b21.dao.UserRatingDao;
import com.example.solox3_dit2b21.daoimpl.FirebaseBookDao;
import com.example.solox3_dit2b21.daoimpl.FirebaseCategoryDao;
import com.example.solox3_dit2b21.daoimpl.FirebaseCommentDao;
import com.example.solox3_dit2b21.daoimpl.FirebaseUserFavouriteBookDao;
import com.example.solox3_dit2b21.daoimpl.FirebaseUserRatingDao;
import com.example.solox3_dit2b21.model.Book;
import com.example.solox3_dit2b21.model.Category;
import com.example.solox3_dit2b21.model.Comment;

import java.util.ArrayList;
import java.util.List;

public class AuthorBookDetails extends AppCompatActivity implements View.OnClickListener {
    private String bookId;
    private Book bookDetails;
    private String userId;
    private List<Comment> twoCommentsForBook=new ArrayList<>();
    private double calculatedUserRating;
    private Category bookCategory;
    private int noOfUserFavouriteBook;

    ImageView bookImage;

    TextView bookTitle;

    TextView bookRating;
    Button bookCategoryButton;

    TextView noOfUserFavouriteText;

    RelativeLayout editBookContainer;

    TextView descriptionContent;


    private RecyclerView recyclerView;
    private CommentAdapter adapter;

    private BookDao bookDao = new FirebaseBookDao();
    private CommentDao commentDao = new FirebaseCommentDao();
    private UserRatingDao userRatingDao = new FirebaseUserRatingDao();

    private CategoryDao categoryDao = new FirebaseCategoryDao();
    private UserFavouriteBookDao userFavouriteBookDao = new FirebaseUserFavouriteBookDao();

    @Override
    protected void onStart() {
        super.onStart();
        AuthUtils.redirectToLoginIfNotAuthenticated(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_book_details);
        userId=AuthUtils.getUserId();
        Bundle getData = getIntent().getExtras();

        if (getData != null) {
            editBookContainer = (RelativeLayout) findViewById(R.id.editBookContainer);
            bookId = getData.getString("bookId");
            bookImage = findViewById(R.id.bookImage);
            recyclerView = findViewById(R.id.recyclerViewLatestComments);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new CommentAdapter(twoCommentsForBook);
            recyclerView.setAdapter(adapter);
            bookTitle = findViewById(R.id.bookTitle);
            bookRating = findViewById(R.id.rating);
            bookCategoryButton= findViewById(R.id.categoryButton);
            noOfUserFavouriteText = findViewById(R.id.noOfFav);
            descriptionContent=findViewById(R.id.descriptionContent);

            bookDao.loadBookDetailsById(bookId, new DataCallback<Book>() {
                @Override
                public void onDataReceived(Book returnedBookDetails) {
                    if (returnedBookDetails!=null) {
                        bookDetails = returnedBookDetails;
                        if (!bookDetails.getAuthorId().equals(userId)){
                            Toast.makeText(getApplicationContext(), "Failed to load page", Toast.LENGTH_LONG).show();
                            finish();
                        }
                        LoadImageURL.loadImageURL(bookDetails.getImage(), bookImage);
                        bookTitle.setText(bookDetails.getTitle());
                        descriptionContent.setText(bookDetails.getDescription());
                        loadCategory(bookDetails.getCategoryId());
                        loadComments(bookId);
                        loadUserRating(bookId);

                        loadUserFavourite(bookId);
                    } else {
                        Toast.makeText(getApplicationContext(), "Failed to get Data", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }

                @Override
                public void onError(Exception exception) {
                    Toast.makeText(getApplicationContext(), "Failed to get Book Details", Toast.LENGTH_LONG).show();
                    Log.e("Firebase", "Failed to get book details", exception);
                    finish();
                }
            });
        }
    }

    private void loadComments(String bookId) {
        commentDao.loadLatest2Comments(bookId, new DataCallback<List<Comment>>() {
            @Override
            public void onDataReceived(List<Comment> comments) {
                twoCommentsForBook.clear();
                twoCommentsForBook.addAll(comments);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception exception) {
                Log.e("loadComments", "Failed to get comments", exception);
                Toast.makeText(getApplicationContext(), "Failed to get Book Details", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void loadUserRating(String bookId) {
        userRatingDao.loadUserRatingForBook(bookId, new DataCallback<Double>() {
            @Override
            public void onDataReceived(Double rating) {
                calculatedUserRating = rating;
                bookRating.setText(String.format("%.1f", calculatedUserRating));
            }

            @Override
            public void onError(Exception exception) {
                Log.e("loadUserRating", "Failed to get user rating", exception);
                Toast.makeText(getApplicationContext(), "Failed to get Book Details", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void loadCategory(String categoryId) {
        categoryDao.loadBookCategory(categoryId, new DataCallback<Category>() {
            @Override
            public void onDataReceived(Category category) {
                if (category != null) {
                    bookCategory = category;
                    bookCategoryButton.setText(category.getCategoryName());
                } else {
                    Log.d("NOT FOUND", "No Category found for the book");
                }
            }

            @Override
            public void onError(Exception exception) {
                Log.e("Firebase", "Error fetching category data", exception);
                Toast.makeText(getApplicationContext(), "Failed to get Book Details", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void loadUserFavourite(String bookId) {
        userFavouriteBookDao.loadNumberOfUserFavouriteBook(bookId, new DataCallback<Integer>() {
            @Override
            public void onDataReceived(Integer count) {
                noOfUserFavouriteBook = count;
                noOfUserFavouriteText.setText(String.valueOf(noOfUserFavouriteBook));
            }

            @Override
            public void onError(Exception exception) {
                Log.e("Firebase", "Failed to get user favourite", exception);
                Toast.makeText(getApplicationContext(), "Failed to get Book Details", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void navigateToFilterResult() {
        Intent intent = new Intent(AuthorBookDetails.this, SearchFilterResults.class);
        intent.putExtra("filter", bookDetails.getCategoryId());
        String searchOrder="2";
        String filterOrder="1";
        intent.putExtra("searchOrder",searchOrder);
        intent.putExtra("filterOrder", filterOrder);
        startActivity(intent);
    }

    @Override
    public void onClick(View v){
        if (v.getId()==R.id.back){
            finish();
        }else if(v.getId()==R.id.seeAllCommentsBtn){
            Intent intent= new Intent(AuthorBookDetails.this, AllComments.class);
            intent.putExtra("bookId", bookId);
            startActivity(intent);
        } else if (v.getId()==R.id.editBookContainer){
            Intent intent = new Intent(AuthorBookDetails.this, AuthorEditBookDetails.class);
            intent.putExtra("bookId", bookId);
            startActivity(intent);
        } else if (v.getId()==R.id.categoryButton){
            navigateToFilterResult();
        }
    }
}