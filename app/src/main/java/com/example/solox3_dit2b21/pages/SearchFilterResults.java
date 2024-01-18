package com.example.solox3_dit2b21.pages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.solox3_dit2b21.R;
import com.example.solox3_dit2b21.model.Book;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchFilterResults extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SearchFilterResultsAdapter adapter;
    private List<Book> bookList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_filter_results);

        recyclerView = findViewById(R.id.searchAndFilterResult);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String search = getIntent().getStringExtra("search");
        String filter = getIntent().getStringExtra("filter");
        String searchOrder = getIntent().getStringExtra("searchOrder");
        String filterOrder = getIntent().getStringExtra("filterOrder");

        if (search == null && filter == null) {
            Toast.makeText(this, "Error occurred, try again later", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        fetchAndFilterBooks(search, filter, searchOrder, filterOrder);

        adapter = new SearchFilterResultsAdapter(this, bookList);
        recyclerView.setAdapter(adapter);
    }

    private void fetchAndFilterBooks(String search, String filter, String searchOrder, String filterOrder) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Books");

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bookList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Book book = snapshot.getValue(Book.class);
                    if (book != null) {
                        if (search != null && filter != null) {
                            if (searchOrder.equals("1") && filterOrder.equals("2")) {
                                if (book.getTitle().contains(search) && matchesFilter(book.getCategoryId(), filter)) {
                                    bookList.add(book);
                                    Log.d("book with id",book.getBookId());
                                }
                            } else if (searchOrder.equals("2") && filterOrder.equals("1")) {
                                if (matchesFilter(book.getCategoryId(), filter) && book.getTitle().contains(search)) {
                                    bookList.add(book);
                                    Log.d("book with id",book.getBookId());
                                }
                            }
                        } else if (search == null && filter != null) {
                            if (matchesFilter(book.getCategoryId(), filter)) {
                                bookList.add(book);
                                Log.d("book with id",book.getBookId());
                            }
                        } else if (search != null) {
                            if (book.getTitle().contains(search)) {
                                bookList.add(book);
                                Log.d("book with id",book.getBookId());
                            }
                        }

                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SearchFilterResults.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                finish();
            }
        };

        databaseReference.addListenerForSingleValueEvent(eventListener);
    }

    private boolean matchesFilter(String categoryId, String filter) {
        String[] filters = filter.split(",");
        for (String f : filters) {
            if (categoryId.equals(f.trim())) {
                return true;
            }
        }
        return false;
    }

}
