package com.example.solox3_dit2b21.pages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.solox3_dit2b21.R;
import com.example.solox3_dit2b21.model.Chapter;

import java.util.ArrayList;
import java.util.List;

public class EditChapter extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EditChaptersAdapter adapter;
    private List<Chapter> chapters; // Assume this is populated with your data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_chapter);

        recyclerView = findViewById(R.id.recyclerView_edit_chapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize your chapters list with actual data
        chapters = getChaptersData();

        adapter = new EditChaptersAdapter(this, chapters);
        recyclerView.setAdapter(adapter);
    }

    private List<Chapter> getChaptersData() {
        // Fetch or create your chapters data here
        return new ArrayList<>();
    }
}
