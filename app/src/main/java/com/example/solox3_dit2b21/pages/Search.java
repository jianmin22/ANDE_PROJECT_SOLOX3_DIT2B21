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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.solox3_dit2b21.R;
import com.example.solox3_dit2b21.dao.DataCallback;
import com.example.solox3_dit2b21.dao.DataStatusCallback;
import com.example.solox3_dit2b21.dao.SearchHistoryDao;
import com.example.solox3_dit2b21.daoimpl.FirebaseSearchHistoryDao;
import com.example.solox3_dit2b21.model.SearchHistory;
import com.google.android.flexbox.FlexboxLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Collections;
import java.util.List;

public class Search extends AppCompatActivity implements View.OnClickListener {

    private FlexboxLayout flexboxLayout;
    private EditText mainSearchField;
    private String userId = "user1";

    private SearchHistoryDao searchHistoryDao = new FirebaseSearchHistoryDao();
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

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

        loadSearchHistory();
    }

    private void loadSearchHistory() {
        searchHistoryDao.getUserSearchHistory(userId, new DataCallback<List<SearchHistory>>() {
            @Override
            public String onDataReceived(List<SearchHistory> searchHistoryList) {
                displaySearchHistory(searchHistoryList);
                return null;
            }
            @Override
            public void onError(Exception exception) {
                Toast.makeText(Search.this, "Error loading search history.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displaySearchHistory(List<SearchHistory> searchHistoryList) {
        flexboxLayout.removeAllViews();
        Collections.sort(searchHistoryList, (o1, o2) -> o2.getLastSearch().compareTo(o1.getLastSearch()));

        for (SearchHistory item : searchHistoryList) {
            View customButtonView = LayoutInflater.from(this).inflate(R.layout.searchhistorybutton, null);
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

            flexboxLayout.addView(customButtonView);
        }
    }

    private void insertOrUpdateSearchHistory(String search) {
        if (!search.isEmpty()) {
            searchHistoryDao.insertOrUpdateSearchHistory(userId, search, new DataStatusCallback() {
                @Override
                public void onSuccess() {
                    navigateToSearchFilterResults(search);
                }

                @Override
                public void onFailure(Exception exception) {
                    Log.e("SearchActivity", "Failed to insert or update search history", exception);
                }
            });
        }
    }

    private void navigateToSearchFilterResults(String search) {
        Intent intent = new Intent(Search.this, SearchFilterResults.class);
        intent.putExtra("search", search);
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
        mainSearchField.setText("");
        startActivity(intent);
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

