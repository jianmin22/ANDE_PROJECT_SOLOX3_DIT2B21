package com.example.solox3_dit2b21.pages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.solox3_dit2b21.R;
import com.example.solox3_dit2b21.Utils.FirebaseStorageManager;
import com.example.solox3_dit2b21.Utils.LoadImageURL;
import com.example.solox3_dit2b21.dao.BookDao;
import com.example.solox3_dit2b21.dao.CategoryDao;
import com.example.solox3_dit2b21.dao.DataCallback;
import com.example.solox3_dit2b21.daoimpl.FirebaseBookDao;
import com.example.solox3_dit2b21.daoimpl.FirebaseCategoryDao;
import com.example.solox3_dit2b21.model.Book;
import com.example.solox3_dit2b21.model.Category;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

public class AuthorEditBookDetails extends AppCompatActivity implements View.OnClickListener{
    private String bookId;
    private static final int PICK_IMAGE_REQUEST = 1;
    CategoryDao categoryDao = new FirebaseCategoryDao();
    BookDao bookDao = new FirebaseBookDao();
    Book bookDetails;
    ImageView bookImage;
    EditText bookTitleEditText;
    Spinner categorySpinner;
    EditText descriptionEditText;
    String imageURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_edit_book_details);

        categorySpinner = findViewById(R.id.categorySpinner);
        bookImage=findViewById(R.id.bookImage);
        bookTitleEditText=findViewById(R.id.bookTitleEditText);
        descriptionEditText=findViewById(R.id.descriptionEditText);
        categoryDao.loadAllCategories(new DataCallback<List<Category>>() {
            @Override
            public void onDataReceived(List<Category> categories) {
                ArrayAdapter<Category> adapter = new ArrayAdapter<>(AuthorEditBookDetails.this, android.R.layout.simple_spinner_item, categories);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                categorySpinner.setAdapter(adapter);
            }

            @Override
            public void onError(Exception exception) {
                Log.e("Firebase", "Failed to get categories", exception);
                Toast.makeText(getApplicationContext(), "Failed to load page", Toast.LENGTH_LONG).show();
                finish();
            }
        });

        Bundle getData = getIntent().getExtras();

        if (getData != null) {
            bookId = getData.getString("bookId");
            bookDao.loadBookDetailsById(bookId, new DataCallback<Book>(){
                @Override
                public void onDataReceived(Book returnedBookDetails) {
                    bookDetails=returnedBookDetails;
                    LoadImageURL.loadImageURL(bookDetails.getImage(), bookImage);
                    bookTitleEditText.setText(bookDetails.getTitle());
                    descriptionEditText.setText(bookDetails.getDescription());
                    imageURL=bookDetails.getImage();
                }

                @Override
                public void onError(Exception exception) {
                    Log.e("Firebase", "Failed to get categories", exception);
                    Toast.makeText(getApplicationContext(), "Failed to load page", Toast.LENGTH_LONG).show();
                    finish();
                }
            });
        }


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
                    LoadImageURL.loadImageURL(imageUrl, bookImage);
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("Upload Image Failed:" , e.getMessage());
                    Toast.makeText(AuthorEditBookDetails.this, "Upload image failed, try again later.",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }



    @Override
    public void onClick (View v){
        if (v.getId() == R.id.back) {
            finish();
        } else if (v.getId() == R.id.proceedEditChapterButton) {

        } else if (v.getId() ==R.id.saveBookDetailsButton){
//            if(bookId!=null)
        }else if (v.getId() == R.id.uploadImageTextView || v.getId() == R.id.bookImage) {
            if (imageURL != null && !imageURL.equals("")) {
                FirebaseStorageManager storageManager = new FirebaseStorageManager();
                storageManager.deleteImage(imageURL, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firebase Storage", "Delete Success");
                        selectImage();
                    }
                }, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Delete Image Failed:", e.getMessage());
                        selectImage();
                    }
                });
            } else {
                selectImage();
            }
        }


    }
}
