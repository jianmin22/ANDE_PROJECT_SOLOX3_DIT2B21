package com.example.solox3_dit2b21.daoimpl;

import com.example.solox3_dit2b21.dao.CommentDao;
import com.example.solox3_dit2b21.dao.DataCallback;
import com.example.solox3_dit2b21.model.Comment;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FirebaseCommentDao implements CommentDao {
    @Override
    public void loadLatest2Comments(String bookId, DataCallback<List<Comment>> callback) {
        DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference("Comment");
        Query commentsQuery = commentsRef.orderByChild("bookId").equalTo(bookId);

        commentsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot commentSnapshot) {
                if (commentSnapshot.exists()) {
                    List<Comment> commentsList = new ArrayList<>();
                    for (DataSnapshot commentData : commentSnapshot.getChildren()) {
                        Comment comment = commentData.getValue(Comment.class);
                        commentsList.add(comment);
                    }
                    Collections.sort(commentsList, (c1, c2) -> c2.getDate().compareTo(c1.getDate()));
                    callback.onDataReceived(commentsList);
                } else {
                    callback.onDataReceived(Collections.emptyList());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

}
