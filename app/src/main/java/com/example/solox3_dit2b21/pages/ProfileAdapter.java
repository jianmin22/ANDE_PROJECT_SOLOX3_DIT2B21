package com.example.solox3_dit2b21.pages;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.solox3_dit2b21.R;
import com.example.solox3_dit2b21.Utils.LoadImageURL;
import com.example.solox3_dit2b21.dao.BookDao;
import com.example.solox3_dit2b21.dao.CategoryDao;
import com.example.solox3_dit2b21.dao.DataCallback;
import com.example.solox3_dit2b21.dao.DataStatusCallback;
import com.example.solox3_dit2b21.daoimpl.FirebaseBookDao;
import com.example.solox3_dit2b21.daoimpl.FirebaseCategoryDao;
import com.example.solox3_dit2b21.model.Book;
import com.example.solox3_dit2b21.model.Category;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {
    private List<Book> userBooks;
    private Context context;
    private BookDao bookDao = new FirebaseBookDao();
    private CategoryDao categoryDao = new FirebaseCategoryDao();


    private MyRecyclerViewItemClickListener mItemClickListener;
    public ProfileAdapter(List<Book> userBooks, MyRecyclerViewItemClickListener itemClickListener, Context context) {
        this.userBooks = userBooks;
        this.mItemClickListener = itemClickListener;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.profile_recycleritembooks, parent, false);

        final ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClicked(userBooks.get(viewHolder.getLayoutPosition()));
            }
        });
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Book book = userBooks.get(position);
        holder.bookTitle.setText(book.getTitle());
        LoadImageURL.loadImageURL(book.getImage(), holder.bookImage);
        holder.bookId.setText(book.getBookId());
        String publish = "PUBLISH";
        if (Boolean.parseBoolean(book.getIsPublished())) publish = "UNPUBLISH";
        holder.publishButton.setText(publish);

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

        String finalPublish = publish.toLowerCase();
        holder.publishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookDao.updateBookIsPublished(book, new DataStatusCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(context, "Book " + finalPublish + "ed successfully", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        Log.e("Update Book Failed", exception.getMessage());
                        Toast.makeText(context, "Failed to " + finalPublish + "!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return userBooks.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView bookImage;
        public TextView bookTitle, category, bookId;
        public Button publishButton;

        public ViewHolder(View itemView) {
            super(itemView);
            bookTitle = itemView.findViewById(R.id.bookTitle);
            bookImage = itemView.findViewById(R.id.bookImage);
            category = itemView.findViewById(R.id.category);
            bookId = itemView.findViewById(R.id.bookId);
            publishButton = itemView.findViewById(R.id.publishButton);
        }
    }
    //RecyclerView Click Listener
    public interface MyRecyclerViewItemClickListener {
        void onItemClicked(Book book);
    }


}
