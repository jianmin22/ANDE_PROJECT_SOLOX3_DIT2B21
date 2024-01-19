package com.example.solox3_dit2b21.pages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
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

public class SearchFilterResults extends AppCompatActivity implements View.OnClickListener{

    private RecyclerView recyclerView;
    private SearchFilterResultsAdapter adapter;
    private List<Book> bookList = new ArrayList<>();
    private String search;
    private String filter;
    private String searchOrder;
    private String filterOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_filter_results);

        recyclerView = findViewById(R.id.searchAndFilterResult);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        search = getIntent().getStringExtra("search");
        filter = getIntent().getStringExtra("filter");
        searchOrder = getIntent().getStringExtra("searchOrder");
        filterOrder = getIntent().getStringExtra("filterOrder");

        if (search == null && filter == null) {
            Toast.makeText(this, "Error occurred, try again later", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (search!=null){
            TextView searchHolder = findViewById(R.id.mainSearchField);
            searchHolder.setHint(search);
        }

        fetchAndFilterBooks(search, filter, searchOrder, filterOrder);

        adapter = new SearchFilterResultsAdapter(this, bookList);
        recyclerView.setAdapter(adapter);
    }

    private void fetchAndFilterBooks(String search, String filter, String searchOrder, String filterOrder) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Book");

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bookList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Book book = snapshot.getValue(Book.class);
                    if (book != null) {
                        if (search != null && filter != null) {
                            if (searchOrder.equals("1") && filterOrder.equals("2")) {
                                if (Boolean.parseBoolean(book.getIsPublished()) && book.getTitle().contains(search) && matchesFilter(book.getCategoryId(), filter)) {
                                    bookList.add(book);
                                }
                            } else if (searchOrder.equals("2") && filterOrder.equals("1")) {
                                if (Boolean.parseBoolean(book.getIsPublished()) && matchesFilter(book.getCategoryId(), filter) && book.getTitle().contains(search)) {
                                    bookList.add(book);
                                }
                            }
                        } else if (search == null && filter != null) {
                            if (Boolean.parseBoolean(book.getIsPublished()) && matchesFilter(book.getCategoryId(), filter)) {
                                bookList.add(book);
                            }
                        } else if (search != null) {
                            if (Boolean.parseBoolean(book.getIsPublished()) && book.getTitle().contains(search)) {
                                bookList.add(book);
                            }
                        }

                    }
                }
                TextView resultsFoundView = findViewById(R.id.resultsFound);
                resultsFoundView.setText(bookList.size() + " Results Found");
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

    private void navigateToSearch(){
        Intent intent = new Intent(SearchFilterResults.this, Search.class);
        if(filter!=null){
            intent.putExtra("filter", filter);
        }
        intent.putExtra("searchOrder",searchOrder);
        intent.putExtra("filterOrder",filterOrder);
        startActivity(intent);
    }

    private void navigateToFilter(){
        Intent intent = new Intent(SearchFilterResults.this, CategoryPage.class);
        if(search!=null){
            intent.putExtra("search", search);
        }
        intent.putExtra("filter",filter);
        intent.putExtra("searchOrder",searchOrder);
        intent.putExtra("filterOrder",filterOrder);
        startActivity(intent);
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

    @Override
    public void onClick(View v){
        if(v.getId() == R.id.mainSearchField) {
            navigateToSearch();
        }else if(v.getId()==R.id.back){
            finish();
        }
    }
}
