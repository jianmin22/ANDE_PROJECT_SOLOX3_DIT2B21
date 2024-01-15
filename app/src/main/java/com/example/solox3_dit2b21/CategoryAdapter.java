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
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private List<Category> allCategory;
    private FirebaseStorage storage;

    private MyRecyclerViewItemClickListener mItemClickListener;
    public CategoryAdapter(List<Category> allCategory, MyRecyclerViewItemClickListener itemClickListener) {
        this.allCategory = allCategory;
        this.mItemClickListener = itemClickListener;
        storage = FirebaseStorage.getInstance();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycleritemcategorys, parent, false);

        final ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClicked(allCategory.get(viewHolder.getLayoutPosition()));
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Category category = allCategory.get(position);
        holder.bookTitle(category.getTitle());
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
        return allCategory.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView categoryImage;
        public TextView bookTitle;

        public TextView bookId;


        public ViewHolder(View itemView) {
            super(itemView);
            categoryTitle = itemView.findViewById(R.id);
            bookImage = itemView.findViewById(R.id.bookImage);
            category = itemView.findViewById(R.id.category);
            categoryId = itemView.findViewById(R.id.bookId);
        }
    }
    //RecyclerView Click Listener
    public interface MyRecyclerViewItemClickListener {
        void onItemClicked(Category category);
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
