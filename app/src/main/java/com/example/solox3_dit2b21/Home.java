package com.example.solox3_dit2b21;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Home extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerView1;
    private RecyclerView recyclerView2;
    HomeAdapter adapter1;
    HomeAdapter adapter2;
    private List<Book> booksPopular = new ArrayList<>();
    private List<Book> booksLatest = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        bindDataForPopular();
        bindDataForLatest();
        setUIRef();
    }

    private void setUIRef()
    {
        recyclerView1 = findViewById(R.id.recycler_view_HomePopular);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView1.setLayoutManager(layoutManager1);

        recyclerView2 = findViewById(R.id.recycler_view_HomeLatest);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView2.setLayoutManager(layoutManager2);


        // Create separate adapters for each RecyclerView
        adapter1 = new HomeAdapter(booksPopular, new HomeAdapter.MyRecyclerViewItemClickListener()
        {
            @Override
            public void onItemClicked(Book book)
            {
                Intent intent = new Intent(Home.this, BookDetails.class);
                intent.putExtra("bookId", book.getBookId());
                startActivity(intent);
            }
        });
        adapter2 = new HomeAdapter(booksLatest, new HomeAdapter.MyRecyclerViewItemClickListener()
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

    private void bindDataForPopular() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        try {
            DatabaseReference ref = database.getReference("Book");

            Log.d("Firebase", "Books Reference: " + ref.toString());
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    booksPopular.clear();

                    // Create a list to hold the books with their reads count
                    List<Book> booksWithReads = new ArrayList<>();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Book book = snapshot.getValue(Book.class);

                        if (book != null) {
                            booksWithReads.add(book);
                        }

                        Log.d("Firebase", "Books Reference:nnn " + book.getBookId());
                    }

                    // Sort the list based on reads in descending order
                    Collections.sort(booksWithReads, new Comparator<Book>() {
                        @Override
                        public int compare(Book book1, Book book2) {
                            return Integer.compare(book2.getNumberOfReads(), book1.getNumberOfReads());
                        }
                    });

                    // Display only the top 5 books
                    for (int i = 0; i < Math.min(5, booksWithReads.size()); i++) {
                        booksPopular.add(booksWithReads.get(i));
                    }

                    adapter1.notifyDataSetChanged();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bindDataForLatest() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        try {
            DatabaseReference ref = database.getReference("Book");

            Log.d("Firebase", "Books Reference: " + ref.toString());
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    booksLatest.clear();

                    // Create a list to hold the books with their published date
                    List<Book> booksWithPublishedDate = new ArrayList<>();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Book book = snapshot.getValue(Book.class);

                        if (book != null) {
                            booksWithPublishedDate.add(book);
                        }

                        Log.d("Firebase", "Books Reference:nnn " + book.getBookId());
                    }

                    // Sort the list based on published date in descending order
                    Collections.sort(booksWithPublishedDate, new Comparator<Book>() {
                        @Override
                        public int compare(Book book1, Book book2) {
                            return book2.getPublishedDate().compareTo(book1.getPublishedDate());
                        }
                    });

                    // Display only the latest 5 books
                    for (int i = 0; i < Math.min(5, booksWithPublishedDate.size()); i++) {
                        booksLatest.add(booksWithPublishedDate.get(i));
                    }

                    adapter2.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v){
        if(v.getId() == R.id.MainSearchField) {
            Intent intent = new Intent(Home.this, Search.class);
            intent.putExtra("from", "Home");
            startActivity(intent);
        }
    }


}