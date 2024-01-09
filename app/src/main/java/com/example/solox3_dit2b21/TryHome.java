package com.example.solox3_dit2b21;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TryHome extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerView;
    private HomeAdapter adapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);
        recyclerView = findViewById(R.id.recycler_view_TryHome);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Book> books = new ArrayList<>();

        // Create Date instances for publishedDate, createdDate, and lastUpdated
        Date currentDate = new Date();

        // Adding three Book instances to the list
        Book book1 = new Book("bookId1", "Doraemon Adventure", "Exciting adventures of Doraemon",
                "categoryId1", 4.5, "@mipmap/doraemonbook", currentDate, currentDate, currentDate);

        Book book2 = new Book("bookId2", "Doraemon's Time Travel", "Time-travel with Doraemon and friends",
                "categoryId1", 4.7, "@mipmap/doraemonbook", currentDate, currentDate, currentDate);

        Book book3 = new Book("bookId3", "Doraemon's Inventions", "Fun with Doraemon's gadgets",
                "categoryId1", 4.2, "@mipmap/doraemonbook", currentDate, currentDate, currentDate);

        // Add the books to the list
        books.add(book1);
        books.add(book2);
        books.add(book3);

        adapter = new HomeAdapter(books);
        recyclerView.setAdapter(adapter);
    }
    @Override
    public void onClick(View v){
        if(v.getId() == R.id.bookCard){
            Intent i = new Intent(this, TryHome.class);
            startActivity(i);
        }
    }
}
