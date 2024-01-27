package com.example.solox3_dit2b21.daoimpl;

import com.example.solox3_dit2b21.dao.DataCallback;
import com.example.solox3_dit2b21.dao.ReadingHistoryDao;
import com.example.solox3_dit2b21.model.Chapter;
import com.example.solox3_dit2b21.model.SubChapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class FirebaseReadingHistoryDao implements ReadingHistoryDao {

    @Override
    public void updateOrCreateReadingHistory(String userId, String bookId, Chapter currentChapter, SubChapter currentSubChapter, String lastRead, DataCallback<Boolean> callback) {
        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference("ReadingHistory");

        historyRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean historyExists = false;
                String existingHistoryId = null;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String fetchedBookId = snapshot.child("bookId").getValue(String.class);
                    if (bookId.equals(fetchedBookId)) {
                        historyExists = true;
                        existingHistoryId = snapshot.getKey();
                        break;
                    }
                }

                Map<String, Object> historyData = new HashMap<>();
                historyData.put("bookId", bookId);
                historyData.put("chapterOrder", currentChapter.getChapterOrder());
                historyData.put("subChapterOrder", currentSubChapter.getSubChapterOrder());
                historyData.put("lastRead", lastRead);
                historyData.put("userId", userId);

                if (historyExists && existingHistoryId != null) {
                    historyRef.child(existingHistoryId).updateChildren(historyData)
                            .addOnSuccessListener(aVoid -> callback.onDataReceived(true))
                            .addOnFailureListener(e -> callback.onError(e));
                } else {
                    historyRef.push().setValue(historyData)
                            .addOnSuccessListener(aVoid -> callback.onDataReceived(true))
                            .addOnFailureListener(e -> callback.onError(e));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(new Exception(databaseError.getMessage()));
            }
        });
    }
}

