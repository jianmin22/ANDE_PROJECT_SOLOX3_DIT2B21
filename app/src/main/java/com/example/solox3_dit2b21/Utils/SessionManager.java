package com.example.solox3_dit2b21.Utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.solox3_dit2b21.pages.Login;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class SessionManager {
    private FirebaseAuth.AuthStateListener mAuthListener;
    public SessionManager (final Context context) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = auth.getCurrentUser();
                Log.d("User: ", user.toString());
                if (user == null) {
                    Intent intent = new Intent(context, Login.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);
                }
            }
        };

//        auth.addAuthStateListener(mAuthListener);
    }

    public void addAuthStateListener(FirebaseAuth firebaseAuth) {
        firebaseAuth.addAuthStateListener(mAuthListener);
    }

    public void removeAuthStateListener(FirebaseAuth firebaseAuth) {
        firebaseAuth.removeAuthStateListener(mAuthListener);
    }

}
