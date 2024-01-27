package com.example.solox3_dit2b21.pages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.solox3_dit2b21.R;
import com.example.solox3_dit2b21.Utils.AuthUtils;
import com.example.solox3_dit2b21.Utils.CurrentDateUtils;
import com.example.solox3_dit2b21.Utils.FirebaseStorageManager;
import com.example.solox3_dit2b21.Utils.LoadImageURL;
import com.example.solox3_dit2b21.dao.DataStatusCallback;
import com.example.solox3_dit2b21.model.Book;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.UUID;

public class EditProfile extends AppCompatActivity implements View.OnClickListener {

    private EditText usernameEditText;
    private ProgressBar progressBar;
    private static final int PICK_IMAGE_REQUEST = 1;
    String imageURL;
    ImageView profilePic;
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
        setContentView(R.layout.activity_edit_profile);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        progressBar = findViewById(R.id.progressBar);

        profilePic = findViewById(R.id.profilePic);
        if (user.getPhotoUrl() != null) {
            imageURL = user.getPhotoUrl().toString();
            LoadImageURL.loadImageURL(imageURL, profilePic);
        }

        usernameEditText = findViewById(R.id.usernameEditText);
        usernameEditText.setText(user.getDisplayName());
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();

            FirebaseStorageManager storageManager = new FirebaseStorageManager();
            storageManager.uploadImage(imageUri, new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String imageUrl = uri.toString();
                    imageURL=imageUrl;
                    LoadImageURL.loadImageURL(imageUrl, profilePic);
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("Upload Image Failed:" , e.getMessage());
                    Toast.makeText(EditProfile.this, "Upload image failed, try again later.",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void deleteImageAndSelectNewOne() {
        if (imageURL != null && !imageURL.equals("")) {
            showLoading(true);
            FirebaseStorageManager storageManager = new FirebaseStorageManager();
            storageManager.deleteImage(imageURL, new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("Firebase Storage", "Delete Success");
                    showLoading(false);
                    selectImage();
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("Delete Image Failed:", e.getMessage());
                    showLoading(false);
                    selectImage();
                }
            });
        } else {
            selectImage();
        }
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back) {
            finish();
        } else if (v.getId() == R.id.saveProfileButton) {
            String username = usernameEditText.getText().toString().trim();

            if (TextUtils.isEmpty(username)) {
                usernameEditText.setError("Username is required.");
            } else {
                UserProfileChangeRequest profileUpdates;
                if (imageURL == null) {
                    profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(username)
                            .build();
                } else {
                    profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(username)
                            .setPhotoUri(Uri.parse(imageURL))
                            .build();
                }

                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("updateProfile", "New Display Name: " + username);
                                    Log.d("updateProfile", "New Photo URI: " + imageURL);
                                } else {
                                    Log.d("updateProfileError", task.getException().getMessage());
                                }
                            }
                        });
            }
        } else if (v.getId() == R.id.uploadImageTextView || v.getId() == R.id.profilePic) {
            deleteImageAndSelectNewOne();
        }
    }
}