package com.example.solox3_dit2b21;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Initialize your FlexboxLayout
        flexboxLayout = findViewById(R.id.flexboxLayout3);
        mainSearchField = findViewById(R.id.MainSearchField);

        mainSearchField.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    insertNewSearchHistory(mainSearchField.getText().toString().trim());
                    return true;
                }
                return false;
            }
        });
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
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
                        Button button = (Button) getLayoutInflater().inflate(R.layout.searchhistorybutton, null);
                        button.setTag(item.getSearch());
                        button.setText(item.getSearch());
                        FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(
                                FlexboxLayout.LayoutParams.WRAP_CONTENT,
                                FlexboxLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams.setMargins(8, 8, 8, 8);
                        button.setLayoutParams(layoutParams);

                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String searchString = (String) v.getTag();
                            }
                        });

                        // Add the button to FlexboxLayout
                        flexboxLayout.addView(button);
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
    private void insertNewSearchHistory(String search) {
        try {
            if(search.length()!=0){
                // Generate a unique searchHistoryId using cuid
                String searchHistoryId = generateUUID();

                // Get a reference to the "SearchHistory" node
                DatabaseReference searchHistoryRef = FirebaseDatabase.getInstance().getReference("SearchHistory");

                // Create a new SearchHistoryItem object
                SearchHistory searchHistoryItem = new SearchHistory(searchHistoryId, "user1", search, getCurrentDateTime());

                // Push the new SearchHistoryItem to the database
                searchHistoryRef.child(searchHistoryId).setValue(searchHistoryItem);
                mainSearchField.setText("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getCurrentDateTime() {
        // Get the current date and time
        Date currentDate = new Date();

        // Create a SimpleDateFormat object with the desired format
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());

        // Format the date as a string
        return sdf.format(currentDate);
    }
    private String generateUUID() {
        // Generate a unique searchHistoryId using UUID
        return UUID.randomUUID().toString();
    }
    @Override
    public void onClick(View v){
        if(v.getId() == R.id.seachButton) {
            insertNewSearchHistory(mainSearchField.getText().toString().trim());
        }else if (v.getId() == R.id.backButton){
            Bundle getData = getIntent().getExtras();
            if (getData != null){
                String fromPage = getData.getString("from");
                Intent intent = new Intent(Search.this, fromPage.getClass());
                startActivity(intent);
            }else{
                Log.d("Back Button", "Error Occurred");
            }
        }
    }
}

