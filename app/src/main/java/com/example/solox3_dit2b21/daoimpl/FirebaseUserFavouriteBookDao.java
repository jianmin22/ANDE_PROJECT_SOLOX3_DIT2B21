package com.example.solox3_dit2b21.daoimpl;

import com.example.solox3_dit2b21.dao.DataStatusCallback;
import com.example.solox3_dit2b21.dao.UserFavouriteBookDao;
import com.example.solox3_dit2b21.dao.DataCallback;
import com.example.solox3_dit2b21.model.UserFavouriteBook;
import com.google.firebase.database.*;

public class FirebaseUserFavouriteBookDao implements UserFavouriteBookDao {
    @Override
    public void loadNumberOfUserFavouriteBook(String bookId, DataCallback<Integer> callback) {
        DatabaseReference userFavouriteRef = FirebaseDatabase.getInstance().getReference("UserFavouriteBook");
        Query userFavouriteQuery = userFavouriteRef.orderByChild("bookId").equalTo(bookId);
        userFavouriteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot userFavouriteSnapshot) {
                if (userFavouriteSnapshot.exists()) {
                    int count = (int) userFavouriteSnapshot.getChildrenCount();
                    callback.onDataReceived(count);
                } else {
                    callback.onDataReceived(0);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    @Override
    public void deleteUserFavourite(String bookId, String userId, DataStatusCallback callback) {
        DatabaseReference userFavouriteRef = FirebaseDatabase.getInstance().getReference("UserFavouriteBook");
        Query deleteQuery = userFavouriteRef.orderByChild("bookId").equalTo(bookId);

        deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserFavouriteBook userFavouriteBook = snapshot.getValue(UserFavouriteBook.class);
                    if (userFavouriteBook != null && userFavouriteBook.getUserId().equals(userId)) {
                        snapshot.getRef().removeValue()
                                .addOnSuccessListener(aVoid -> callback.onSuccess())
                                .addOnFailureListener(e -> callback.onFailure(e));
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
    public void insertUserFavourite(UserFavouriteBook userFavouriteBook, DataStatusCallback callback) {
        DatabaseReference userFavouriteRef = FirebaseDatabase.getInstance().getReference("UserFavouriteBook");
        userFavouriteRef.push().setValue(userFavouriteBook)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }
}
