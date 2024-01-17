package com.example.solox3_dit2b21;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Reading extends AppCompatActivity {

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);

        // Initialize Firebase
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Chapter");

        // Fetch data from Firebase
        fetchChapterData();
    }

    private void fetchChapterData() {
        databaseReference.child("chapter1").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Retrieve data from dataSnapshot and update UI
                Chapter chapter = dataSnapshot.getValue(Chapter.class);

                // Log the retrieved data
                if (chapter != null) {
                    Log.d("ReadingActivity", "Chapter Data Retrieved: " +
                            "BookId: " + chapter.getBookId() +
                            ", ChapterContent: " + chapter.getChapterContent() +
                            ", ChapterId: " + chapter.getChapterId() +
                            ", ChapterNumber: " + chapter.getChapterNumber() +
                            ", ChapterOrder: " + chapter.getChapterOrder() +
                            ", SubChapterOrder: " + chapter.getSubChapterOrder());
                } else {
                    Log.e("ReadingActivity", "Chapter Data is null");
                }

                // Update UI elements with chapter data
                TextView chapterTitle = findViewById(R.id.ChapterTitle);
                if (chapter != null) {
                    chapterTitle.setText("Chapter " + chapter.getChapterNumber() + " " + chapter.getTitle());
                } else {
                    chapterTitle.setText("Chapter Data Not Available");
                }
                TextView chapterContent =findViewById(R.id.chapterContent);
                chapterContent.setText(chapter.getChapterContent());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
                Log.e("ReadingActivity", "Failed to retrieve chapter data: " + databaseError.getMessage());
            }
        });
    }

}