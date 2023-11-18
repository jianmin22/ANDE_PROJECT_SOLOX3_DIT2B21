package com.example.solox3_dit2b21;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setContentView(R.layout.activity_home);

        //BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set a listener for item selection
        /*
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_bookshelf:
                        // Handle bookshelf item click
                        showToast("Bookshelf clicked");
                        return true;

                    case R.id.navigation_home:
                        // Handle home item click
                        showToast("Home clicked");
                        return true;

                    case R.id.navigation_category:
                        // Handle category item click
                        showToast("Category clicked");
                        return true;

                    case R.id.navigation_profile:
                        // Handle profile item click
                        showToast("Profile clicked");
                        return true;
                }
                return false;
            }
        });

         */
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
