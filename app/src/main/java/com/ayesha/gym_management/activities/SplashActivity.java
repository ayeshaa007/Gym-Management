package com.ayesha.gym_management.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.ayesha.gym_management.R;

public class SplashActivity extends AppCompatActivity {
    
    private static final int SPLASH_DELAY = 2000; // 2 seconds
    private SharedPreferences sharedPreferences;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        sharedPreferences = getSharedPreferences("GymPrefs", MODE_PRIVATE);
        
        // Navigate after delay
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                navigateToNextScreen();
            }
        }, SPLASH_DELAY);
    }
    
    private void navigateToNextScreen() {
        String pin = sharedPreferences.getString("pin", null);
        
        Intent intent;
        if (pin != null && !pin.isEmpty()) {
            // PIN is set, go to PIN screen
            intent = new Intent(SplashActivity.this, PinActivity.class);
        } else {
            // No PIN, go directly to Dashboard
            intent = new Intent(SplashActivity.this, DashboardActivity.class);
        }
        
        startActivity(intent);
        finish();
    }
}
