package com.example.solox3_dit2b21.pages;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.solox3_dit2b21.Utils.FormatDateUtils;
import com.example.solox3_dit2b21.R;
import com.example.solox3_dit2b21.dao.UserDao;
import com.example.solox3_dit2b21.daoimpl.FirebaseUserDao;
import com.example.solox3_dit2b21.model.Comment;

import java.util.List;
import com.example.solox3_dit2b21.dao.DataCallback;
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
        userDao.getUsername(comment.getUserId(), new DataCallback<String>() {
            @Override
            public void onDataReceived(String userName) {
                if (userName != null) {
                    holder.usernameTextView.setText(userName);
                            holder.commentTextView.setText(comment.getCommentText());
            // Format and set the timestamp
        String formattedDate = FormatDateUtils.formatDateString(comment.getDate());
        holder.timestampTextView.setText(formattedDate);
                } else {
                    holder.usernameTextView.setText("");
                }
            }

            @Override
            public void onError(Exception exception) {
                holder.usernameTextView.setText("");
            }
        });
        // Set data to views
//        holder.usernameTextView.setText(comment.getUserId());
//        holder.commentTextView.setText(comment.getCommentText());
//        // Format and set the timestamp
//        String formattedDate = FormatDateUtils.formatDateString(comment.getDate());
//        holder.timestampTextView.setText(formattedDate);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    static class CommentsViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView;
        TextView commentTextView;
        TextView timestampTextView;

        CommentsViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize views
            usernameTextView = itemView.findViewById(R.id.username);
            commentTextView = itemView.findViewById(R.id.comments);
            timestampTextView = itemView.findViewById(R.id.timestamp);
        }
    }
}

