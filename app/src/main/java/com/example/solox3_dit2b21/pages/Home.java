package com.example.solox3_dit2b21.pages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.solox3_dit2b21.Utils.AuthUtils;
import com.example.solox3_dit2b21.dao.DataCallback;
import com.example.solox3_dit2b21.daoimpl.FirebaseBookDao;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.solox3_dit2b21.model.Book;
import com.example.solox3_dit2b21.R;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity implements View.OnClickListener {
    private BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerView1;
    private RecyclerView recyclerView2;
    HomeAdapter adapter1;
    HomeAdapter adapter2;
    private List<Book> booksPopular = new ArrayList<>();
    private List<Book> booksLatest = new ArrayList<>();

    private FirebaseBookDao bookDao = new FirebaseBookDao();
    @Override
    protected void onStart() {
        super.onStart();
        AuthUtils.redirectToLoginIfNotAuthenticated(this);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        bindDataForPopular();
        bindDataForLatest();
        setUIRef();
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_bookshelf) {
                Intent intent = new Intent(Home.this, EditorSpace.class);
                intent.putExtra("bookId", "f28f342c-753b-4dcd-830f-a8849078f5cb");
                startActivity(intent);
                // Navigate to Bookshelf activity
            } else if (itemId == R.id.navigation_home) {
                // Already in Home, no action needed
            } else if (itemId == R.id.navigation_category) {
                // Navigate to Category activity
                Intent intent = new Intent(Home.this, CategoryPage.class);
                startActivity(intent);
            } else if (itemId == R.id.navigation_profile) {
                Intent intent = new Intent(Home.this, AuthorBookDetails.class);
                intent.putExtra("bookId", "book1");
                startActivity(intent);
                // Navigate to Profile activity
//                Intent intent = new Intent(Home.this, AuthorEditBookDetails.class);
//                intent.putExtra("bookId", "book1");
//                startActivity(intent);
                // Navigate to Profile activity
//                Intent intent = new Intent(Home.this, Profile.class);
//                startActivity(intent);
            }

            return true;
        });

        // To select a default item (e.g., home) when the activity starts
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
    }


    private void setUIRef()
    {
        recyclerView1 = findViewById(R.id.recycler_view_HomePopular);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView1.setLayoutManager(layoutManager1);

        recyclerView2 = findViewById(R.id.recycler_view_HomeLatest);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView2.setLayoutManager(layoutManager2);


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
        bookDao.getPopularBooks(new DataCallback<List<Book>>() {
            @Override
            public void onDataReceived(List<Book> books) {
                booksPopular.clear();
                booksPopular.addAll(books);
                adapter1.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception exception) {
                Log.e("bindDataForPopular", "Error fetching popular books", exception);
                Toast.makeText(Home.this, "Error fetching popular data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bindDataForLatest() {
        bookDao.getLatestBooks(new DataCallback<List<Book>>() {
            @Override
            public void onDataReceived(List<Book> books) {
                booksLatest.clear();
                booksLatest.addAll(books);
                adapter2.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception exception) {
                Log.e("bindDataForLatest", "Error fetching latest books", exception);
                Toast.makeText(Home.this, "Error fetching latest books.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v){
        if(v.getId() == R.id.mainSearchField) {
            Intent intent = new Intent(Home.this, Search.class);
            startActivity(intent);
        }
    }


}