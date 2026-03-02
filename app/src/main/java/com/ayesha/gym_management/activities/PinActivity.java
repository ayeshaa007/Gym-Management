package com.ayesha.gym_management.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

import com.ayesha.gym_management.R;

public class PinActivity extends AppCompatActivity {
    
    private TextView[] pinDots;
    private String enteredPin = "";
    private SharedPreferences sharedPreferences;
    private String savedPin;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);
        
        sharedPreferences = getSharedPreferences("GymPrefs", MODE_PRIVATE);
        savedPin = sharedPreferences.getString("pin", "");
        
        initializeViews();
        setupKeypad();
    }
    
    private void initializeViews() {
        pinDots = new TextView[4];
        pinDots[0] = findViewById(R.id.pinDot1);
        pinDots[1] = findViewById(R.id.pinDot2);
        pinDots[2] = findViewById(R.id.pinDot3);
        pinDots[3] = findViewById(R.id.pinDot4);
    }
    
    private void setupKeypad() {
        // Number buttons
        int[] numberButtonIds = {
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        };
        
        for (int i = 0; i < numberButtonIds.length; i++) {
            final int number = i;
            MaterialButton button = findViewById(numberButtonIds[i]);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onNumberPressed(String.valueOf(number));
                }
            });
        }
        
        // Delete button
        MaterialButton btnDelete = findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeletePressed();
            }
        });
    }
    
    private void onNumberPressed(String number) {
        if (enteredPin.length() < 4) {
            enteredPin += number;
            updatePinDots();
            
            if (enteredPin.length() == 4) {
                verifyPin();
            }
        }
    }
    
    private void onDeletePressed() {
        if (enteredPin.length() > 0) {
            enteredPin = enteredPin.substring(0, enteredPin.length() - 1);
            updatePinDots();
        }
    }
    
    private void updatePinDots() {
        for (int i = 0; i < 4; i++) {
            if (i < enteredPin.length()) {
                pinDots[i].setBackgroundResource(R.drawable.pin_dot_filled);
            } else {
                pinDots[i].setBackgroundResource(R.drawable.pin_dot_empty);
            }
        }
    }
    
    private void verifyPin() {
        if (enteredPin.equals(savedPin)) {
            Intent intent = new Intent(PinActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        }
        else{
            Toast.makeText(this, R.string.incorrect_pin, Toast.LENGTH_SHORT).show();
            enteredPin = "";
            updatePinDots();

            // Shake animation
            LinearLayout pinDotsLayout = findViewById(R.id.pinDotsLayout);
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            pinDotsLayout.startAnimation(shake);
        }
    }
}
