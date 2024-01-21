package com.example.solox3_dit2b21.pages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.solox3_dit2b21.R;
import com.example.solox3_dit2b21.dao.DataCallback;
import com.example.solox3_dit2b21.daoimpl.FirebaseBookDao;
import com.example.solox3_dit2b21.model.Book;

import java.util.ArrayList;
import java.util.List;

public class Profile extends AppCompatActivity {

    private RecyclerView recyclerViewProfile;
    private ProfileAdapter profileAdapter;
    private List<Book> booksProfile = new ArrayList<>();
    private FirebaseBookDao bookDao = new FirebaseBookDao();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        bindDataForProfile();
        setUIRef();
    }

    private void setUIRef()
    {
        recyclerViewProfile = findViewById(R.id.recycler_view_profile);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewProfile.setLayoutManager(layoutManager1);
        profileAdapter = new ProfileAdapter(booksProfile, new ProfileAdapter.MyRecyclerViewItemClickListener()
        {
            @Override
            public void onItemClicked(Book book)
            {
                Intent intent = new Intent(Profile.this, BookDetails.class);
                intent.putExtra("bookId", book.getBookId());
                startActivity(intent);
            }
        });

        recyclerViewProfile.setAdapter(profileAdapter);

    }



    private void bindDataForProfile() {
        bookDao.getPopularBooks(new DataCallback<List<Book>>() {
            @Override
            public void onDataReceived(List<Book> books) {
                booksProfile.clear();
                booksProfile.addAll(books);
                profileAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception exception) {
                Log.e("bindDataForPopular", "Error fetching popular books", exception);
                Toast.makeText(Profile.this, "Error fetching popular data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}