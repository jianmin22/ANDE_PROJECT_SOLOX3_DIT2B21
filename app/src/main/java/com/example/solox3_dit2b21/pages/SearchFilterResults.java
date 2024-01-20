package com.example.solox3_dit2b21.pages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.solox3_dit2b21.R;
import com.example.solox3_dit2b21.dao.BookDao;
import com.example.solox3_dit2b21.dao.DataCallback;
import com.example.solox3_dit2b21.daoimpl.FirebaseBookDao;
import com.example.solox3_dit2b21.model.Book;
import com.example.solox3_dit2b21.model.Category;
import com.example.solox3_dit2b21.model.SearchHistory;
import com.google.android.flexbox.FlexboxLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchFilterResults extends AppCompatActivity implements View.OnClickListener{
    private ValueEventListener eventListener;
    private RecyclerView recyclerView;
    private SearchFilterResultsAdapter adapter;
    private List<Book> bookList = new ArrayList<>();
    private String search;
    private String filter;
    private String searchOrder;
    private String filterOrder;
    private BookDao bookDao = new FirebaseBookDao();
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
        List<String> filterList;
        if (filter != null) {
            String[] filters = filter.split(",");
            filterList = new ArrayList<>(Arrays.asList(filters));
            FlexboxLayout flexboxLayout = findViewById(R.id.flexboxLayout3);

            for (String filterItem : filterList) {
                DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference().child("Category").child(filterItem);
                View customButtonView = LayoutInflater.from(this).inflate(R.layout.searchhistorybutton, flexboxLayout, false);
                TextView buttonText = customButtonView.findViewById(R.id.buttonText);
                ImageView removeIcon = customButtonView.findViewById(R.id.removeIcon);

                // Define layout params for the filter tag
                FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(
                        FlexboxLayout.LayoutParams.WRAP_CONTENT,
                        FlexboxLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(10, 10, 10, 10);
                customButtonView.setLayoutParams(layoutParams);

                // Set click listener for the remove icon, if needed
                // Set click listener for the remove icon, if needed
                removeIcon.setOnClickListener(v -> {
                    // Remove the filter item from the list
                    filterList.remove(filterItem);
                    // Remove the customButtonView from the FlexboxLayout
                    flexboxLayout.removeView(customButtonView);
                    // Update the filter string by joining the list back into a string
                    filter = TextUtils.join(",", filterList);

                    // Refresh the search and filter results
                    fetchAndFilterBooks(search, filter, searchOrder, filterOrder); // Call the method to refresh results

                    // You may also want to update some other UI elements or logic to reflect the filter removal
                    // ...
                });

                // Fetch the category name using the category ID
                categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Check if the dataSnapshot contains data
                        if (dataSnapshot.exists()) {
                            // Assuming the category name is stored under a "name" field
                            Category category = dataSnapshot.getValue(Category.class);
                            buttonText.setText(category.getCategoryName() != null ? category.getCategoryName() : "Unknown Category");
                        } else {
                            buttonText.setText("Unknown Category");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("TAG", "loadCategory:onCancelled", databaseError.toException());
                        buttonText.setText("Error");
                    }
                });

                // Add the custom layout to the FlexboxLayout
                flexboxLayout.addView(customButtonView);
            }
        } else {
            filterList = new ArrayList<>();
        }


        fetchAndFilterBooks(search, filter, searchOrder, filterOrder);

        adapter = new SearchFilterResultsAdapter(this, bookList);
        recyclerView.setAdapter(adapter);
    }


    private void fetchAndFilterBooks(String search, String filter, String searchOrder, String filterOrder) {
        bookDao.fetchAndFilterBooks(search, filter, searchOrder, filterOrder, new DataCallback<List<Book>>() {
            @Override
            public void onDataReceived(List<Book> books) {
                bookList.clear();
                bookList.addAll(books);
                TextView resultsFoundView = findViewById(R.id.resultsFound);
                resultsFoundView.setText(bookList.size() + " Results Found");
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception exception) {
                Toast.makeText(SearchFilterResults.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
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

    @Override
    public void onClick(View v){
        if(v.getId() == R.id.mainSearchField) {
            navigateToSearch();
        }else if(v.getId() == R.id.fillteredIcon){
            navigateToFilter();
        }
        else if(v.getId()==R.id.back){
            finish();
        }
    }
}
