package com.example.solox3_dit2b21.daoimpl;

import android.util.Log;

import com.example.solox3_dit2b21.dao.BookDao;
import com.example.solox3_dit2b21.dao.DataCallback;
import com.example.solox3_dit2b21.dao.DataStatusCallback;
import com.example.solox3_dit2b21.model.Book;
import com.example.solox3_dit2b21.model.BookWithReadingHistory;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class FirebaseBookDao implements BookDao {
    DatabaseReference bookRef = FirebaseDatabase.getInstance().getReference("Book");


    @Override
    public void getPopularBooks(final DataCallback callback) {
        bookRef.addValueEventListener(new ValueEventListener() {
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
        bookRef.addValueEventListener(new ValueEventListener() {
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
    public void getUserBooks(final DataCallback callback, String userId, Boolean published) {
        Query userBooksQuery = bookRef.orderByChild("authorId").equalTo(userId);

        userBooksQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Book> userBooks = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Book book = snapshot.getValue(Book.class);

                    if (book != null && book.getAuthorId().toString().equals(userId) && Boolean.parseBoolean(book.getIsPublished()) == published) {
                        userBooks.add(book);
                    }
                }

                if (published) {
                    Collections.sort(userBooks, (book1, book2) ->
                            book2.getPublishedDate().compareTo(book1.getPublishedDate()));
                } else {
                    Collections.sort(userBooks, (book1, book2) ->
                            book2.getLastUpdated().compareTo(book1.getLastUpdated()));
                }

                callback.onDataReceived(userBooks);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    @Override
    public void getUserFavouriteBooks(DataCallback callback, String userId) {
        FirebaseUserFavouriteBookDao userFavouriteBookDao = new FirebaseUserFavouriteBookDao();

        userFavouriteBookDao.getUserFavouriteBookIds(userId, new DataCallback<List<String>>() {
            @Override
            public void onDataReceived(List<String> userBookIds) {
                bookRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<Book> userFavouriteBooks = new ArrayList<>();

                        for (DataSnapshot bookSnapshot : dataSnapshot.getChildren()) {
                            Book book = bookSnapshot.getValue(Book.class);
                            if (book != null && userBookIds.contains(book.getBookId()) && Boolean.parseBoolean(book.getIsPublished())) {
                                userFavouriteBooks.add(book);
                            }
                        }

                        Collections.sort(userFavouriteBooks, (book1, book2) ->
                                book2.getLastUpdated().compareTo(book1.getLastUpdated()));

                        callback.onDataReceived(userFavouriteBooks);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        callback.onError(databaseError.toException());
                    }
                });
            }

            @Override
            public void onError(Exception exception) {
                callback.onError(exception);
            }
        });
    }

    @Override
    public void getUserBookIds(final DataCallback callback, String userId) {
        Query userBooksQuery = bookRef.orderByChild("authorId").equalTo(userId);

        userBooksQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> userBookIds = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Book book = snapshot.getValue(Book.class);
                    if (book != null && Boolean.parseBoolean(book.getIsPublished())) {
                        userBookIds.add(book.getBookId());
                    }
                }

                callback.onDataReceived(userBookIds);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    @Override
    public void loadBookDetailsById(String bookId, DataCallback<Book> callback) {
        DatabaseReference bookIdRef = bookRef.child(bookId);
        bookIdRef.addListenerForSingleValueEvent(new ValueEventListener() {
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
    public void fetchSearchAndFilterBooks(String search, String filter, String searchOrder, String filterOrder, DataCallback<List<Book>> callback) {
        bookRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Book> bookList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Book book = snapshot.getValue(Book.class);
                    if (book != null) {
                        if (search != null && filter != null) {
                            if (searchOrder.equals("1") && filterOrder.equals("2")) {
                                if (Boolean.parseBoolean(book.getIsPublished()) && book.getTitle().toLowerCase().contains(search.toLowerCase()) && matchesFilter(book.getCategoryId(), filter)) {
                                    bookList.add(book);
                                }
                            } else if (searchOrder.equals("2") && filterOrder.equals("1")) {
                                if (Boolean.parseBoolean(book.getIsPublished()) && matchesFilter(book.getCategoryId(), filter) && book.getTitle().toLowerCase().contains(search.toLowerCase())) {
                                    bookList.add(book);
                                }
                            }
                        } else if (search == null && filter != null) {
                            if (Boolean.parseBoolean(book.getIsPublished()) && matchesFilter(book.getCategoryId(), filter)) {
                                bookList.add(book);
                            }
                        } else if (search != null) {
                            if (Boolean.parseBoolean(book.getIsPublished()) && book.getTitle().toLowerCase().contains(search.toLowerCase())) {
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

    @Override
    public void insertBook(Book book, DataStatusCallback callback) {
        bookRef.child(book.getBookId()).setValue(book)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    @Override
    public void updateBookDetails(Book book, DataStatusCallback callback) {
        Map<String, Object> bookUpdates = book.toMap();
        bookRef.child(book.getBookId()).updateChildren(bookUpdates)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    @Override
    public void updateBookIsPublished(Book book, DataStatusCallback callback) {
        Map<String, Object> bookUpdates = book.toMap();
        String published = String.valueOf(!Boolean.parseBoolean(book.getIsPublished()));
        bookUpdates.put("isPublished", published);
        if (Boolean.parseBoolean(published)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
            dateFormat.setTimeZone(Calendar.getInstance().getTimeZone());
            bookUpdates.put("publishedDate", dateFormat.format(new Date()));
        }
        bookRef.child(book.getBookId()).updateChildren(bookUpdates)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
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

    @Override
    public void getTotalUserPublished(final DataCallback callback, String userId) {
        Query query = bookRef.orderByChild("authorId").equalTo(userId);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer totalPublished = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Book book = snapshot.getValue(Book.class);

//                    check if book has been published
                    if (book != null && Boolean.parseBoolean(book.getIsPublished())) {
                        totalPublished++;
                    }
                }

//                if no need to check for additional conditions,
//                Integer totalPublished = (int) dataSnapshot.getChildrenCount();

                callback.onDataReceived(totalPublished);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    @Override
    public void getUserReadingHistoryBooks(String userId, DataCallback<List<BookWithReadingHistory>> callback) {
        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference("ReadingHistory");
        DatabaseReference bookRef = FirebaseDatabase.getInstance().getReference("Book");

        historyRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Integer> bookIdToChapterOrderMap = new HashMap<>();
                Map<String, Integer> bookIdToSubChapterOrderMap = new HashMap<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String bookId = snapshot.child("bookId").getValue(String.class);
                    Integer chapterOrder = snapshot.child("chapterOrder").getValue(Integer.class);
                    Integer subChapterOrder = snapshot.child("subChapterOrder").getValue(Integer.class);

                    if (bookId != null) {
                        bookIdToChapterOrderMap.put(bookId, chapterOrder);
                        bookIdToSubChapterOrderMap.put(bookId, subChapterOrder);
                    }
                }

                List<BookWithReadingHistory> booksWithHistory = new ArrayList<>();
                AtomicInteger booksFetched = new AtomicInteger();
                bookIdToChapterOrderMap.forEach((bookId, chapterOrder) -> {
                    bookRef.child(bookId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot bookSnapshot) {
                            Book book = bookSnapshot.getValue(Book.class);
                            Integer subChapterOrder = bookIdToSubChapterOrderMap.get(bookId);

                            if (book != null) {
                                booksWithHistory.add(new BookWithReadingHistory(book, chapterOrder, subChapterOrder));
                                if (booksFetched.incrementAndGet() == bookIdToChapterOrderMap.size()) {
                                    callback.onDataReceived(booksWithHistory);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e("FirebaseBookDao", "Failed to fetch book details: " + databaseError.getMessage());
                        }
                    });
                });

                if (bookIdToChapterOrderMap.isEmpty()) {
                    callback.onDataReceived(booksWithHistory); // Return empty list if no reading history found
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseBookDao", "Failed to fetch reading history: " + databaseError.getMessage());
                callback.onError(databaseError.toException());
            }
        });
    }



}

