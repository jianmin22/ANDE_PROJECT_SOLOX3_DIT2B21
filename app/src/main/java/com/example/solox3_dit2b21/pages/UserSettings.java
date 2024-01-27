package com.example.solox3_dit2b21.pages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.solox3_dit2b21.R;
import com.example.solox3_dit2b21.Utils.AuthUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserSettings extends AppCompatActivity implements View.OnClickListener {

    private TextView editProfile, commentHistory, resetPassword, logOut;
    FirebaseAuth auth;
    FirebaseUser user;
    @Override
    protected void onStart() {
        super.onStart();
        AuthUtils.redirectToLoginIfNotAuthenticated(this);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        editProfile = findViewById(R.id.editProfile);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserSettings.this, EditProfile.class);
                startActivity(intent);
            }
        });

        commentHistory = findViewById(R.id.commentHistory);
        commentHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserSettings.this, CommentHistory.class);
                startActivity(intent);
            }
        });

        resetPassword = findViewById(R.id.resetPassword);
        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Reset Password?");
                passwordResetDialog.setMessage("Are you sure you want to reset your password?");

                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        auth.sendPasswordResetEmail(user.getEmail())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(UserSettings.this, "Reset password link sent to your email.", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(UserSettings.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });

                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                passwordResetDialog.create().show();
            }
        });

        logOut = findViewById(R.id.logOut);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                Intent intent = new Intent(UserSettings.this, Login.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onClick(View v){
        if (v.getId() == R.id.back){
            finish();
        }
    }
}