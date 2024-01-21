package com.example.solox3_dit2b21;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity {

    EditText editTextEmail, editTextPassword, editTextPasswordCheck;
    Button btnRegister;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), Home.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        ImageView imageViewShowHidePassword = findViewById(R.id.show_hide_password);
        imageViewShowHidePassword.setImageResource(R.drawable.eye_checked);
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
        imageViewShowHidePasswordCheck.setImageResource(R.drawable.eye_checked);
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
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String email, password, passwordCheck;
                email = editTextEmail.getText().toString();
                password = editTextPassword.getText().toString();
                passwordCheck = editTextPasswordCheck.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(Register.this, "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(Register.this, "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(passwordCheck)) {
                    Toast.makeText(Register.this, "Re-enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(passwordCheck)) {
                    Toast.makeText(Register.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Toast.makeText(Register.this, "Account Created", Toast.LENGTH_SHORT).show();
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