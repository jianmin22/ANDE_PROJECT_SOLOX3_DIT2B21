package com.example.solox3_dit2b21;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

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

public class BookDetails extends AppCompatActivity {
    private String bookId;
    private Book bookDetails;
    private List<Comment> twoCommentsForBook;
    private double calculatedUserRating;
    private Category bookCategory;
    private int noOfUserFavouriteBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        Bundle getData = getIntent().getExtras();

        if (getData != null) {
            bookId = getData.getString("bookId");

            DatabaseReference bookRef = FirebaseDatabase.getInstance().getReference().child("Book").child(bookId);
            Log.e("Data", "bookRef: " + bookRef);
            bookRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        bookDetails = dataSnapshot.getValue(Book.class);

                        DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference("Comment");
                        Query commentsQuery = commentsRef.orderByChild("bookId").equalTo(bookId);

                        commentsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot commentSnapshot) {
                                if (commentSnapshot.exists()) {
                                    List<Comment> commentsList = new ArrayList<>();

                                    for (DataSnapshot commentData : commentSnapshot.getChildren()) {
                                        Comment comment = commentData.getValue(Comment.class);
                                        commentsList.add(comment);
                                    }

                                    // Sort commentsList based on the date in descending order
                                    Collections.sort(commentsList, new Comparator<Comment>() {
                                        @Override
                                        public int compare(Comment comment1, Comment comment2) {
                                            return comment2.getDate().compareTo(comment1.getDate());
                                        }
                                    });

                                    twoCommentsForBook = commentsList.subList(0, Math.min(2, commentsList.size()));
                                } else {
                                    Log.e("NOT FOUND", "No comments found for the book");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.e("Firebase", "Failed to get comments", databaseError.toException());
                            }
                        });

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
                                } else {
                                    // No comments found for the book
                                    Log.e("NOT FOUND", "No user Rating found for the book");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.e("Firebase", "Failed to get user rating", databaseError.toException());
                            }
                        });

                        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference().child("categories").child(bookId);
                        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@org.checkerframework.checker.nullness.qual.NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    Category category = dataSnapshot.getValue(Category.class);
                                    if (category != null) {
                                        bookCategory=category;
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@org.checkerframework.checker.nullness.qual.NonNull DatabaseError databaseError) {
                                Log.e("Firebase", "Error fetching category data", databaseError.toException());
                            }
                        });

                        DatabaseReference userFavourite = FirebaseDatabase.getInstance().getReference("UserFavouriteBook");
                        Query userFavouriteQuery = userFavourite.orderByChild("bookId").equalTo(bookId);
                        userFavouriteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot userFavouriteSnapshot) {
                                if (userFavouriteSnapshot.exists()) {
                                    for (DataSnapshot userFavouriteValue : userFavouriteSnapshot.getChildren()) {
                                        noOfUserFavouriteBook+=1;
                                    }
                                } else {
                                    Log.e("NOT FOUND", "No user favourite found for the book");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.e("Firebase", "Failed to get user favourite", databaseError.toException());
                            }
                        });


                    } else {
                        Log.e("NOT FOUND", "Book not found");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("Firebase", "Failed to get book details", databaseError.toException());
                }
            });



        } else {
            Toast.makeText(getApplicationContext(), "Failed to get Data", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(BookDetails.this, Home.class);
            startActivity(intent);
        }
    }
}
