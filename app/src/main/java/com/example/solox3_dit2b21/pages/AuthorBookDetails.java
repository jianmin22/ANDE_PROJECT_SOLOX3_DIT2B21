package com.example.solox3_dit2b21.pages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.solox3_dit2b21.R;
import com.example.solox3_dit2b21.model.Book;
import com.example.solox3_dit2b21.model.Category;
import com.example.solox3_dit2b21.model.Comment;
import com.example.solox3_dit2b21.model.UserRating;
import com.example.solox3_dit2b21.pages.CommentsAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AuthorBookDetails extends AppCompatActivity implements View.OnClickListener {
    private String bookId;
    private Book bookDetails;
    private String userId="user1";
    private List<Comment> twoCommentsForBook=new ArrayList<>();
    private double calculatedUserRating;
    private Category bookCategory;
    private int noOfUserFavouriteBook;

    ImageView bookImage;

    TextView bookTitle;

    TextView bookRating;
    Button bookCategoryButton;

    TextView addToFavouriteText;

    RelativeLayout editBookContainer;

    TextView descriptionContent;


    private RecyclerView recyclerView;
    private CommentsAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_book_details);
        Bundle getData = getIntent().getExtras();

        if (getData != null) {
            editBookContainer = (RelativeLayout) findViewById(R.id.editBookContainer);
            bookId = getData.getString("bookId");
            bookImage = findViewById(R.id.bookImage);
            recyclerView = findViewById(R.id.recyclerViewLatestComments);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new CommentsAdapter(twoCommentsForBook);
            recyclerView.setAdapter(adapter);
            bookTitle = findViewById(R.id.bookTitle);
            bookRating = findViewById(R.id.rating);
            bookCategoryButton= findViewById(R.id.categoryButton);
            addToFavouriteText = findViewById(R.id.noOfFav);
            descriptionContent=findViewById(R.id.descriptionContent);

            DatabaseReference bookRef = FirebaseDatabase.getInstance().getReference().child("Book").child(bookId);

            bookRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        bookDetails = dataSnapshot.getValue(Book.class);
                        loadBookImage(bookDetails.getImage(), bookImage);
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
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("Firebase", "Failed to get book details", databaseError.toException());
                    finish();
                }
            });
        }
    }

    private void loadComments(String bookId) {
        DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference("Comment");
        Query commentsQuery = commentsRef.orderByChild("bookId").equalTo(bookId);

        commentsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot commentSnapshot) {
                if (commentSnapshot.exists()) {
                    List<Comment> commentsList = new ArrayList<>();

                    for (DataSnapshot commentData : commentSnapshot.getChildren()) {
                        Comment comment = commentData.getValue(Comment.class);
                        Log.d("comment",comment.getCommentsId());
                        commentsList.add(comment);
                    }

                    Collections.sort(commentsList, new Comparator<Comment>() {
                        @Override
                        public int compare(Comment c1, Comment c2) {
                            return c2.getDate().compareTo(c1.getDate());
                        }
                    });

                    twoCommentsForBook.clear();
                    int count = Math.min(commentsList.size(), 2);
                    for (int i = 0; i < count; i++) {
                        twoCommentsForBook.add(commentsList.get(i));
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Log.d("NOT FOUND", "No comments found for the book");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Failed to get comments", databaseError.toException());
                finish();
            }
        });
    }

    private void loadUserRating(String bookId) {
        DatabaseReference userRating = FirebaseDatabase.getInstance().getReference("UserRating");
        Query userRatingQuery = userRating.orderByChild("bookId").equalTo(bookId);

        userRatingQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userRatingSnapshot) {
                if (userRatingSnapshot.exists()) {
                    List<UserRating> userRatingList = new ArrayList<>();
                    double sumOfRating=0;
                    int countOfRating=0;
                    for (DataSnapshot userRating : userRatingSnapshot.getChildren()) {
                        UserRating userRatingValue = userRating.getValue(UserRating.class);
                        countOfRating+=1;
                        sumOfRating+=userRatingValue.getRating();
                    }
                    if (countOfRating > 0) {
                        calculatedUserRating = sumOfRating / countOfRating;
                    } else {
                        // No ratings available
                        calculatedUserRating = 0;
                    }

                    bookRating.setText(String.valueOf(calculatedUserRating));
                } else {
                    Log.d("NOT FOUND", "No user Rating found for the book");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Failed to get user rating", databaseError.toException());
                finish();
            }
        });
    }

    private void loadCategory(String categoryId) {
        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference().child("Category").child(categoryId);
        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Category category = dataSnapshot.getValue(Category.class);
                    if (category != null) {
                        bookCategory = category;
                        bookCategoryButton.setText(category.getCategoryName());
                    }
                } else {
                    Log.d("NOT FOUND", "No Category found for the book");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error fetching category data", databaseError.toException());
                Toast.makeText(getApplicationContext(), "Failed to get Book Details", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void loadUserFavourite(String bookId) {
        DatabaseReference userFavourite = FirebaseDatabase.getInstance().getReference("UserFavouriteBook");
        Query userFavouriteQuery = userFavourite.orderByChild("bookId").equalTo(bookId);
        userFavouriteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userFavouriteSnapshot) {
                if (userFavouriteSnapshot.exists()) {
                    for (DataSnapshot userFavouriteValue : userFavouriteSnapshot.getChildren()) {
                        noOfUserFavouriteBook+=1;
                    }
                    addToFavouriteText.setText(String.valueOf(noOfUserFavouriteBook));
                } else {
                    Log.d("NOT FOUND", "No user favourite found for the book");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Failed to get user favourite", databaseError.toException());
                finish();
            }
        });
    }


    private void loadBookImage(String imageUrl, ImageView imageView) {
        // Load image into ImageView using Glide
        Glide.with(imageView.getContext())
                .load(imageUrl)
                .into(imageView);
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
        }
    }
}