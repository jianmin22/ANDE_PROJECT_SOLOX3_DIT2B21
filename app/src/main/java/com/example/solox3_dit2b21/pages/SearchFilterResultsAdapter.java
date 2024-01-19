package com.example.solox3_dit2b21.pages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.solox3_dit2b21.R;
import com.example.solox3_dit2b21.model.Book;
import com.example.solox3_dit2b21.model.Category;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class SearchFilterResultsAdapter extends RecyclerView.Adapter<SearchFilterResultsAdapter.ViewHolder> {

    private Context context;
    private List<Book> bookList;

    public SearchFilterResultsAdapter(Context context, List<Book> bookList) {
        this.context = context;
        this.bookList = bookList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.searchandfilterbookitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Book book = bookList.get(position);
        holder.bookTitle.setText(book.getTitle());
        loadBookImage(book.getImage(), holder.bookImage);
        holder.bookAuthor.setText(book.getAuthorId());
        holder.bookDescription.setText(book.getDescription());
        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference().child("Category").child(book.getCategoryId());
        holder.viewBookDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BookDetails.class);
                intent.putExtra("bookId", book.getBookId());
                context.startActivity(intent);
            }
        });
        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@org.checkerframework.checker.nullness.qual.NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Category category = dataSnapshot.getValue(Category.class);
                    if (category != null) {
                        holder.bookCategoryButton.setText(category.getCategoryName());
                        Log.d("Firebase", "Category Reference: " + category.getCategoryId());
                    }
                }
            }
            @Override
            public void onCancelled(@org.checkerframework.checker.nullness.qual.NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error fetching category data", databaseError.toException());
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView bookTitle;
        ImageView bookImage;
        TextView bookAuthor;
        TextView bookDescription;
        Button bookCategoryButton;
        LinearLayout viewBookDetails;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bookImage = itemView.findViewById(R.id.bookCoverImage);
            bookTitle = itemView.findViewById(R.id.bookTitle);
            bookAuthor = itemView.findViewById(R.id.bookAuthor);
            bookDescription = itemView.findViewById(R.id.bookDescription);
            bookCategoryButton = itemView.findViewById(R.id.bookCategoryButton);
            viewBookDetails = itemView.findViewById(R.id.viewBookDetails);
        }
    }

    private void loadBookImage(String imageUrl, ImageView imageView) {
        // Load image into ImageView using Glide
        Glide.with(imageView.getContext())
                .load(imageUrl)
                .into(imageView);
    }
}
