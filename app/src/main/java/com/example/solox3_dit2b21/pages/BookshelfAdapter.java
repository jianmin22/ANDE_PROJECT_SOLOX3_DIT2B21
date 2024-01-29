package com.example.solox3_dit2b21.pages;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.solox3_dit2b21.R;
import com.example.solox3_dit2b21.Utils.LoadImageURL;
import com.example.solox3_dit2b21.dao.CategoryDao;
import com.example.solox3_dit2b21.dao.DataCallback;
import com.example.solox3_dit2b21.daoimpl.FirebaseCategoryDao;
import com.example.solox3_dit2b21.model.Book;
import com.example.solox3_dit2b21.model.Category;

import java.util.List;

public class BookshelfAdapter extends RecyclerView.Adapter<BookshelfAdapter.ViewHolder> {
    private List<Book> books;
    private CategoryDao categoryDao = new FirebaseCategoryDao();
    private boolean isFromReadingHistory;

    private MyRecyclerViewItemClickListener mItemClickListener;
    public BookshelfAdapter(List<Book> books,boolean isFromReadingHistory, MyRecyclerViewItemClickListener itemClickListener) {
        this.books = books;
        this.isFromReadingHistory = isFromReadingHistory;
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
                mItemClickListener.onItemClicked(books.get(viewHolder.getLayoutPosition()));
            }
        });
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Book book = books.get(position);
        holder.bookTitle.setText(book.getTitle());
        LoadImageURL.loadImageURL(book.getImage(), holder.bookImage);
        holder.bookId.setText(book.getBookId());

        categoryDao.loadBookCategory(book.getCategoryId(), new DataCallback<Category>() {
            @Override
            public void onDataReceived(Category category) {
                if (category != null) {
                    holder.category.setText(category.getCategoryName());
                }
            }

            @Override
            public void onError(Exception exception) {
                Log.e("Firebase", "Error fetching category data", exception);
            }
        });
        holder.itemView.setOnClickListener(v -> {
            if (isFromReadingHistory) {
                // If from reading history, go straight to the reading page
                mItemClickListener.onReadingHistoryItemClicked(book);
            } else {
                // Otherwise, go to the book detail page
                mItemClickListener.onItemClicked(book);
            }
        });
    }

    @Override
    public int getItemCount() {
        return books.size();
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
        void onReadingHistoryItemClicked(Book book);
    }


}
