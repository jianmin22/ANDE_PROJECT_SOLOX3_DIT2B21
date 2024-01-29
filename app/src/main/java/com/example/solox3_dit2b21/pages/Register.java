package com.example.solox3_dit2b21.pages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.solox3_dit2b21.R;
import com.example.solox3_dit2b21.Utils.CurrentDateUtils;
import com.example.solox3_dit2b21.dao.DataStatusCallback;
import com.example.solox3_dit2b21.daoimpl.FirebaseUserDao;
import com.example.solox3_dit2b21.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class Register extends AppCompatActivity {

    EditText editTextUsername, editTextEmail, editTextPassword, editTextPasswordCheck;
    Button btnRegister;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    ProgressBar progressBar;
    TextView textView;
    private FirebaseUserDao userDao = new FirebaseUserDao();

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(this, Home.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        editTextUsername = findViewById(R.id.username);
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        ImageView imageViewShowHidePassword = findViewById(R.id.show_hide_password);
        imageViewShowHidePassword
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (editTextPassword.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())) {
                            editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            imageViewShowHidePassword.setImageResource(R.drawable.eye_checked);
                        } else {
                            editTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            imageViewShowHidePassword.setImageResource(R.drawable.eye_unchecked);
                        }
                    }
                });
        editTextPasswordCheck = findViewById(R.id.password_check);
        ImageView imageViewShowHidePasswordCheck = findViewById(R.id.show_hide_password_check);
        imageViewShowHidePasswordCheck
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (editTextPassword.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())) {
                            editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            imageViewShowHidePasswordCheck.setImageResource(R.drawable.eye_checked);
                        } else {
                            editTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            imageViewShowHidePasswordCheck.setImageResource(R.drawable.eye_unchecked);
                        }
                    }
                });

        btnRegister = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.loginNow2);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
                finish();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String username = editTextUsername.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                String passwordCheck = editTextPasswordCheck.getText().toString().trim();

                if (TextUtils.isEmpty(username)) {
                    editTextUsername.setError("Username is required.");
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    editTextEmail.setError("Email is required.");
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    editTextPassword.setError("Password is required.");
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (password.length() < 8) {
                    editTextPassword.setError("Password must be at least 8 characters.");
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (TextUtils.isEmpty(passwordCheck)) {
                    editTextPasswordCheck.setError("Please enter your password twice.");
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (!password.equals(passwordCheck)) {
                    editTextPasswordCheck.setError("Passwords do not match.");
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    firebaseUser = auth.getCurrentUser();

                                    User user = new User(firebaseUser.getUid(), firebaseUser.getEmail(), username, "", "", CurrentDateUtils.getCurrentDateTime(), CurrentDateUtils.getCurrentDateTime());
                                    userDao.insertUser(user, new DataStatusCallback() {
                                        @Override
                                        public void onSuccess() {
                                            Toast.makeText(Register.this, "Account Created", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onFailure(Exception exception) {
                                            Toast.makeText(Register.this, "Failed to register account", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    Intent intent = new Intent(getApplicationContext(), Login.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    if (task.getException().getMessage().equals("The supplied auth credential is incorrect, malformed or has expired.")) {
                                        Toast.makeText(Register.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(Register.this, "Authentication Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });

            }
        });
    }
}