package com.example.solox3_dit2b21;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Home extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerView1;
    private RecyclerView recyclerView2;
private List<Book> books = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        bindData();
        setUIRef();

    }

    private void setUIRef()
    {
        recyclerView1 = findViewById(R.id.recycler_view_HomePopular);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView1.setLayoutManager(layoutManager1);

        recyclerView2 = findViewById(R.id.recycler_view_HomeRecommendation);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView2.setLayoutManager(layoutManager2);


        // Create separate adapters for each RecyclerView
        HomeAdapter adapter1 = new HomeAdapter(books, new HomeAdapter.MyRecyclerViewItemClickListener()
        {
            @Override
            public void onItemClicked(Book book)
            {
                Intent intent = new Intent(Home.this, BookDetails.class);
                intent.putExtra("bookId", book.getBookId());
                startActivity(intent);
            }
        });
        HomeAdapter adapter2 = new HomeAdapter(books, new HomeAdapter.MyRecyclerViewItemClickListener()
        {
            @Override
            public void onItemClicked(Book book)
            {
                Intent intent = new Intent(Home.this, BookDetails.class);
                intent.putExtra("bookId", book.getBookId());
                startActivity(intent);
            }
        });

        recyclerView1.setAdapter(adapter1);
        recyclerView2.setAdapter(adapter2);

    }

    private void bindData()
    {
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
    }
    @Override
    public void onClick(View v){
    }
}