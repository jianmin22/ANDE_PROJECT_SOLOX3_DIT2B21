package com.example.solox3_dit2b21;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AllComments extends AppCompatActivity {
    private String bookId;
    private RecyclerView recyclerView;
    private CommentsAdapter adapter;
    private List<Comments> allComments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_comments);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recycler_view_all_comments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CommentsAdapter(allComments);
        recyclerView.setAdapter(adapter);

        // Retrieve comments from Firebase
        bookId="book1";
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
                    Comments comment = snapshot.getValue(Comments.class);
                    if (comment != null) {
                        allComments.add(comment);
                    }
                }

                // Sort comments by date, assuming date is a string in "yyyy-MM-dd HH:mm:ss" format
                Collections.sort(allComments, new Comparator<Comments>() {
                    @Override
                    public int compare(Comments c1, Comments c2) {
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

}
