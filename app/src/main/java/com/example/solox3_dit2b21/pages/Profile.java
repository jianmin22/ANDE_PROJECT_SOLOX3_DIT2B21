package com.example.solox3_dit2b21.pages;

import androidx.appcompat.app.AppCompatActivity;
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
import com.example.solox3_dit2b21.model.Book;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class Profile extends AppCompatActivity {

    private TextView textPublished, textDraft;
    private ImageView profilePic;
    FirebaseAuth auth;
    FirebaseUser user;
    private RecyclerView recyclerViewProfile;
    private ProfileAdapter profileAdapter;
    private List<Book> booksProfile = new ArrayList<>();
    private FirebaseBookDao bookDao = new FirebaseBookDao();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        textPublished = findViewById(R.id.textPublished);
        textDraft = findViewById(R.id.textDraft);
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

        setSelectedTab(textPublished);

        textPublished.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectedTab(textPublished);
                bindDataForProfile(user.getUid(), true);
            }
        });

        textDraft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectedTab(textDraft);
                bindDataForProfile(user.getUid(), false);
            }
        });

//        bindDataForProfile(user.getUid(), true);
        setUIRef();
    }

    private void setSelectedTab(TextView selectedTab) {
        textPublished.setTextColor(Color.parseColor("000000"));
        textDraft.setTextColor(Color.parseColor("000000"));

        selectedTab.setTextColor(Color.parseColor("F4D163"));
    }

    private void setUIRef()
    {
        recyclerViewProfile = findViewById(R.id.recycler_view_profile);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewProfile.setLayoutManager(layoutManager1);
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