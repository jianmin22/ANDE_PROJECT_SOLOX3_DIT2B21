package com.example.solox3_dit2b21.pages;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.solox3_dit2b21.Utils.FormatDateUtils;
import com.example.solox3_dit2b21.R;
import com.example.solox3_dit2b21.Utils.LoadImageURL;
import com.example.solox3_dit2b21.dao.UserDao;
import com.example.solox3_dit2b21.daoimpl.FirebaseUserDao;
import com.example.solox3_dit2b21.model.Comment;

import java.util.List;
import com.example.solox3_dit2b21.dao.DataCallback;
import com.example.solox3_dit2b21.model.User;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentsViewHolder> {

    private List<Comment> commentList;
    private UserDao userDao = new FirebaseUserDao();
    public CommentAdapter(List<Comment> commentList) {
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleritemcomments, parent, false);
        return new CommentsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        userDao.getUser(comment.getUserId(), new DataCallback<User>() {
            @Override
            public void onDataReceived(User userData) {
                if (userData != null) {
                    String userName = userData.getUsername();
                    holder.usernameTextView.setText(userName);
                    holder.commentTextView.setText(comment.getCommentText());
                    String formattedDate = FormatDateUtils.formatDateString(comment.getDate());
                    holder.timestampTextView.setText(formattedDate);
                    String profilepic = userData.getProfilePic();
                    if (profilepic == null || profilepic.isEmpty()) {
                        holder.currentProfilePic.setImageResource(R.drawable.empty_profile_pic);
                    } else {
                        LoadImageURL.loadImageURL(profilepic, holder.currentProfilePic);
                    }

                } else {
                    throw new Error("User not received");
                }
            }

            @Override
            public void onError(Exception exception) {
                Log.e("getUser", "Error fetching user", exception);
            }
        });

    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    static class CommentsViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView;
        TextView commentTextView;
        TextView timestampTextView;
        ImageView currentProfilePic;
        CommentsViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize views
            currentProfilePic=itemView.findViewById(R.id.profilePicComment);
            usernameTextView = itemView.findViewById(R.id.username);
            commentTextView = itemView.findViewById(R.id.comments);
            timestampTextView = itemView.findViewById(R.id.timestamp);
        }
    }
}

