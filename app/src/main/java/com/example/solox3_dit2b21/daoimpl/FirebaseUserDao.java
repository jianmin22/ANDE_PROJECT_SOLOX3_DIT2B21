package com.example.solox3_dit2b21.daoimpl;

import android.util.Log;

import com.example.solox3_dit2b21.dao.DataCallback;
import com.example.solox3_dit2b21.dao.DataStatusCallback;
import com.example.solox3_dit2b21.dao.UserDao;
import com.example.solox3_dit2b21.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class FirebaseUserDao implements UserDao {
    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User");

    @Override
    public void getUser(String userId, DataCallback<User> callback) {
        Query userQuery = userRef.orderByChild("userId").equalTo(userId);

        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot userSnapshot = dataSnapshot.getChildren().iterator().next();
                User user = userSnapshot.getValue(User.class);

                callback.onDataReceived(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    @Override
    public void getUsername(String userId, DataCallback<String> callback) {
        Query userQuery = userRef.orderByChild("userId").equalTo(userId);

        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot userSnapshot = dataSnapshot.getChildren().iterator().next();
                User user = userSnapshot.getValue(User.class);
                Log.d("user", "data change");

                if (user != null) {
                    Log.d("user", user.getUsername());
                    callback.onDataReceived(user.getUsername());
                } else {
                    Log.d("user", null);
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
    public void insertUser(User user, DataStatusCallback callback) {
        userRef.child(user.getUserId()).setValue(user)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    @Override
    public void updateUser(User user, DataStatusCallback callback) {
        Map<String, Object> userUpdate = user.toMap();
        userRef.child(user.getUserId()).updateChildren(userUpdate)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }
}
