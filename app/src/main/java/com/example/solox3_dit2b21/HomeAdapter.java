package com.example.solox3_dit2b21;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;
import com.bumptech.glide.Glide;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.FirebaseStorage;
public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {
    private List<Book> allBooks;
    private FirebaseStorage storage;

    private MyRecyclerViewItemClickListener mItemClickListener;
    public HomeAdapter(List<Book> allBooks, MyRecyclerViewItemClickListener itemClickListener) {
        this.allBooks = allBooks;
        this.mItemClickListener = itemClickListener;
        storage = FirebaseStorage.getInstance();
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
        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference().child("categories").child(book.getCategoryId());
        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Category category = dataSnapshot.getValue(Category.class);
                    if (category != null) {
                        holder.category.setText(category.getCategoryName());
                        Log.d("Firebase", "Category Reference: " + category.getCategoryId());
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error fetching category data", databaseError.toException());
            }
        });
        loadBookImage(book.getImage(), holder.bookImage);
        holder.bookId.setText(book.getBookId());
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
    private void loadBookImage(String imageUrl, ImageView imageView) {
        // Load image into ImageView using Glide
        Glide.with(imageView.getContext())
                .load(imageUrl)
//                .placeholder(R.mipmap.doraemonbook) // Optional placeholder image while loading
//                .error(R.drawable.error_image) // Optional error image if the load fails
                .into(imageView);
    }

//    private void loadBookImage(String storagePath, ImageView imageView) {
//        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//            @Override
//            public void onSuccess(Uri uri) {
//                // Load image into ImageView using Glide
//                Glide.with(imageView.getContext())
//                        .load(uri)
//                        .into(imageView);
//            }
//        });
//    }
}
