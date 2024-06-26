package com.example.solox3_dit2b21.daoimpl;

import android.util.Log;

import com.example.solox3_dit2b21.dao.CommentDao;
import com.example.solox3_dit2b21.dao.DataCallback;
import com.example.solox3_dit2b21.dao.DataStatusCallback;
import com.example.solox3_dit2b21.model.Comment;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FirebaseCommentDao implements CommentDao {
    DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference("Comment");

    @Override
    public void loadLatest2Comments(String bookId, DataCallback<List<Comment>> callback) {
        Query commentsQuery = commentsRef.orderByChild("bookId").equalTo(bookId);

        commentsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot commentSnapshot) {
                if (commentSnapshot.exists()) {
                    List<Comment> commentsList = new ArrayList<>();
                    for (DataSnapshot commentData : commentSnapshot.getChildren()) {
                        Comment comment = commentData.getValue(Comment.class);
                        if (comment != null) {
                            commentsList.add(comment);
                        }
                    }
                    Collections.sort(commentsList, (c1, c2) ->
                            c2.getDate().compareTo(c1.getDate()));
                    if (commentsList.size() > 2) {
                        commentsList = commentsList.subList(0, 2);
                    }
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


    @Override
    public void addComment(Comment comment, DataStatusCallback callback) {
        commentsRef.child(comment.getCommentId()).setValue(comment)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    @Override
    public void getTotalCommentsReceived(String userId, DataCallback callback) {
        FirebaseBookDao bookDao = new FirebaseBookDao();

       bookDao.getUserBookIds(new DataCallback<List<String>>() {
           @Override
           public void onDataReceived(List<String> userBookIds) {
               if (userBookIds.size() == 0) callback.onDataReceived(0);

               final Integer[] totalComments = {0};
               for (String bookId : userBookIds) {
                   // Now, query the "Comment" table to get the comments for each book
                   Query query = commentsRef.orderByChild("bookId").equalTo(bookId);
                   query.addListenerForSingleValueEvent(new ValueEventListener() {
                       @Override
                       public void onDataChange(DataSnapshot dataSnapshot) {
                           // Count the comments for each book
                           totalComments[0] += (int) dataSnapshot.getChildrenCount();

                           // Notify the callback after processing all books
                           callback.onDataReceived(totalComments[0]);
                       }

                       @Override
                       public void onCancelled(DatabaseError databaseError) {
                           // Handle errors
                           callback.onError(databaseError.toException());
                       }
                   });
               }
           }

           @Override
           public void onError(Exception exception) {
               callback.onError(exception);
           }
       }, userId);
    }

    @Override
    public void getUserComments(String userId, DataCallback callback) {
        commentsRef.orderByChild("userId").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Comment> userComments = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Comment comment = snapshot.getValue(Comment.class);
                    if (comment != null) {
                        userComments.add(comment);
                    }
                }

                // Sort comments by date, assuming date is a string in "yyyy-MM-dd HH:mm:ss" format
                Collections.sort(userComments, (comment1, comment2) ->
                        comment2.getDate().compareTo(comment1.getDate()));

                callback.onDataReceived(userComments);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
                callback.onError(databaseError.toException());
            }
        });
    }

    @Override
    public void deleteComment(String commentId, DataStatusCallback callback) {
        commentsRef.child(commentId).removeValue()
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }
}
