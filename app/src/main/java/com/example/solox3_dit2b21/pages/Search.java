package com.example.solox3_dit2b21.pages;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.solox3_dit2b21.R;
import com.example.solox3_dit2b21.Utils.CurrentDateUtils;
import com.example.solox3_dit2b21.model.SearchHistory;
import com.google.android.flexbox.FlexboxLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class Search extends AppCompatActivity implements View.OnClickListener {

    private FlexboxLayout flexboxLayout;
    private EditText mainSearchField;
    private String userId = "user1";
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Initialize your FlexboxLayout
        flexboxLayout = findViewById(R.id.flexboxLayout3);
        mainSearchField = findViewById(R.id.mainSearchField);

        mainSearchField.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    insertOrUpdateSearchHistory(mainSearchField.getText().toString().trim());
                    return true;
                }
                return false;
            }
        });

        try {
            DatabaseReference ref = database.getReference("SearchHistory");
            Query searchHistoryQuery = ref.orderByChild("userId").equalTo(userId);

            searchHistoryQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Clear existing buttons
                    flexboxLayout.removeAllViews();

                    // Iterate through the search history data
                    List<SearchHistory> searchHistoryList = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        SearchHistory searchHistoryItem = snapshot.getValue(SearchHistory.class);
                        searchHistoryList.add(searchHistoryItem);
                    }

                    // Sort the search history by date (latest to oldest)
                    Collections.sort(searchHistoryList, new Comparator<SearchHistory>() {
                        @Override
                        public int compare(SearchHistory o1, SearchHistory o2) {
                            // Assuming the date is in ISO 8601 format
                            return o2.getLastSearch().compareTo(o1.getLastSearch());
                        }
                    });

                    for (SearchHistory item : searchHistoryList) {
                        View customButtonView = LayoutInflater.from(Search.this).inflate(R.layout.searchhistorybutton, null);
                        TextView buttonText = customButtonView.findViewById(R.id.buttonText);
                        ImageView removeIcon = customButtonView.findViewById(R.id.removeIcon);

                        buttonText.setTag(item.getSearchId());
                        removeIcon.setTag(item.getSearchId());
                        buttonText.setText(item.getSearch());
                        FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(
                                FlexboxLayout.LayoutParams.WRAP_CONTENT,
                                FlexboxLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams.setMargins(10, 10, 10, 10);
                        customButtonView.setLayoutParams(layoutParams);

                        // Add the button to FlexboxLayout
                        flexboxLayout.addView(customButtonView);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle errors
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void insertOrUpdateSearchHistory(String search) {
        try {
            if (search.length() != 0) {
                final String userId = "user1";
                DatabaseReference searchHistoryRef = FirebaseDatabase.getInstance().getReference("SearchHistory");
                Query searchQuery = searchHistoryRef.orderByChild("search").equalTo(search);

                searchQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String currentDateTime = CurrentDateUtils.getCurrentDateTime();
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String searchId = snapshot.getKey();
                                updateLastSearch(searchId, currentDateTime);
                            }
                        } else {
                            String searchHistoryId = generateUUID();
                            insertNewSearchHistory(searchHistoryId, userId, search, currentDateTime);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("NOT FOUND", "Insert or update search history failed");
                    }
                });
                Intent intent = new Intent(Search.this, SearchFilterResults.class);
                intent.putExtra("search", mainSearchField.getText().toString().trim());
                String searchOrder="1";
                String filterOrder="2";
                Bundle getData = getIntent().getExtras();
                if (getData != null) {
                    String filter = getData.getString("filter");
                    String filterOrderPassed = getData.getString("filterOrder");
                    if(filter != null){
                        intent.putExtra("filter", filter);
                        if(filterOrderPassed.equals("1")){
                            filterOrder="1";
                            searchOrder="2";
                        }
                    }
                }
                intent.putExtra("searchOrder",searchOrder);
                intent.putExtra("filterOrder", filterOrder);
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void updateLastSearch(String searchId, String lastSearch) {
        try {
            DatabaseReference searchHistoryRef = FirebaseDatabase.getInstance().getReference("SearchHistory").child(searchId);
            searchHistoryRef.child("lastSearch").setValue(lastSearch);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertNewSearchHistory(String searchHistoryId, String userId, String search, String lastSearch) {
        try {
            DatabaseReference searchHistoryRef = FirebaseDatabase.getInstance().getReference("SearchHistory");

            SearchHistory searchHistoryItem = new SearchHistory(searchHistoryId, userId, search, lastSearch);

            searchHistoryRef.child(searchHistoryId).setValue(searchHistoryItem);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String generateUUID() {
        // Generate a unique searchHistoryId using UUID
        return UUID.randomUUID().toString();
    }
    @Override
    public void onClick(View v){
        if(v.getId() == R.id.searchButton) {
            insertOrUpdateSearchHistory(mainSearchField.getText().toString().trim());
        }else if (v.getId() == R.id.back){
                finish();
        }else if (v.getId() == R.id.buttonText) {
            TextView buttonText = v.findViewById(R.id.buttonText);

            String search = buttonText.getText().toString();

            insertOrUpdateSearchHistory(search);
        } else if (v.getId() == R.id.removeIcon) {
                String searchId = (String) v.getTag();

                DatabaseReference ref = database.getReference("SearchHistory");

                DatabaseReference searchIdRef = ref.child(searchId);

                searchIdRef.removeValue();
            }else if (v.getId()==R.id.filterButton){
                Intent intent = new Intent(Search.this, CategoryPage.class);
                startActivity(intent);
        }

    }
}

