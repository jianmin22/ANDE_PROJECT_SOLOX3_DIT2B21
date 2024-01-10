package com.example.solox3_dit2b21;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {
    private List<Book> allBooks;
    private MyRecyclerViewItemClickListener mItemClickListener;
    public HomeAdapter(List<Book> allBooks, MyRecyclerViewItemClickListener itemClickListener) {
        this.allBooks = allBooks;
        this.mItemClickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycleritembooks, parent, false);

        final ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("RecyclerViewAdapter", "sdegonItemClicked called!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                mItemClickListener.onItemClicked(allBooks.get(viewHolder.getLayoutPosition()));
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Book book = allBooks.get(position);
        holder.bookTitle.setText(book.getTitle());
        holder.category.setText("yay");
        holder.bookId.setText(book.getBookId());

        // Set OnClickListener for bookCard

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
    //RecyclerView Click Listener
    public interface MyRecyclerViewItemClickListener {
        void onItemClicked(Book book);
    }
}
