package com.example.chessgame;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler; // <-- Import Handler
import android.os.Looper; // <-- Import Looper for modern Handler

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Splash extends AppCompatActivity {

    // Define the delay time as a constant for clarity
    private static final long SPLASH_DELAY_MS = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.splash);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // --- FIX: Use Handler for a cleaner delay and run on the Main Thread ---
        // This is a safer and more standard way to implement a simple delay.
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // The target activity must exist and be declared in the manifest.
                // It is assumed the class name is 'log_lobby.class' exactly as you wrote it.
                Intent intent = new Intent(Splash.this, log_lobby.class);
                startActivity(intent);

                // --- FIX: Add finish() so the user can't navigate back to the splash screen ---
                finish();
            }
        }, SPLASH_DELAY_MS);

        // Removed the unnecessary and complex 'Thread' implementation.
    }
}