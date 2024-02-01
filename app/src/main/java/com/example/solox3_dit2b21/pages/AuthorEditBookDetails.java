package com.example.solox3_dit2b21.pages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.solox3_dit2b21.R;
import com.example.solox3_dit2b21.Utils.AuthUtils;
import com.example.solox3_dit2b21.Utils.CurrentDateUtils;
import com.example.solox3_dit2b21.Utils.FirebaseStorageManager;
import com.example.solox3_dit2b21.Utils.LoadImageURL;
import com.example.solox3_dit2b21.dao.BookDao;
import com.example.solox3_dit2b21.dao.CategoryDao;
import com.example.solox3_dit2b21.dao.DataCallback;
import com.example.solox3_dit2b21.dao.DataStatusCallback;
import com.example.solox3_dit2b21.daoimpl.FirebaseBookDao;
import com.example.solox3_dit2b21.daoimpl.FirebaseCategoryDao;
import com.example.solox3_dit2b21.model.Book;
import com.example.solox3_dit2b21.model.Category;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.example.solox3_dit2b21.Utils.DeletionCompleteListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class AuthorEditBookDetails extends AppCompatActivity implements View.OnClickListener{
    private String bookId;
    private String userId;
    FirebaseStorageManager storageManager = new FirebaseStorageManager();
    private static final int PICK_IMAGE_REQUEST = 1;
    CategoryDao categoryDao = new FirebaseCategoryDao();
    BookDao bookDao = new FirebaseBookDao();
    private boolean deleting=false;
    Book bookDetails;
    ImageView bookImage;
    EditText bookTitleEditText;
    Spinner categorySpinner;
    EditText descriptionEditText;
    String imageURL;
    List<String> oldImageURL=new ArrayList<String>();
    String categoryId;
    List<Category> categoriesList = new ArrayList<>();
    private ProgressBar progressBar;

    @Override
    protected void onStart() {
        super.onStart();
        AuthUtils.redirectToLoginIfNotAuthenticated(this);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_edit_book_details);
        userId= AuthUtils.getUserId();
        categorySpinner = findViewById(R.id.categorySpinner);
        bookImage=findViewById(R.id.bookImage);
        bookTitleEditText=findViewById(R.id.bookTitleEditText);
        progressBar = findViewById(R.id.progressBar);
        descriptionEditText=findViewById(R.id.descriptionEditText);
        categoryDao.loadAllCategories(new DataCallback<List<Category>>() {
            @Override
            public void onDataReceived(List<Category> categories) {
                List<String> categoryNames = new ArrayList<>();
                categoriesList=categories;
                for (Category category : categories) {
                    categoryNames.add(category.getCategoryName());

                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(AuthorEditBookDetails.this, android.R.layout.simple_spinner_item, categoryNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                categorySpinner.setAdapter(adapter);

                categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        // Get the selected category name
                        String selectedCategoryName = categoryNames.get(position);

                        // Find the corresponding Category object and retrieve its ID
                        for (Category category : categories) {
                            if (category.getCategoryName().equals(selectedCategoryName)) {
                                categoryId = category.getCategoryId();
                                break;
                            }
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        categoryId=null;
                    }
                });

                Bundle getData = getIntent().getExtras();

                if (getData != null) {
                    bookId = getData.getString("bookId");
                    bookDao.loadBookDetailsById(bookId, new DataCallback<Book>(){
                        @Override
                        public void onDataReceived(Book returnedBookDetails) {
                            bookDetails=returnedBookDetails;
                            if (!bookDetails.getAuthorId().equals(userId)){
                                Toast.makeText(getApplicationContext(), "Failed to load page", Toast.LENGTH_LONG).show();
                                finish();
                            }
                            if(bookDetails.getImage()!=null&&!bookDetails.getImage().isEmpty()){
                                LoadImageURL.loadImageURL(bookDetails.getImage(),bookImage);
                            }
                            bookTitleEditText.setText(bookDetails.getTitle());
                            descriptionEditText.setText(bookDetails.getDescription());
                            imageURL=bookDetails.getImage();
                            categoryId=bookDetails.getCategoryId();
                            if (categoryId != null) {
                                int selectedIndex = -1;
                                for (int i = 0; i < categoriesList.size(); i++) {
                                    if (categoriesList.get(i).getCategoryId().equals(categoryId)) {
                                        selectedIndex = i;
                                        break;
                                    }
                                }

                                if (selectedIndex != -1) {
                                    categorySpinner.setSelection(selectedIndex);
                                }
                            }
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

            @Override
            public void onError(Exception exception) {
                Log.e("Firebase", "Failed to get categories", exception);
                Toast.makeText(getApplicationContext(), "Failed to load page", Toast.LENGTH_LONG).show();
                finish();
            }
        });
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
            storageManager.uploadImage(imageUri, new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String imageUrl = uri.toString();
                    Log.d("Upload Image Success:" , imageUrl);
                    imageURL=imageUrl;
                    LoadImageURL.loadImageURL(imageUrl, bookImage);
                    showLoading(false);
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("Upload Image Failed:" , e.getMessage());
                    Toast.makeText(AuthorEditBookDetails.this, "Upload image failed, try again later.",Toast.LENGTH_SHORT).show();
                    showLoading(false);
                }
            });
        }
    }



    @Override
    public void onClick (View v){
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
        }else if (v.getId() == R.id.proceedEditChapterButton) {
            if(!oldImageURL.isEmpty()){
                deleting=true;
                DeletionCompleteListener deletionListener = () -> navigateToEditBook();
                if (oldImageURL.size() > 1) {
                    List<String> urlsToDelete = oldImageURL.subList(1, oldImageURL.size());
                    deleteImages(urlsToDelete, deletionListener);
                    deleteImages(Collections.singletonList(imageURL),deletionListener);
                }else if(oldImageURL.size()==1){
                    deleteImages(Collections.singletonList(imageURL),deletionListener);
                }
            }
            if(!deleting) {
                navigateToEditBook();
            }

        } else if (v.getId() == R.id.saveBookDetailsButton) {
            String title = bookTitleEditText.getText().toString();
            String description = descriptionEditText.getText().toString();
            if (!categoryId.isEmpty() && !title.isEmpty() && !description.isEmpty()) {
                Book book;

                if (bookId != null && !bookId.isEmpty()) {
                    book = bookDetails;
                    book.setTitle(title);
                    book.setDescription(description);
                    book.setCategoryId(categoryId);
                    book.setImage(imageURL);
                    book.setLastUpdated(CurrentDateUtils.getCurrentDateTime());
                    bookDao.updateBookDetails(book, new DataStatusCallback() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(AuthorEditBookDetails.this, "Book updated successfully", Toast.LENGTH_SHORT).show();
                            if (!oldImageURL.isEmpty()) {
                                deleting=true;
                                DeletionCompleteListener deletionListener = () -> Log.d("update and delete image success",imageURL);
                                deleteImages(oldImageURL,deletionListener);
                            }
                        }

                        @Override
                        public void onFailure(Exception exception) {
                            Log.e("Update Book Failed", exception.getMessage());
                            Toast.makeText(AuthorEditBookDetails.this, "Failed to save book details!", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    String newBookId=UUID.randomUUID().toString();
                    book = new Book(newBookId, title, description, categoryId, 0,imageURL, null, 0, CurrentDateUtils.getCurrentDateTime(), CurrentDateUtils.getCurrentDateTime(), userId, "false");
                    bookDao.insertBook(book, new DataStatusCallback() {
                        @Override
                        public void onSuccess() {
                            bookId=newBookId;
                            Toast.makeText(AuthorEditBookDetails.this, "Book saved successfully", Toast.LENGTH_SHORT).show();
                            if (!oldImageURL.isEmpty()) {
                                deleting=true;
                                DeletionCompleteListener deletionListener = () -> Log.d("update and delete image success",imageURL);
                                deleteImages(oldImageURL,deletionListener);
                            }
                        }

                        @Override
                        public void onFailure(Exception exception) {
                            Log.e("Insert Book Failed", exception.getMessage());
                            Toast.makeText(AuthorEditBookDetails.this, "Failed to save book details!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                Toast.makeText(AuthorEditBookDetails.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        } else if (v.getId() == R.id.uploadImageTextView || v.getId() == R.id.bookImage) {
            selectNewImage();
        }


    }
    private void navigateToEditBook(){
        if (bookId != null && !bookId.equals("")) {
            Intent intent = new Intent(AuthorEditBookDetails.this, EditChapter.class);
            intent.putExtra("bookId", bookId);
            startActivity(intent);
        } else {
            Toast.makeText(AuthorEditBookDetails.this, "Please Save Your Book Details First.", Toast.LENGTH_SHORT).show();
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
}
