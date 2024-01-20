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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

import com.google.firebase.storage.FirebaseStorage;
public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {
    private List<Book> allBooks;
    private CategoryDao categoryDao = new FirebaseCategoryDao();


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
                mItemClickListener.onItemClicked(allBooks.get(viewHolder.getLayoutPosition()));
            }
        });
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Book book = allBooks.get(position);
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
