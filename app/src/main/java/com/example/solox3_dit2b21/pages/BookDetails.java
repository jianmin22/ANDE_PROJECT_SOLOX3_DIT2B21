package com.example.solox3_dit2b21.pages;

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

import com.example.solox3_dit2b21.Utils.AuthUtils;
import com.example.solox3_dit2b21.Utils.CurrentDateUtils;
import com.example.solox3_dit2b21.Utils.LoadImageURL;
import com.example.solox3_dit2b21.dao.BookDao;
import com.example.solox3_dit2b21.dao.CategoryDao;
import com.example.solox3_dit2b21.dao.CommentDao;
import com.example.solox3_dit2b21.dao.DataCallback;
import com.example.solox3_dit2b21.dao.DataStatusCallback;
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
import com.example.solox3_dit2b21.R;
import com.example.solox3_dit2b21.model.UserFavouriteBook;
import com.example.solox3_dit2b21.model.UserRating;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class BookDetails extends AppCompatActivity implements View.OnClickListener {
    private String bookId;
    private Book bookDetails;
    private String userId;
    private String userName;
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
    private CommentAdapter adapter;

    private boolean addedToFavourite=false;
    private boolean rated=false;
    private double rateGiveByCurrentUser=0.0;

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
        setContentView(R.layout.activity_book_details);
        userId=AuthUtils.getUserId();
        userName=AuthUtils.getUserName();
        Bundle getData = getIntent().getExtras();

        if (getData != null) {
            bookId = getData.getString("bookId");
            bookImage = findViewById(R.id.bookImage);
            recyclerView = findViewById(R.id.recyclerViewLatestComments);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new CommentAdapter(twoCommentsForBook);
            recyclerView.setAdapter(adapter);
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

            bookDao.loadBookDetailsById(bookId, new DataCallback<Book>() {
                @Override
                public void onDataReceived(Book returnedBookDetails) {
                    if (returnedBookDetails!=null) {
                        bookDetails = returnedBookDetails;
                        LoadImageURL.loadImageURL(bookDetails.getImage(),bookImage);
                        bookTitle.setText(bookDetails.getTitle());
                        descriptionContent.setText(bookDetails.getDescription());
                        loadCategory(bookDetails.getCategoryId());
                        loadComments(bookId);
                        loadUserRating(bookId);
                        currentUserName.setText(userName);

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
                addToFavouriteText.setText(String.valueOf(noOfUserFavouriteBook));
            }

            @Override
            public void onError(Exception exception) {
                Log.e("Firebase", "Failed to get user favourite", exception);
                Toast.makeText(getApplicationContext(), "Failed to get Book Details", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void deleteUserFavourite() {
        userFavouriteBookDao.deleteUserFavourite(bookId, userId, new DataStatusCallback() {
            @Override
            public void onSuccess() {
                addedToFavourite = false;
                addToFavouriteStarImage.setImageResource(R.drawable.starnocolor);
                loadUserFavourite(bookId);
            }

            @Override
            public void onFailure(Exception exception) {
                Log.e("Firebase", "Failed to delete user favourite", exception);
            }
        });
    }

    private void insertUserFavourite() {
        UserFavouriteBook userFavouriteBook = new UserFavouriteBook(userId, bookId);
        userFavouriteBookDao.insertUserFavourite(userFavouriteBook, new DataStatusCallback() {
            @Override
            public void onSuccess() {
                addedToFavourite = true;
                addToFavouriteStarImage.setImageResource(R.drawable.staryellow);
                loadUserFavourite(bookId);
            }

            @Override
            public void onFailure(Exception exception) {
                Log.e("Firebase", "Failed to insert user favourite", exception);
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
        userRatingDao.deleteUserRating(bookId, userId, new DataStatusCallback() {
            @Override
            public void onSuccess() {
                rated = false;
                resetEmojiImages();
                rateGiveByCurrentUser = 0.0;
            }

            @Override
            public void onFailure(Exception exception) {
                Log.e("Firebase", "Failed to delete user rating", exception);
            }
        });
    }

    private void updateRating(double newRating) {
        userRatingDao.updateUserRating(bookId, userId, newRating, new DataStatusCallback() {
            @Override
            public void onSuccess() {
                resetEmojiImages();
                rateGiveByCurrentUser = newRating;
                updateEmojiImage(newRating);
                loadUserRating(bookId);
            }

            @Override
            public void onFailure(Exception exception) {
                Log.e("Firebase", "Failed to update user rating", exception);
            }
        });
    }

    private void insertRating(double newRating) {
        String currentDateTime = CurrentDateUtils.getCurrentDateTime();
        UserRating userRating = new UserRating(generateUUID(), newRating, currentDateTime, bookId, userId);

        userRatingDao.insertUserRating(userRating, new DataStatusCallback() {
            @Override
            public void onSuccess() {
                rated = true;
                rateGiveByCurrentUser = newRating;
                updateEmojiImage(rateGiveByCurrentUser);
                loadUserRating(bookId);
            }

            @Override
            public void onFailure(Exception exception) {
                Log.e("Firebase", "Failed to insert user rating", exception);
            }
        });
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

        commentDao.addComment(newComment, new DataStatusCallback() {
            @Override
            public void onSuccess() {
                addCommentsInputField.setText("");
                loadComments(bookId);
            }

            @Override
            public void onFailure(Exception exception) {
                Toast.makeText(BookDetails.this, "Failed to add comment, Try again later", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private String generateUUID() {
        return UUID.randomUUID().toString();
    }

    private void resetEmojiImages() {
        sadEmojiImage.setImageResource(R.drawable.sadnocolor);
        neutralEmojiImage.setImageResource(R.drawable.neutralnocolor);
        happyEmojiImage.setImageResource(R.drawable.happynocolor);
    }

    private void updateEmojiImage(double rating) {
        switch ((int)rating) {
            case 1:
                sadEmojiImage.setImageResource(R.drawable.sadyellow);
                break;
            case 3:
                neutralEmojiImage.setImageResource(R.drawable.neutralyellow);
                break;
            default:
                happyEmojiImage.setImageResource(R.drawable.happyyellow);
                break;
        }
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
