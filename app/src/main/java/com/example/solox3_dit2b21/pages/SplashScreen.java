package com.example.solox3_dit2b21.pages;

import static java.lang.Thread.sleep;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.solox3_dit2b21.R;

public class SplashScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Launch the layout -> splash.xml
        setContentView(R.layout.activity_splash);

        Thread splashThread = new Thread() {

            public void run() {
                try {
                    // sleep time in milliseconds (3000 = 3sec)
                    sleep(3000);
                } catch(InterruptedException e) {
                    // Trace the error
                    e.printStackTrace();
                } finally {
                    // Launch the login class
                    Intent intent = new Intent(SplashScreen.this, Login.class);
                    startActivity(intent);
                }
            }
        };
        // To Start the thread
        splashThread.start();

    }
}
