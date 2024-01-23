package com.example.solox3_dit2b21.Utils;

import android.net.Uri;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class FirebaseStorageManager {

    public static void uploadImage(Uri imageUri, OnSuccessListener<Uri> onSuccessListener, OnFailureListener onFailureListener) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference fileRef = storageRef.child("uploadedImages/" + UUID.randomUUID().toString());

        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(onSuccessListener))
                .addOnFailureListener(onFailureListener);
    }

    public static void deleteImage(String imageUrl, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        StorageReference oldImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
        oldImageRef.delete().addOnSuccessListener(onSuccessListener).addOnFailureListener(onFailureListener);
    }

}
