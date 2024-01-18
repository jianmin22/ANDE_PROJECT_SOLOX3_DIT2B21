package com.example.solox3_dit2b21;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;

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
//                    Intent intent = new Intent(SplashScreen.this, Login.class);
//                    startActivity(intent);
                    SessionManager sessionManager = new SessionManager();
                    sessionManager.checkAuthenticationAndNavigate(SplashScreen.this, Login.class);
                }

            }
        };
        // To Start the thread
        splashThread.start();

    }
}
