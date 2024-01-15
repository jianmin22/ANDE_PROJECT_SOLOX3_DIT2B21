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
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Home extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth auth;
    FirebaseUser user;
    private RecyclerView recyclerView1;
    private RecyclerView recyclerView2;
    HomeAdapter adapter1;
    HomeAdapter adapter2;
private List<Book> books = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }
        user.

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
        adapter1 = new HomeAdapter(books, new HomeAdapter.MyRecyclerViewItemClickListener()
        {
            @Override
            public void onItemClicked(Book book)
            {
                Intent intent = new Intent(Home.this, BookDetails.class);
                intent.putExtra("bookId", book.getBookId());
                startActivity(intent);
            }
        });
        adapter2 = new HomeAdapter(books, new HomeAdapter.MyRecyclerViewItemClickListener()
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

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        try {

            DatabaseReference ref = database.getReference("Book");

            Log.d("Firebase", "Books Reference: " + ref.toString());
            ref.addValueEventListener(new ValueEventListener() {
                @Override

                public void onDataChange(DataSnapshot dataSnapshot) {
                    books.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Book book = snapshot.getValue(Book.class);
                        books.add(book);
                        Log.d("Firebase", "Books Reference:nnn " + book.getBookId());
                    }
                    adapter1.notifyDataSetChanged();
                    adapter2.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onClick(View v){
    }
}