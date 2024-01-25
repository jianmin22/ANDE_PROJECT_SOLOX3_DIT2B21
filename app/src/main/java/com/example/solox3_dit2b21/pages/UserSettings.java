package com.example.solox3_dit2b21.pages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.solox3_dit2b21.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.auth.User;

public class UserSettings extends AppCompatActivity implements View.OnClickListener {

    private TextView editProfile, resetPassword, logOut;
    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        editProfile = findViewById(R.id.editProfile);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText newUsername = new EditText (v.getContext());
                final AlertDialog.Builder editProfileDialog = new AlertDialog.Builder(v.getContext());
                editProfileDialog.setTitle("Edit Profile");
                editProfileDialog.setMessage("Enter your new username.");
                editProfileDialog.setView(newUsername);

                editProfileDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String username = newUsername.getText().toString().trim();

                        if (TextUtils.isEmpty(username)) {
                            newUsername.setError("Username is required.");
                            return;
                        }

                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(username)
                                .build();

                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("updateProfile", "New Display Name: " + username);
                                        } else {
                                            Log.d("updateProfileError", task.getException().getMessage());
                                        }
                                    }
                                });
                    }
                });

                editProfileDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                editProfileDialog.create().show();
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