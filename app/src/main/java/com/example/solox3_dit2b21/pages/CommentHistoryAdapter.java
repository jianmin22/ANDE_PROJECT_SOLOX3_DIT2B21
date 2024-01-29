package com.example.solox3_dit2b21.pages;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.solox3_dit2b21.Utils.FormatDateUtils;
import com.example.solox3_dit2b21.R;
import com.example.solox3_dit2b21.dao.DataStatusCallback;
import com.example.solox3_dit2b21.daoimpl.FirebaseCommentDao;
import com.example.solox3_dit2b21.model.Comment;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class CommentHistoryAdapter extends RecyclerView.Adapter<CommentHistoryAdapter.CommentsViewHolder> {

    private Context context;
    private String username;
    private List<Comment> commentList;

    private FirebaseCommentDao commentDao = new FirebaseCommentDao();

    public CommentHistoryAdapter(Context context, String username, List<Comment> commentList) {
        this.context = context;
        this.username = username;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleritemcommenthistory, parent, false);
        return new CommentsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsViewHolder holder, int position) {
        Comment comment = commentList.get(position);

        // Set data to views
        holder.usernameTextView.setText(username);
        holder.commentTextView.setText(comment.getCommentText());
        // Format and set the timestamp
        String formattedDate = FormatDateUtils.formatDateString(comment.getDate());
        holder.timestampTextView.setText(formattedDate);

        holder.deleteCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentDao.deleteComment(comment.getCommentId(), new DataStatusCallback() {

                    @Override
                    public void onSuccess() {
                        Toast.makeText(context, "Comment deleted successfully", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        Log.e("Delete Comment Failed", exception.getMessage());
                        Toast.makeText(context, "Failed to delete comment!", Toast.LENGTH_SHORT).show();
                    }
                });
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
        ImageView deleteCommentButton;
        CommentsViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize views
            usernameTextView = itemView.findViewById(R.id.username);
            commentTextView = itemView.findViewById(R.id.comments);
            timestampTextView = itemView.findViewById(R.id.timestamp);
            deleteCommentButton = itemView.findViewById(R.id.deleteCommentButton);
        }
    }
}


