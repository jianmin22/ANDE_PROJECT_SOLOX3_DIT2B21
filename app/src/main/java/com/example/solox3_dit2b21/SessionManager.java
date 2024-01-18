package com.example.solox3_dit2b21;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class SessionManager extends AppCompatActivity {
    final Session session = new ViewModelProvider(this).get(Session.class);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session.getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User data) {
                // update ui.
            }

        });
    }

    public void checkAuthenticationAndNavigate(Context context, Class<?> targetActivity) {
        if (session.getUser() == null) {
            navigateTo(context, Login.class);
        } else {
            navigateTo(context, targetActivity);
        }
    }

    private void navigateTo(Context context, Class<?> targetActivity) {
        Intent intent = new Intent(context, targetActivity);
        context.startActivity(intent);
        if (context instanceof Activity) {
            ((Activity) context).finish(); // Finish the current activity to prevent going back
        }
    }
}
