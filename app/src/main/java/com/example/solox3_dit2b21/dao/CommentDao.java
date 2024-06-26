package com.example.solox3_dit2b21.dao;

import com.example.solox3_dit2b21.model.Comment;
import java.util.List;

public interface CommentDao {
    void loadLatest2Comments(String bookId, DataCallback<List<Comment>> callback);
    void addComment(Comment comment, DataStatusCallback callback);
    void getTotalCommentsReceived(String userId, DataCallback callback);
    void getUserComments(String userId, DataCallback callback);
    void deleteComment(String commentId, DataStatusCallback callback);
}
