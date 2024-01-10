package com.example.solox3_dit2b21;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {
    private List<Book> allBooks;
    Context context;
    public HomeAdapter(List<Book> allBooks, Context context) {
        this.allBooks = allBooks;
        this.context=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycleritembooks, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Book book = allBooks.get(position);
        holder.bookTitle.setText(book.getTitle());
        holder.category.setText("yay");
        holder.bookId.setText(book.getBookId());

        // Set OnClickListener for bookCard
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle item click
                Intent intent = new Intent(context, BookDetails.class);
                // Pass the book ID to the BookDetails activity
                intent.putExtra("bookId", book.getBookId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return allBooks.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView bookImage;
        public TextView bookTitle;
        public TextView category;

        public TextView bookId;

        public ViewHolder(View itemView) {
            super(itemView);
            bookTitle = itemView.findViewById(R.id.bookTitle);
            bookImage = itemView.findViewById(R.id.bookImage);
            category = itemView.findViewById(R.id.category);
            bookId = itemView.findViewById(R.id.bookId);
        }
    }
}
