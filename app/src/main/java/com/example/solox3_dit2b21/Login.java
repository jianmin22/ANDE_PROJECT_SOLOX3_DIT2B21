package com.example.solox3_dit2b21;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.AppCheckToken;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    EditText editTextEmail, editTextPassword;
    Button btnLogin;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView;
    FirebaseAppCheck firebaseAppCheck;

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
        setContentView(R.layout.activity_login);

        FirebaseApp.initializeApp(this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(PlayIntegrityAppCheckProviderFactory.getInstance());

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

        btnLogin = findViewById(R.id.btn_log_in);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.registerNow2);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Register.class);
                startActivity(intent);
                finish();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String email, password;
                email = editTextEmail.getText().toString();
                password = editTextPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(Login.this, "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(Login.this, "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(getApplicationContext(), "Get Token", Toast.LENGTH_SHORT).show();
//                firebaseAppCheck
//                                .getLimitedUseAppCheckToken()
//                                        .addOnSuccessListener(new OnSuccessListener<AppCheckToken>() {
//                                            @Override
//                                            public void onSuccess(@NonNull AppCheckToken tokenResponse) {
//                                                String appCheckToken = tokenResponse.getToken();
//                                                Toast.makeText(getApplicationContext(), "token: " + appCheckToken, Toast.LENGTH_SHORT).show();
//                                                Log.d("Token: ", appCheckToken);
                                                mAuth.signInWithEmailAndPassword(email, password)
                                                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                                progressBar.setVisibility(View.GONE);
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                                                    Intent intent = new Intent(getApplicationContext(), Home.class);
                                                                    startActivity(intent);
                                                                    finish();
                                                                } else {
                                                                    if (task.getException().getMessage().equals("The supplied auth credential is incorrect, malformed or has expired.")) {
                                                                        Toast.makeText(Login.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                                                                    } else {
                                                                        Toast.makeText(Login.this, "Authentication Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            }
                                                        });
                                            }
//                                        })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                progressBar.setVisibility(View.GONE);
//                                Log.e("AppCheck", "Error fetching token: " + e.getMessage());
//                                Toast.makeText(Login.this, "Authentication Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        });

//            }
        });

    }
}