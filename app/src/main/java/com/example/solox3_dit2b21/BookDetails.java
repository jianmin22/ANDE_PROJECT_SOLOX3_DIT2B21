package com.example.solox3_dit2b21;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class BookDetails extends AppCompatActivity {
private String bookId;
    TextView bookIdContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);
        bookIdContent = (TextView)findViewById(R.id.bookId);
        Bundle getData = getIntent().getExtras();

        if (getData != null){
            bookId = getData.getString("bookId");
            bookIdContent.setText(bookId);

        }else{
            Toast.makeText(getApplicationContext(),"Failed to get Data",Toast.LENGTH_LONG).show();
        }

    }
}