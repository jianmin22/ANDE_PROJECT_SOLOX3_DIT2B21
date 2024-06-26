package com.example.solox3_dit2b21.pages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.solox3_dit2b21.dao.UserDao;
import com.example.solox3_dit2b21.daoimpl.FirebaseUserDao;
import com.example.solox3_dit2b21.model.Comment;
import com.example.solox3_dit2b21.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AllComments extends AppCompatActivity implements View.OnClickListener{
    private String bookId;
    private RecyclerView recyclerView;
    private CommentAdapter adapter;
    private List<Comment> allComments = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_comments);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recycler_view_all_comments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CommentAdapter(allComments);
        recyclerView.setAdapter(adapter);

        // Retrieve comments from Firebase
        bookId=getIntent().getStringExtra("bookId");
        loadCommentsFromFirebase(bookId);
    }

    private void loadCommentsFromFirebase(String bookId) {
        DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference("Comment");

        // Query comments for a specific bookId
        commentsRef.orderByChild("bookId").equalTo(bookId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                allComments.clear(); // Clear existing data
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Comment comment = snapshot.getValue(Comment.class);
                    if (comment != null) {
                        allComments.add(comment);
                    }
                }

                // Sort comments by date, assuming date is a string in "yyyy-MM-dd HH:mm:ss" format
                Collections.sort(allComments, new Comparator<Comment>() {
                    @Override
                    public int compare(Comment c1, Comment c2) {
                        return c2.getDate().compareTo(c1.getDate());
                    }
                });

                adapter.notifyDataSetChanged(); // Notify adapter that data has changed
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("AllComments", "Error loading comments from Firebase: " + databaseError.getMessage());
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.backButtonComment) {
            finish();
        }
    }

}
