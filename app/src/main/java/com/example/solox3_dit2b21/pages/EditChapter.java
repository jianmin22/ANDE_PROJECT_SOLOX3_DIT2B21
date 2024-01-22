package com.example.solox3_dit2b21.pages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.solox3_dit2b21.R;
import com.example.solox3_dit2b21.model.Chapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EditChapter extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EditChaptersAdapter adapter;
    private List<Chapter> chapters = new ArrayList<>();
    private DatabaseReference databaseReference;
    private String bookId;

    private String chapterNodeIdentifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_chapter);

        bookId = getIntent().getStringExtra("bookId");
        if (bookId == null) {
            Log.e("EditChapter", "No bookId passed to the activity");
            finish();
            return;
        }

        recyclerView = findViewById(R.id.recyclerView_edit_chapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new EditChaptersAdapter(this, chapters);
        recyclerView.setAdapter(adapter);
        getChaptersData();
        Button btnSave = findViewById(R.id.save_chapter_button);
        btnSave.setOnClickListener(this::saveChapters);


    }

    private void getChaptersData() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Chapter");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot bookChapterSnapshot : dataSnapshot.getChildren()) {
                    if (bookChapterSnapshot.child("bookId").getValue(String.class).equals(bookId)) {
                        chapterNodeIdentifier = bookChapterSnapshot.getKey(); // Store the node identifier
                        Log.d("chapterNodeIdentifier: ", chapterNodeIdentifier);
                        DataSnapshot chaptersSnapshot = bookChapterSnapshot.child("chapters");
                        for (DataSnapshot chapterSnapshot : chaptersSnapshot.getChildren()) {
                            Log.d("node identifier: ",chapterSnapshot.getKey());
                            Chapter chapter = chapterSnapshot.getValue(Chapter.class);
                            if (chapter != null) {
                                chapters.add(chapter);
                            }
                        }
                        break;
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("EditChapter", "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void saveChapters(View view) {
        // First, find the parent node for the chapters based on bookId
        databaseReference.orderByChild("bookId").equalTo(bookId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot bookChapterSnapshot : dataSnapshot.getChildren()) {
                                // We found the parent node, now get its key
                                String parentNodeKey = bookChapterSnapshot.getKey();
                                DatabaseReference bookChaptersRef = databaseReference.child(parentNodeKey).child("chapters");
                                bookChaptersRef.setValue(chapters)
                                        .addOnSuccessListener(aVoid -> Toast.makeText(EditChapter.this, "Chapter " + " saved successfully", Toast.LENGTH_SHORT).show())
                                        .addOnFailureListener(e -> Toast.makeText(EditChapter.this, "Failed to save chapter " + ": " + e.getMessage(), Toast.LENGTH_SHORT).show());
                                ;

                            }
                        } else {
                            Log.e("EditChapter", "No parent node found for bookId: " + bookId);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("EditChapter", "Database error: " + databaseError.getMessage());
                    }
                });
    }




}
