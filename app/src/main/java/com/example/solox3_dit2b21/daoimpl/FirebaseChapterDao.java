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
import java.util.List;

public class FirebaseChapterDao implements ChapterDao {
    DatabaseReference chapterRef = FirebaseDatabase.getInstance().getReference("Chapter");


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
        chapterRef.orderByChild("bookId").equalTo(bookId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot bookChapterSnapshot : dataSnapshot.getChildren()) {
                                String parentNodeKey = bookChapterSnapshot.getKey();
                                if (parentNodeKey != null) {
                                    DatabaseReference bookChaptersRef = chapterRef.child(parentNodeKey).child("chapters");
                                    bookChaptersRef.setValue(chapters)
                                            .addOnSuccessListener(aVoid -> callback.onSuccess())
                                            .addOnFailureListener(callback::onFailure);
                                } else {
                                    // Handle the case where the parentNodeKey is null
                                    callback.onFailure(new Exception("Parent node key is null"));
                                }
                            }
                        } else {
                            // No data present for the given bookId, might want to handle this case separately
                            callback.onFailure(new Exception("No data present for the given bookId"));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        callback.onFailure(databaseError.toException());
                    }
                }
        );
    }
}
