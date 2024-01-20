package com.example.solox3_dit2b21.Utils;

import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class LoadImageURL {
    public static void loadImageURL(String imageUrl, ImageView imageView) {
        // Load image into ImageView using Glide
        Glide.with(imageView.getContext())
                .load(imageUrl)
                .into(imageView);
    }
}
