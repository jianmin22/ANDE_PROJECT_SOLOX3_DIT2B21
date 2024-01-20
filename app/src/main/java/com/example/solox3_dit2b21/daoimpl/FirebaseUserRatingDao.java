package com.example.solox3_dit2b21.daoimpl;

import com.example.solox3_dit2b21.dao.DataCallback;
import com.example.solox3_dit2b21.dao.UserRatingDao;
import com.google.firebase.database.*;

public class FirebaseUserRatingDao implements UserRatingDao {
    @Override
    public void loadUserRatingForBook(String bookId, DataCallback<Double> callback) {
        DatabaseReference userRatingRef = FirebaseDatabase.getInstance().getReference("UserRating");
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
}
