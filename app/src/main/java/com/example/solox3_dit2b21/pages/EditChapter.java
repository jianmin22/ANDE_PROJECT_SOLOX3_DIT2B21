package com.example.solox3_dit2b21.pages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.solox3_dit2b21.R;
import com.example.solox3_dit2b21.dao.ChapterDao;
import com.example.solox3_dit2b21.dao.DataCallback;
import com.example.solox3_dit2b21.dao.DataStatusCallback;
import com.example.solox3_dit2b21.daoimpl.FirebaseChapterDao;
import com.example.solox3_dit2b21.model.Chapter;
import com.example.solox3_dit2b21.model.SubChapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditChapter extends AppCompatActivity implements View.OnClickListener{
    private RecyclerView recyclerView;
    private EditChaptersAdapter adapter;
    private List<Chapter> chapters = new ArrayList<>();
    private DatabaseReference databaseReference;
    private String bookId;
    private ChapterDao chapterDao;
    private String chapterNodeIdentifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_chapter);
        chapterDao = new FirebaseChapterDao();
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
    void addNewChapter() {
        int newChapterOrder = chapters.size() + 1; // Assuming chapter order starts from 1
        Chapter newChapter = new Chapter();
        newChapter.setBookId(bookId);
        newChapter.setTitle("Chapter " + newChapterOrder);
        newChapter.setChapterOrder(newChapterOrder);

        // Initialize empty subChapters map or any other required fields
        Map<String, SubChapter> subChaptersMap = new HashMap<>();

        // Create a new subchapter
        SubChapter newSubChapter = new SubChapter();
        newSubChapter.setTitle("Part 1");
        newSubChapter.setChapterContent("Your content here...");
        newSubChapter.setSubChapterOrder(1); // Assuming subChapter order starts from 1

        // Add the new subchapter to the subChapters map
        subChaptersMap.put("subChapter1", newSubChapter); // Using "subChapter1" as the key

        // Set the subChapters map in the new chapter
        newChapter.setSubChapters(subChaptersMap);

        chapters.add(newChapter);
        adapter.notifyItemInserted(chapters.size() - 1); // Notify adapter about the new chapter
    }


    private void getChaptersData() {
        chapterDao.getChaptersByBookId(bookId, new DataCallback<List<Chapter>>() {
            @Override
            public void onDataReceived(List<Chapter> chaptersData) {
                chapters.clear();
                chapters.addAll(chaptersData);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                Log.e("EditChapter", "Error fetching chapters: " + e.getMessage());
            }
        });
    }

    private void saveChapters(View view) {
        chapterDao.saveChapters(bookId, chapters, new DataStatusCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(EditChapter.this, "Chapters saved successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(EditChapter.this, "Failed to save chapters: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void onClick(View v) {
        if (v.getId() == R.id.backButtonChapter) {
            finish();
        } else if (v.getId()==R.id.button_go_to_editor){
            Intent intent = new Intent(EditChapter.this, EditorSpace.class);
            intent.putExtra("bookId", bookId);
            startActivity(intent);
        }
    }


}
