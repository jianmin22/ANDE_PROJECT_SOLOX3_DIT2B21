package com.example.solox3_dit2b21.daoimpl;

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
                        if (comment != null) {
                            commentsList.add(comment);
                        }
                    }
                    Collections.sort(commentsList, new Comparator<Comment>() {
                        @Override
                        public int compare(Comment c1, Comment c2) {
                            return c2.getDate().compareTo(c1.getDate());
                        }
                    });
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
        DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference("Comment");
        commentsRef.child(comment.getCommentId()).setValue(comment)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    @Override
    public void getTotalUserComments(DataCallback callback, String userId) {
        DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference("Comment");

//        FirebaseBookDao bookDao = new FirebaseBookDao();
//
//       List<String> userBookIds = bookDao.getUserBooksId(new DataCallback<List<String>>() {
//           @Override
//           public String onDataReceived(List<String> userBookIds) {
//               Integer totalComments = 0;
//
//               for (String bookId : userBookIds) {
//                   // Now, query the "Comment" table to get the comments for each book
//                   DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference("Comment")
//                           .orderByChild("bookId")
//                           .equalTo(bookId);
//
//                   commentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                       @Override
//                       public void onDataChange(DataSnapshot dataSnapshot) {
//                           // Count the comments for each book
//                           totalComments += (int) dataSnapshot.getChildrenCount();
//
//                           // Notify the callback after processing all books
//                           callback.onDataReceived(totalComments);
//                       }
//
//                       @Override
//                       public void onCancelled(DatabaseError databaseError) {
//                           // Handle errors
//                           callback.onError(databaseError.toException());
//                       }
//                   });
//               }
//           }
//       });

    }
}
