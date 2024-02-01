package com.example.solox3_dit2b21.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.solox3_dit2b21.dao.DataCallback;
import com.example.solox3_dit2b21.daoimpl.FirebaseUserDao;
import com.example.solox3_dit2b21.model.User;
import com.example.solox3_dit2b21.pages.Login;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthUtils {
    private static FirebaseAuth auth = FirebaseAuth.getInstance();

    public static boolean isUserAuthenticated() {
        return auth.getCurrentUser() != null;
    }

    public static String getUserId() {
        FirebaseUser user = auth.getCurrentUser();
        return (user != null) ? user.getUid() : null;
    }
    public static void redirectToLoginIfNotAuthenticated(Context context) {
        if (!isUserAuthenticated()) {
            Intent intent = new Intent(context, Login.class);
            context.startActivity(intent);
            if (context instanceof Activity) {
                ((Activity) context).finish();
            }
        }
    }
}
