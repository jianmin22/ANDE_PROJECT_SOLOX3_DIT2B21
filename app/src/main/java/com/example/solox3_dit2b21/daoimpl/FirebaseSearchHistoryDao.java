package com.example.solox3_dit2b21.daoimpl;

import android.util.Log;

import com.example.solox3_dit2b21.Utils.CurrentDateUtils;
import com.example.solox3_dit2b21.dao.DataCallback;
import com.example.solox3_dit2b21.dao.SearchHistoryDao;
import com.example.solox3_dit2b21.dao.DataStatusCallback;
import com.example.solox3_dit2b21.model.SearchHistory;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FirebaseSearchHistoryDao implements SearchHistoryDao {

    @Override
    public void getUserSearchHistory(String userId, DataCallback<List<SearchHistory>> callback) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("SearchHistory");
        Query searchHistoryQuery = ref.orderByChild("userId").equalTo(userId);

        searchHistoryQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<SearchHistory> searchHistoryList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    SearchHistory searchHistoryItem = snapshot.getValue(SearchHistory.class);
                    if (searchHistoryItem != null) {
                        searchHistoryList.add(searchHistoryItem);
                    }
                }
                callback.onDataReceived(searchHistoryList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    @Override
    public void insertOrUpdateSearchHistory(String userId, String search, DataStatusCallback callback) {
        DatabaseReference searchHistoryRef = FirebaseDatabase.getInstance().getReference("SearchHistory");
        Query searchQuery = searchHistoryRef.orderByChild("search").equalTo(search);

        searchQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String currentDateTime = CurrentDateUtils.getCurrentDateTime();
                boolean updateMade = false;

                if (dataSnapshot.exists()) {
                    // Update existing search history for the specific user
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        SearchHistory searchHistory = snapshot.getValue(SearchHistory.class);
                        if (searchHistory != null && searchHistory.getUserId().equals(userId)) {
                            snapshot.getRef().child("lastSearch").setValue(currentDateTime)
                                    .addOnSuccessListener(aVoid -> callback.onSuccess())
                                    .addOnFailureListener(e -> callback.onFailure(e));
                            updateMade = true;
                            break;
                        }
                    }
                }

                if (!updateMade) {
                    // Insert new search history
                    String searchHistoryId = generateUUID();
                    SearchHistory newSearchHistory = new SearchHistory(searchHistoryId, userId, search, currentDateTime);
                    searchHistoryRef.child(searchHistoryId).setValue(newSearchHistory)
                            .addOnSuccessListener(aVoid -> callback.onSuccess())
                            .addOnFailureListener(e -> callback.onFailure(e));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onFailure(databaseError.toException());
            }
        });
    }

    private String generateUUID() {
        return FirebaseDatabase.getInstance().getReference().push().getKey();
    }
}


