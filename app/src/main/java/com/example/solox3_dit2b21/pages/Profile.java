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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.solox3_dit2b21.R;
import com.example.solox3_dit2b21.dao.DataCallback;
import com.example.solox3_dit2b21.daoimpl.FirebaseBookDao;
import com.example.solox3_dit2b21.daoimpl.FirebaseCommentDao;
import com.example.solox3_dit2b21.daoimpl.FirebaseUserRatingDao;
import com.example.solox3_dit2b21.model.Book;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class Profile extends AppCompatActivity {
    private TextView profileUsername, totalPublished, totalComments, averageRating, tabPublished, tabDraft;
    int selectedColor, unselectedColor;
    private ImageView profilePic;
    FirebaseAuth auth;
    FirebaseUser user;
    private RecyclerView recyclerViewProfile;
    private ProfileAdapter profileAdapter;
    private List<Book> booksProfile = new ArrayList<>();
    private FirebaseBookDao bookDao = new FirebaseBookDao();
    private FirebaseCommentDao commentDao = new FirebaseCommentDao();
    private FirebaseUserRatingDao userRatingDao = new FirebaseUserRatingDao();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        String userId = user.getUid();

        profileUsername = findViewById(R.id.profileUsername);
        profileUsername.setText(user.getEmail());

//        Toast.makeText(Profile.this, "Display: " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
//        Toast.makeText(Profile.this, "UID: " + user.getUid(), Toast.LENGTH_SHORT).show();

        totalPublished = findViewById(R.id.totalPublished);
        bindDataForTotalPublished(userId);

        totalComments = findViewById(R.id.totalComments);
        bindDataForTotalComments(userId);

        averageRating = findViewById(R.id.averageRating);
        bindDataForAverageRating(userId);

        tabPublished = findViewById(R.id.tabPublished);
        tabDraft = findViewById(R.id.tabDraft);

        selectedColor = ContextCompat.getColor(this, R.color.selected_color);
        unselectedColor = ContextCompat.getColor(this, R.color.unselected_color);

        profilePic = findViewById(R.id.profilePic);
//        implementation 'com.squareup.picasso:picasso:2.71828'
//        if (user != null) {
//            String photoUrl = user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null;
//
//            if (photoUrl != null && !photoUrl.isEmpty()) {
//                Picasso.get().load(photoUrl).into(profilePic);
//            } else {
//                // Set a default image if the photo URL is not available
//                profilePic.setImageResource(R.drawable.default_profile_image);
//            }
//        }

//        setSelectedTab(tabPublished);

        tabPublished.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectedTab(tabPublished, tabDraft);
                bindDataForProfile(userId, true);
            }
        });

        tabDraft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectedTab(tabDraft, tabPublished);
                bindDataForProfile(userId, false);
            }
        });

        bindDataForProfile(userId, true);
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

    private void setUIRef()
    {
        recyclerViewProfile = findViewById(R.id.recycler_view_profile);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3, LinearLayoutManager.VERTICAL, false);
        recyclerViewProfile.setLayoutManager(layoutManager);
        profileAdapter = new ProfileAdapter(booksProfile, new ProfileAdapter.MyRecyclerViewItemClickListener()
        {
            @Override
            public void onItemClicked(Book book)
            {
                Intent intent = new Intent(Profile.this, BookDetails.class);
                intent.putExtra("bookId", book.getBookId());
                startActivity(intent);
            }
        });

        recyclerViewProfile.setAdapter(profileAdapter);

    }

    private void bindDataForTotalPublished(String userId) {

        bookDao.getTotalUserPublished(new DataCallback<Integer>() {
            @Override
            public void onDataReceived(Integer totalPublishedInt) {
                if (totalPublishedInt != null) {
                    totalPublished.setText(String.valueOf(totalPublishedInt));
                } else {
                    totalPublished.setText("0");
                }
            }

            @Override
            public void onError(Exception exception) {
                Log.e("bindDataForTotalPublished", "Error fetching total number of published books", exception);
                Toast.makeText(Profile.this, "Error fetching total number of published books.", Toast.LENGTH_SHORT).show();
            }
        }, userId);
    }

    private void bindDataForTotalComments(String userId) {

        commentDao.getTotalCommentsReceived(new DataCallback<Integer>() {
            @Override
            public void onDataReceived(Integer totalCommentsInt) {
                if (totalCommentsInt != null) {
                    totalComments.setText(String.valueOf(totalCommentsInt));
                } else {
                    totalComments.setText("0");
                }
            }

            @Override
            public void onError(Exception exception) {
                Log.e("bindDataForTotalComments", "Error fetching total number of comments", exception);
                Toast.makeText(Profile.this, "Error fetching total number of comments.", Toast.LENGTH_SHORT).show();
            }
        }, userId);
    }

    private void bindDataForAverageRating(String userId) {

        userRatingDao.getAverageUserRating(new DataCallback<Double>() {
            @Override
            public void onDataReceived(Double averageRatingInt) {
                if (averageRatingInt != null) {
                    averageRating.setText(String.format("%.2f", averageRatingInt));
                } else {
                    averageRating.setText("0");
                }
            }

            @Override
            public void onError(Exception exception) {
                Log.e("bindDataForAverageRating", "Error fetching user's average rating", exception);
                Toast.makeText(Profile.this, "Error fetching user's average rating.", Toast.LENGTH_SHORT).show();
            }
        }, userId);
    }

    private void bindDataForProfile(String userId, Boolean published) {

        bookDao.getUserBooks(new DataCallback<List<Book>>() {
            @Override
            public void onDataReceived(List<Book> books) {
                booksProfile.clear();
                booksProfile.addAll(books);
                profileAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception exception) {
                Log.e("bindDataForProfile", "Error fetching user books", exception);
                Toast.makeText(Profile.this, "Error fetching profile data.", Toast.LENGTH_SHORT).show();
            }
        }, userId, published);
    }
}