package com.example.solox3_dit2b21.daoimpl;

import com.example.solox3_dit2b21.dao.UserFavouriteBookDao;
import com.example.solox3_dit2b21.dao.DataCallback;
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
}
