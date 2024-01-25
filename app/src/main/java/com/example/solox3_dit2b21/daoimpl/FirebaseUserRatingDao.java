package com.example.solox3_dit2b21.daoimpl;

import android.util.Log;

import com.example.solox3_dit2b21.dao.DataCallback;
import com.example.solox3_dit2b21.dao.DataStatusCallback;
import com.example.solox3_dit2b21.dao.UserRatingDao;
import com.example.solox3_dit2b21.model.UserRating;
import com.google.firebase.database.*;

import java.util.List;

public class FirebaseUserRatingDao implements UserRatingDao {
    DatabaseReference userRatingRef = FirebaseDatabase.getInstance().getReference("UserRating");

    @Override
    public void loadUserRatingForBook(String bookId, DataCallback<Double> callback) {
        Query userRatingQuery = userRatingRef.orderByChild("bookId").equalTo(bookId);

        userRatingQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot userRatingSnapshot) {
                if (userRatingSnapshot.exists()) {
                    double sumOfRating = 0;
                    int countOfRating = 0;
                    for (DataSnapshot ratingSnapshot : userRatingSnapshot.getChildren()) {
                        Double rating = ratingSnapshot.child("rating").getValue(Double.class);
                        if (rating != null) {
                            countOfRating++;
                            sumOfRating += rating;
                        }
                    }
                    double averageRating = countOfRating > 0 ? sumOfRating / countOfRating : 0;
                    callback.onDataReceived(averageRating);
                } else {
                    callback.onDataReceived(0.0);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    @Override
    public void deleteUserRating(String bookId, String userId, DataStatusCallback callback) {
        Query deleteQuery = userRatingRef.orderByChild("bookId").equalTo(bookId);
        deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserRating userRating = snapshot.getValue(UserRating.class);
                    if (userRating != null && userRating.getUserId().equals(userId)) {
                        snapshot.getRef().removeValue()
                                .addOnSuccessListener(aVoid -> callback.onSuccess())
                                .addOnFailureListener(callback::onFailure);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onFailure(databaseError.toException());
            }
        });
    }

    @Override
    public void updateUserRating(String bookId, String userId, double newRating, DataStatusCallback callback) {
        Query updateQuery = userRatingRef.orderByChild("bookId").equalTo(bookId);
        updateQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserRating userRating = snapshot.getValue(UserRating.class);
                    if (userRating != null && userRating.getUserId().equals(userId)) {
                        snapshot.getRef().child("rating").setValue(newRating)
                                .addOnSuccessListener(aVoid -> callback.onSuccess())
                                .addOnFailureListener(callback::onFailure);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onFailure(databaseError.toException());
            }
        });
    }

    @Override
    public void insertUserRating(UserRating userRating, DataStatusCallback callback) {
        userRatingRef.push().setValue(userRating)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    @Override
    public void getAverageUserRating(DataCallback callback, String userId) {
        DatabaseReference userRatingRef = FirebaseDatabase.getInstance().getReference("UserRating");

        FirebaseBookDao bookDao = new FirebaseBookDao();

        bookDao.getUserBookIds(new DataCallback<List<String>>() {
            @Override
            public void onDataReceived(List<String> userBookIds) {
                if (userBookIds.size() == 0) callback.onDataReceived(0.0);

                final Double[] totalRatingValue = {0.0};
                final Integer[] totalRatingsReceived = {0};
                for (String bookId : userBookIds) {

                    Log.d("bookId", bookId);

                    // Now, query the "Comment" table to get the comments for each book
                    Query query = userRatingRef.orderByChild("bookId").equalTo(bookId);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot ratingSnapshot : dataSnapshot.getChildren()) {
                                    UserRating rating = ratingSnapshot.getValue(UserRating.class);
                                    if (rating != null) {
                                        Log.d("rating", String.valueOf(rating.getRating()));
                                        totalRatingValue[0] += rating.getRating();
                                        totalRatingsReceived[0]++;
                                    }
                                }
                                // Notify the callback after processing all books
                                callback.onDataReceived(totalRatingValue[0] / totalRatingsReceived[0]);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Handle errors
                            callback.onError(databaseError.toException());
                        }
                    });
                }
            }

            @Override
            public void onError(Exception exception) {
                callback.onError(exception);
            }
        }, userId);
    }
}
