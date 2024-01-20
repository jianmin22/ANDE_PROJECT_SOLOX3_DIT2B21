package com.example.solox3_dit2b21.daoimpl;

import com.example.solox3_dit2b21.dao.BookDao;
import com.example.solox3_dit2b21.dao.DataCallback;
import com.example.solox3_dit2b21.model.Book;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FirebaseBookDao implements BookDao {
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    public void getPopularBooks(final DataCallback callback) {
        DatabaseReference ref = database.getReference("Book");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Book> booksWithReads = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Book book = snapshot.getValue(Book.class);

                    if (book != null && Boolean.parseBoolean(book.getIsPublished())) {
                        booksWithReads.add(book);
                    }
                }

                Collections.sort(booksWithReads, (book1, book2) ->
                        Integer.compare(book2.getNumberOfReads(), book1.getNumberOfReads()));

                callback.onDataReceived(booksWithReads.subList(0, Math.min(5, booksWithReads.size())));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    @Override
    public void getLatestBooks(final DataCallback callback) {
        DatabaseReference ref = database.getReference("Book");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Book> booksWithPublishedDate = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Book book = snapshot.getValue(Book.class);

                    if (book != null && Boolean.parseBoolean(book.getIsPublished())) {
                        booksWithPublishedDate.add(book);
                    }
                }

                Collections.sort(booksWithPublishedDate, (book1, book2) ->
                        book2.getPublishedDate().compareTo(book1.getPublishedDate()));

                callback.onDataReceived(booksWithPublishedDate.subList(0, Math.min(5, booksWithPublishedDate.size())));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    @Override
    public void loadBookDetailsById(String bookId, DataCallback<Book> callback) {
        DatabaseReference bookRef = FirebaseDatabase.getInstance().getReference().child("Book").child(bookId);
        bookRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Book book = dataSnapshot.getValue(Book.class);
                    callback.onDataReceived(book);
                } else {
                    callback.onDataReceived(null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }
    @Override
    public void fetchAndFilterBooks(String search, String filter, String searchOrder, String filterOrder, DataCallback<List<Book>> callback) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Book");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Book> bookList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Book book = snapshot.getValue(Book.class);
                    if (book != null) {
                        if (search != null && filter != null) {
                            if (searchOrder.equals("1") && filterOrder.equals("2")) {
                                if (Boolean.parseBoolean(book.getIsPublished()) && book.getTitle().contains(search) && matchesFilter(book.getCategoryId(), filter)) {
                                    bookList.add(book);
                                }
                            } else if (searchOrder.equals("2") && filterOrder.equals("1")) {
                                if (Boolean.parseBoolean(book.getIsPublished()) && matchesFilter(book.getCategoryId(), filter) && book.getTitle().contains(search)) {
                                    bookList.add(book);
                                }
                            }
                        } else if (search == null && filter != null) {
                            if (Boolean.parseBoolean(book.getIsPublished()) && matchesFilter(book.getCategoryId(), filter)) {
                                bookList.add(book);
                            }
                        } else if (search != null) {
                            if (Boolean.parseBoolean(book.getIsPublished()) && book.getTitle().contains(search)) {
                                bookList.add(book);
                            }
                        }

                    }
                }
                callback.onDataReceived(bookList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    private boolean matchesFilter(String categoryId, String filter) {
        String[] filters = filter.split(",");
        for (String f : filters) {
            if (categoryId.equals(f.trim())) {
                return true;
            }
        }
        return false;
    }

}

