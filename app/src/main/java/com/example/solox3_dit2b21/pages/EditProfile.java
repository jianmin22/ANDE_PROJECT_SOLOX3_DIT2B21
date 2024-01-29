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
import com.example.solox3_dit2b21.dao.DataCallback;
import com.example.solox3_dit2b21.dao.DataStatusCallback;
import com.example.solox3_dit2b21.daoimpl.FirebaseUserDao;
import com.example.solox3_dit2b21.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.example.solox3_dit2b21.Utils.DeletionCompleteListener;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class EditProfile extends AppCompatActivity implements View.OnClickListener {

    private EditText usernameEditText, bioEditText;
    private ProgressBar progressBar;
    private boolean deleting=false;
    private static final int PICK_IMAGE_REQUEST = 1;
    FirebaseStorageManager storageManager = new FirebaseStorageManager();
    List<String> oldImageURL=new ArrayList<String>();

    private String imageURL, userId;
    ImageView profilePic;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    private User user;
    private FirebaseUserDao userDao = new FirebaseUserDao();
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
        firebaseUser = auth.getCurrentUser();
        userId = firebaseUser.getUid();

        progressBar = findViewById(R.id.progressBar);

        profilePic = findViewById(R.id.profilePic);
        usernameEditText = findViewById(R.id.usernameEditText);
        bioEditText = findViewById(R.id.bioEditText);

        bindDataForEditProfile(userId);
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
            showLoading(true);
            Uri imageUri = data.getData();

            FirebaseStorageManager storageManager = new FirebaseStorageManager();
            storageManager.uploadImage(imageUri, new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String imageUrl = uri.toString();
                    Log.d("Upload Image Success:" , imageUrl);
                    imageURL=imageUrl;
                    LoadImageURL.loadImageURL(imageUrl, profilePic);
                    showLoading(false);
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("Upload Image Failed:" , e.getMessage());
                    Toast.makeText(EditProfile.this, "Upload image failed, try again later.",Toast.LENGTH_SHORT).show();
                    showLoading(false);
                }
            });
        }
    }


    private void selectNewImage() {
        if (imageURL != null && !imageURL.equals("")) {
            oldImageURL.add(imageURL);
            selectImage();
        } else {
            selectImage();
        }
    }

    private void deleteImages(List<String> urls, DeletionCompleteListener callback) {
        if (urls.isEmpty()) {
            callback.onAllDeletionsComplete();
            return;
        }

        AtomicInteger pendingDeletions = new AtomicInteger(urls.size()); // Track pending deletions

        for (String url : urls) {
            storageManager.deleteImage(url, new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("Firebase Storage", "Delete Success for URL: " + url);
                    if (pendingDeletions.decrementAndGet() == 0) { // Check if all deletions are done
                        callback.onAllDeletionsComplete();
                    }
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("Delete Image Failed for URL: " + url, e.getMessage());
                    if (pendingDeletions.decrementAndGet() == 0) { // Check if all deletions are done
                        callback.onAllDeletionsComplete();
                    }
                }
            });
        }
    }


    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void bindDataForEditProfile(String userId) {
        userDao.getUser(userId, new DataCallback<User>() {
            @Override
            public void onDataReceived(User userData) {
                if (userData != null) {
                    user = userData;
                    usernameEditText.setText(userData.getUsername());
                    if (userData.getBio() != null) {
                        bioEditText.setText(userData.getBio());
                    }
                    if (userData.getProfilePic() != null) {
                        imageURL = userData.getProfilePic();
                        LoadImageURL.loadImageURL(imageURL.toString(), profilePic);
                    }
                } else {
                    throw new Error("User not received");
                }
            }

            @Override
            public void onError(Exception exception) {
                Log.e("bindDataForUser", "Error fetching user", exception);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back) {
            if(!oldImageURL.isEmpty()){
                deleting=true;
                DeletionCompleteListener deletionListener = () -> finish();
                if (oldImageURL.size() > 1) {
                    List<String> urlsToDelete = oldImageURL.subList(1, oldImageURL.size());
                    deleteImages(urlsToDelete,deletionListener);
                    deleteImages(Collections.singletonList(imageURL), deletionListener);
                }else if(oldImageURL.size()==1){
                    deleteImages(Collections.singletonList(imageURL),deletionListener);
                }
            }
            if(!deleting){
                finish();
            }
        } else if (v.getId() == R.id.saveProfileButton) {
            String username = usernameEditText.getText().toString().trim();

            if (TextUtils.isEmpty(username)) {
                usernameEditText.setError("Username is required.");
            } else {
                User updatedUser = user;
                updatedUser.setUsername(username);

                String bio = bioEditText.getText().toString().trim();
                updatedUser.setBio(bio);

                updatedUser.setProfilePic(imageURL);

                updatedUser.setLastUpdated(CurrentDateUtils.getCurrentDateTime());

                userDao.updateUser(updatedUser, new DataStatusCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(EditProfile.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        if (!oldImageURL.isEmpty()) {
                            deleting=true;
                            DeletionCompleteListener deletionListener = () -> Log.d("update and delete image success",imageURL);
                            deleteImages(oldImageURL,deletionListener);
                        }
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        Log.e("Update Profile Failed", exception.getMessage());
                        Toast.makeText(EditProfile.this, "Failed to update profile!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else if (v.getId() == R.id.uploadImageTextView || v.getId() == R.id.profilePic) {
            selectNewImage();
        }
    }
}