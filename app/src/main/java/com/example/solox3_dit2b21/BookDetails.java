package com.example.solox3_dit2b21;

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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;


public class BookDetails extends AppCompatActivity implements View.OnClickListener {
    private String bookId;
    private Book bookDetails;
    private final String userId="user1";
    private String profilePicURL;
    private List<Comment> twoCommentsForBook=new ArrayList<>();
    private double calculatedUserRating;
    private Category bookCategory;
    private int noOfUserFavouriteBook;

    ImageView bookImage;

    TextView bookTitle;

    TextView bookRating;
    Button bookCategoryButton;

    TextView addToFavouriteText;

    ImageView addToFavouriteStarImage;

    ImageView sadEmojiImage;
    ImageView neutralEmojiImage;

    ImageView happyEmojiImage;
    TextView descriptionContent;

    Button readNowButton;


    ImageView currentProfilePic;
    TextView currentUserName;
    EditText addCommentsInputField;

    private RecyclerView recyclerView;
    private CommentsAdapter adapter;

    private boolean addedToFavourite=false;
    private boolean rated=false;
    private double rateGiveByCurrentUser=0.0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);


        Bundle getData = getIntent().getExtras();

        if (getData != null) {
            bookId = getData.getString("bookId");
            bookImage = findViewById(R.id.bookImage);
            recyclerView = findViewById(R.id.recyclerViewLatestComments);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new CommentsAdapter(twoCommentsForBook);
            recyclerView.setAdapter(adapter);
            bookImage = findViewById(R.id.bookImage);
            bookTitle = findViewById(R.id.bookTitle);
            bookRating = findViewById(R.id.rating);
            bookCategoryButton= findViewById(R.id.categoryButton);
            addToFavouriteText = findViewById(R.id.noOfFav);
            addToFavouriteStarImage=findViewById(R.id.addToFavouriteStar);
            sadEmojiImage=findViewById(R.id.sadEmoji);
            neutralEmojiImage=findViewById(R.id.neutralEmoji);
            happyEmojiImage=findViewById(R.id.happyEmoji);
            descriptionContent=findViewById(R.id.descriptionContent);
            readNowButton=findViewById(R.id.buttonRead);
            currentProfilePic=findViewById(R.id.currentProfilePic);
            currentUserName=findViewById(R.id.currentUserName);
            addCommentsInputField=findViewById(R.id.addCommentsInputField);

            DatabaseReference bookRef = FirebaseDatabase.getInstance().getReference().child("Book").child(bookId);

            bookRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        bookDetails = dataSnapshot.getValue(Book.class);
                        loadBookImage(bookDetails.getImage(), bookImage);
                        bookTitle.setText(bookDetails.getTitle());
                        descriptionContent.setText(bookDetails.getDescription());
                        loadCategory(bookId);
                        loadComments(bookId);
                        loadUserRating(bookId);
                        currentUserName.setText(userId);

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
                    Log.e("NOT FOUND", "No comments found for the book");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Failed to get comments", databaseError.toException());
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
                    // No comments found for the book
                    Log.e("NOT FOUND", "No user Rating found for the book");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Failed to get user rating", databaseError.toException());
            }
        });
    }

    private void loadCategory(String bookId) {
        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference("Category");
        Query categoryQuery = categoryRef.orderByChild("bookId").equalTo(bookId);
        categoryQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Category category = dataSnapshot.getValue(Category.class);
                    if (category != null) {
                        bookCategory=category;
                        bookCategoryButton.setText(bookCategory.getCategoryName());
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error fetching category data", databaseError.toException());
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
                    noOfUserFavouriteBook = (int) userFavouriteSnapshot.getChildrenCount();
                    addToFavouriteText.setText(String.valueOf(noOfUserFavouriteBook));
                    for (DataSnapshot snapshot : userFavouriteSnapshot.getChildren()) {
                        UserFavouriteBook userFavourite = snapshot.getValue(UserFavouriteBook.class);
                        if (userFavourite != null && userId.equals(userFavourite.getUserId())) {
                            addedToFavourite = true;
                            addToFavouriteStarImage.setImageResource(R.drawable.staryellow);
                            break;
                        }
                    }
                } else {
                    noOfUserFavouriteBook = 0;
                    addToFavouriteText.setText("0");
                    Log.e("NOT FOUND", "No user favourite found for the book");
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Failed to get user favourite", databaseError.toException());
                finish();
            }
        });
    }

    private void deleteUserFavourite() {
        DatabaseReference userFavouriteRef = FirebaseDatabase.getInstance().getReference("UserFavouriteBook");

        // Find the row with the userId and bookId and remove it
        Query deleteQuery = userFavouriteRef.orderByChild("bookId").equalTo(bookId);
        deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            UserFavouriteBook userFavouriteBook = snapshot.getValue(UserFavouriteBook.class);

                            if (userFavouriteBook != null && userFavouriteBook.getUserId().equals(userId)) {
                                snapshot.getRef().removeValue();
                                addedToFavourite = false;
                                addToFavouriteStarImage.setImageResource(R.drawable.starnocolor);
                                break;
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("Firebase", "Failed to delete user favourite", databaseError.toException());
                    }
                });
    }


    private void insertUserFavourite() {
        DatabaseReference userFavouriteRef = FirebaseDatabase.getInstance().getReference("UserFavouriteBook");
        UserFavouriteBook userFavouriteBook = new UserFavouriteBook(userId, bookId);

        // Insert the new UserFavouriteBook into the database
        userFavouriteRef.push().setValue(userFavouriteBook)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Successfully added to the database
                        addedToFavourite = true;
                        addToFavouriteStarImage.setImageResource(R.drawable.staryellow);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to add to the database
                        Log.e("Firebase", "Failed to insert user favourite", e);
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
            Intent intent= new Intent(BookDetails.this, AllComments.class);
            intent.putExtra("bookId", bookId);
            startActivity(intent);
        } else if (v.getId()==R.id.starContainer){
            if(!addedToFavourite){
                insertUserFavourite();
            }else{
                deleteUserFavourite();
            }
        } else if (v.getId()==R.id.buttonRead){
            Intent intent = new Intent(BookDetails.this, Reading.class);
            intent.putExtra("bookId", bookId);
            startActivity(intent);
        }
    }
}
