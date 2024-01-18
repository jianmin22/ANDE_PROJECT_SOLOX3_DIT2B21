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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.solox3_dit2b21.Utils.CurrentDateUtils;
import com.example.solox3_dit2b21.model.Book;
import com.example.solox3_dit2b21.model.Category;
import com.example.solox3_dit2b21.model.Comment;
import com.example.solox3_dit2b21.R;
import com.example.solox3_dit2b21.model.UserFavouriteBook;
import com.example.solox3_dit2b21.model.UserRating;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

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
                        loadCategory(bookDetails.getCategoryId());
                        loadComments(bookId);
                        loadUserRating(bookId);
                        currentUserName.setText(userId);

                        loadUserFavourite(bookId);
                    } else {
                        Toast.makeText(getApplicationContext(), "Failed to get Book Details", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("Firebase", "Failed to get book details", databaseError.toException());
                    Toast.makeText(getApplicationContext(), "Failed to get Book Details", Toast.LENGTH_LONG).show();
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
                Toast.makeText(getApplicationContext(), "Failed to get Book Details", Toast.LENGTH_LONG).show();
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
                    double sumOfRating=0;
                    int countOfRating=0;
                    for (DataSnapshot userRating : userRatingSnapshot.getChildren()) {
                        UserRating userRatingValue = userRating.getValue(UserRating.class);
                        if(userRatingValue.getUserId().equals(userId)){
                            rated=true;
                            rateGiveByCurrentUser=userRatingValue.getRating();
                            if(rateGiveByCurrentUser==1){
                                sadEmojiImage.setImageResource(R.drawable.sadyellow);
                            }else if(rateGiveByCurrentUser==3){
                                neutralEmojiImage.setImageResource(R.drawable.neutralyellow);
                            }else if(rateGiveByCurrentUser==5){
                                happyEmojiImage.setImageResource(R.drawable.happyyellow);
                            }else {
                                rated=false;
                                rateGiveByCurrentUser=0;
                            }
                        }
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
                Toast.makeText(getApplicationContext(), "Failed to get Book Details", Toast.LENGTH_LONG).show();
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
                        bookCategory=category;
                        bookCategoryButton.setText(category.getCategoryName());
                    }
                }else {
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
                    Log.d("NOT FOUND", "No user favourite found for the book");
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Failed to get user favourite", databaseError.toException());
                Toast.makeText(getApplicationContext(), "Failed to get Book Details", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void deleteUserFavourite() {
        DatabaseReference userFavouriteRef = FirebaseDatabase.getInstance().getReference("UserFavouriteBook");

        Query deleteQuery = userFavouriteRef.orderByChild("bookId").equalTo(bookId);
        deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserFavouriteBook userFavouriteBook = snapshot.getValue(UserFavouriteBook.class);

                    if (userFavouriteBook != null && userFavouriteBook.getUserId().equals(userId)) {
                        snapshot.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                addedToFavourite = false;
                                addToFavouriteStarImage.setImageResource(R.drawable.starnocolor);
                                loadUserFavourite(bookId);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("Firebase", "Failed to delete user favourite", e);
                                // Handle the failure case here
                            }
                        });
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
                        loadUserFavourite(bookId);
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


    private void handleRating(int emojiId) {
        double ratingToSet = 0;
        if (emojiId == R.id.sadEmoji) {
            ratingToSet = 1.0;
        } else if (emojiId == R.id.neutralEmoji) {
            ratingToSet = 3.0;
        } else if (emojiId == R.id.happyEmoji) {
            ratingToSet = 5.0;
        }

        if (rated && rateGiveByCurrentUser == ratingToSet) {
            deleteRating();
        } else {
                if (rated) {
                    updateRating(ratingToSet);
                } else {
                    insertRating(ratingToSet);
                }

        }
    }

    private void deleteRating() {
        DatabaseReference userRatingRef = FirebaseDatabase.getInstance().getReference("UserRating");
        Query deleteQuery = userRatingRef.orderByChild("bookId").equalTo(bookId);
        deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserRating userRating = snapshot.getValue(UserRating.class);
                    if (userRating != null && userRating.getUserId().equals(userId)) {
                        snapshot.getRef().removeValue();
                        rated = false;
                        if(rateGiveByCurrentUser==1){
                            sadEmojiImage.setImageResource(R.drawable.sadnocolor);
                            loadUserRating(bookId);
                        }else if(rateGiveByCurrentUser==3){
                            neutralEmojiImage.setImageResource(R.drawable.neutralnocolor);
                            loadUserRating(bookId);
                        }else{
                            happyEmojiImage.setImageResource(R.drawable.happynocolor);
                            loadUserRating(bookId);
                        }
                        rateGiveByCurrentUser = 0.0;
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Failed to delete user rating", databaseError.toException());
            }
        });
    }


    private void updateRating(double newRating) {
        DatabaseReference userRatingRef = FirebaseDatabase.getInstance().getReference("UserRating");

        // Find the rating entry with the userId and bookId and update it
        Query updateQuery = userRatingRef.orderByChild("bookId").equalTo(bookId);
        updateQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserRating userRating = snapshot.getValue(UserRating.class);
                    if (userRating != null && userRating.getUserId().equals(userId) ) {
                        snapshot.getRef().child("rating").setValue(newRating);
                        if(rateGiveByCurrentUser==1){
                            sadEmojiImage.setImageResource(R.drawable.sadnocolor);
                            loadUserRating(bookId);
                        }else if(rateGiveByCurrentUser==3){
                            neutralEmojiImage.setImageResource(R.drawable.neutralnocolor);
                            loadUserRating(bookId);
                        }else{
                            happyEmojiImage.setImageResource(R.drawable.happynocolor);
                            loadUserRating(bookId);
                        }
                        rateGiveByCurrentUser = newRating;
                        if(rateGiveByCurrentUser==1){
                            sadEmojiImage.setImageResource(R.drawable.sadyellow);
                            loadUserRating(bookId);
                        }else if(rateGiveByCurrentUser==3){
                            neutralEmojiImage.setImageResource(R.drawable.neutralyellow);
                            loadUserRating(bookId);
                        }else{
                            happyEmojiImage.setImageResource(R.drawable.happyyellow);
                            loadUserRating(bookId);
                        }
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Failed to update user rating", databaseError.toException());
            }
        });
    }

    private void insertRating(double newRating) {
        String currentDateTime = CurrentDateUtils.getCurrentDateTime();
        DatabaseReference userRatingRef = FirebaseDatabase.getInstance().getReference("UserRating");
        UserRating userRating = new UserRating(generateUUID(),newRating,currentDateTime,bookId,userId);
        userRatingRef.push().setValue(userRating)
                .addOnSuccessListener(aVoid -> {
                    rated = true;
                    rateGiveByCurrentUser = newRating;
                    if(rateGiveByCurrentUser==1){
                        sadEmojiImage.setImageResource(R.drawable.sadyellow);
                        loadUserRating(bookId);
                    }else if(rateGiveByCurrentUser==3){
                        neutralEmojiImage.setImageResource(R.drawable.neutralyellow);
                        loadUserRating(bookId);
                    }else{
                        happyEmojiImage.setImageResource(R.drawable.happyyellow);
                        loadUserRating(bookId);
                    }
                })
                .addOnFailureListener(e -> Log.e("Firebase", "Failed to insert user rating", e));
    }

    private void addComment() {
        String commentText = addCommentsInputField.getText().toString().trim();
        if (commentText.isEmpty()) {
            Toast.makeText(this, "Comment cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        String commentId = generateUUID();
        String currentDate = CurrentDateUtils.getCurrentDateTime();

        Comment newComment = new Comment(commentId, bookId, commentText, userId, currentDate);

        DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference("Comment");
        commentsRef.child(commentId).setValue(newComment)
                .addOnSuccessListener(aVoid -> {
                    addCommentsInputField.setText("");
                    loadComments(bookId);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(BookDetails.this, "Failed to add comment, Try again later", Toast.LENGTH_SHORT).show();
                });
    }



    private void loadBookImage(String imageUrl, ImageView imageView) {
        // Load image into ImageView using Glide
        Glide.with(imageView.getContext())
                .load(imageUrl)
                .into(imageView);
    }

    private String generateUUID() {
        // Generate a unique searchHistoryId using UUID
        return UUID.randomUUID().toString();
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
        } else if (v.getId() == R.id.sadEmoji || v.getId() == R.id.neutralEmoji || v.getId() == R.id.happyEmoji) {
            handleRating(v.getId());
        } else if (v.getId() == R.id.addCommentBtn){
            addComment();
        }
    }
}
