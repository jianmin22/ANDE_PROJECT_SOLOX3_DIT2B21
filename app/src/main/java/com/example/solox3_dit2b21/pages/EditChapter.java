package com.example.solox3_dit2b21.pages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

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
    private List<Chapter> chapters = new ArrayList<>(); // Initialize the chapters list
    private DatabaseReference databaseReference;
    private String bookId;

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
    }

    private void getChaptersData() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Chapters");
        databaseReference.orderByChild("bookId").equalTo(bookId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        chapters.clear(); // Clear existing data
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Chapter chapter = snapshot.getValue(Chapter.class);
                            if (chapter != null) {
                                chapters.add(chapter); // Add chapter to the list
                            }
                        }
                        if (chapters.isEmpty()) {
                            Log.e("EditChapter", "No chapters found for bookId: " + bookId);
                        } else {
                            adapter.notifyDataSetChanged(); // Notify the adapter of the data change
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("EditChapter", "Database error: " + databaseError.getMessage());
                    }
                });
    }
}

