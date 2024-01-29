// ChapterDaoImpl.java
package com.example.solox3_dit2b21.daoimpl;

import com.example.solox3_dit2b21.dao.ChapterDao;
import com.example.solox3_dit2b21.dao.DataCallback;
import com.example.solox3_dit2b21.dao.DataStatusCallback;
import com.example.solox3_dit2b21.model.Chapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseChapterDao implements ChapterDao {
    DatabaseReference chapterRef = FirebaseDatabase.getInstance().getReference("Chapter");

    public void getChaptersByBookId(String bookId, DataCallback<List<Chapter>> callback) {
        chapterRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Chapter> chapters = new ArrayList<>();
                for (DataSnapshot bookChapterSnapshot : dataSnapshot.getChildren()) {
                    String chapterBookId = bookChapterSnapshot.child("bookId").getValue(String.class);
                    if (chapterBookId != null && chapterBookId.equals(bookId)) {
                        DataSnapshot chaptersSnapshot = bookChapterSnapshot.child("chapters");
                        for (DataSnapshot chapterSnapshot : chaptersSnapshot.getChildren()) {
                            Chapter chapter = chapterSnapshot.getValue(Chapter.class);
                            if (chapter != null) {
                                chapters.add(chapter);
                            }
                        }
                        break; // Break after finding the matching bookId
                    }
                }
                callback.onDataReceived(chapters);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }


    @Override
    public void fetchChapters(String bookId, DataCallback<List<Chapter>> callback) {
        chapterRef.orderByChild("bookId").equalTo(bookId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<Chapter> chapters = new ArrayList<>();
                    for (DataSnapshot bookChapterSnapshot : dataSnapshot.getChildren()) {
                        for (DataSnapshot chapterSnapshot : bookChapterSnapshot.child("chapters").getChildren()) {
                            Chapter chapter = chapterSnapshot.getValue(Chapter.class);
                            if (chapter != null) {
                                chapters.add(chapter); // Add chapter to list
                            }
                        }
                    }
                    callback.onDataReceived(chapters);
                } else {
                    callback.onError(new Exception("No chapters available"));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    @Override
    public void saveChapters(String bookId, List<Chapter> chapters, DataStatusCallback callback) {
        chapterRef.orderByChild("bookId").equalTo(bookId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Existing chapters found, update them
                    for (DataSnapshot bookChapterSnapshot : dataSnapshot.getChildren()) {
                        String parentNodeKey = bookChapterSnapshot.getKey();
                        if (parentNodeKey != null) {
                            DatabaseReference bookChaptersRef = chapterRef.child(parentNodeKey).child("chapters");
                            bookChaptersRef.setValue(chapters)
                                    .addOnSuccessListener(aVoid -> callback.onSuccess())
                                    .addOnFailureListener(callback::onFailure);
                        } else {
                            callback.onFailure(new Exception("Parent node key is null"));
                        }
                    }
                } else {
                    // No existing chapters found, create a new entry
                    DatabaseReference newBookChapterRef = chapterRef.push(); // Create a new unique key for the chapter
                    Map<String, Object> newChapterData = new HashMap<>();
                    newChapterData.put("bookId", bookId);
                    newChapterData.put("chapters", chapters); // Assuming chapters is a list of Chapter objects

                    newBookChapterRef.setValue(newChapterData)
                            .addOnSuccessListener(aVoid -> callback.onSuccess())
                            .addOnFailureListener(callback::onFailure);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onFailure(databaseError.toException());
            }
        });
    }

}
