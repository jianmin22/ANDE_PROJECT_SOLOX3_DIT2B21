package com.example.solox3_dit2b21.pages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.solox3_dit2b21.Utils.AuthUtils;
import com.example.solox3_dit2b21.dao.DataCallback;
import com.example.solox3_dit2b21.daoimpl.FirebaseCommentDao;
import com.example.solox3_dit2b21.daoimpl.FirebaseUserDao;
import com.example.solox3_dit2b21.model.Comment;
import com.example.solox3_dit2b21.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class CommentHistory extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CommentHistoryAdapter commentHistoryAdapter;
    private List<Comment> userComments = new ArrayList<>();
    FirebaseAuth auth;
    FirebaseUser user;
    private String username;
    private FirebaseUserDao userDao = new FirebaseUserDao();

    private FirebaseCommentDao commentDao = new FirebaseCommentDao();
    @Override
    protected void onStart() {
        super.onStart();
        AuthUtils.redirectToLoginIfNotAuthenticated(this);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_comments);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        String userId = user.getUid();
        recyclerView = findViewById(R.id.recycler_view_all_comments);

        bindDataForUsername(userId);
        bindDataForUserComments(userId);
//        setUIRef();
    }

    private void setUIRef() {


        Toast.makeText(this, "username: " + username, Toast.LENGTH_SHORT).show();
//        commentHistoryAdapter = new CommentHistoryAdapter(this, username, userComments);
//        recyclerView.setAdapter(commentHistoryAdapter);
    }

    private void bindDataForUsername(String userId) {
        userDao.getUsername(userId, new DataCallback<String>() {
            @Override
            public void onDataReceived(String userName) {
                if (userName != null) {
                    username = userName;
                } else {
                    username = "";
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(CommentHistory.this));
                commentHistoryAdapter = new CommentHistoryAdapter(CommentHistory.this, username, userComments);
                recyclerView.setAdapter(commentHistoryAdapter);
                commentHistoryAdapter.notifyDataSetChanged();
                Toast.makeText(CommentHistory.this, "username2: " + username, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Exception exception) {
                Log.e("bindDataForUsername", "Error fetching username", exception);
            }
        });
    }

    private void bindDataForUserComments(String userId) {
        commentDao.getUserComments(userId, new DataCallback<List<Comment>>() {
            @Override
            public void onDataReceived(List<Comment> comments) {
                userComments.clear();
                userComments.addAll(comments);
                commentHistoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception exception) {
                Log.e("bindDataForUserComments", "Error fetching user's comments", exception);
                Toast.makeText(CommentHistory.this, "Error fetching user's comments.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
