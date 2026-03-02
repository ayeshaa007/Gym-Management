package com.ayesha.gym_management.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.ayesha.gym_management.R;
import com.gymmanagement.app.database.DatabaseHelper;
import com.gymmanagement.app.models.Member;

import java.util.List;

public class DashboardActivity extends AppCompatActivity {
    
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;
    
    private TextView tvTotalMembers, tvActiveMembers, tvExpiredMembers;
    private TextView tvTotalRevenue, tvExpiringSoon;
    private FloatingActionButton fabAddMember;
    private CardView btnViewMembers;
    private ImageView btnSettings;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        
        databaseHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("GymPrefs", MODE_PRIVATE);
        
        initializeViews();
        setupClickListeners();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        updateDashboardStats();
    }
    
    @SuppressLint("WrongViewCast")
    private void initializeViews() {
        tvTotalMembers = findViewById(R.id.tvTotalMembers);
        tvActiveMembers = findViewById(R.id.tvActiveMembers);
        tvExpiredMembers = findViewById(R.id.tvExpiredMembers);
        tvTotalRevenue = findViewById(R.id.tvTotalRevenue);
        tvExpiringSoon = findViewById(R.id.tvExpiringSoon);
        fabAddMember = findViewById(R.id.fabAddMember);
        btnViewMembers = findViewById(R.id.btnViewMembers);
        btnSettings = findViewById(R.id.btnSettings);
    }
    
    private void setupClickListeners() {
        fabAddMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, AddMemberActivity.class));
            }
        });
        
        btnViewMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, MemberListActivity.class));
            }
        });
        
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, SettingsActivity.class));
            }
        });
    }
    
    private void updateDashboardStats() {
        List<Member> allMembers = databaseHelper.getAllMembers();
        
        int totalMembers = allMembers.size();
        int activeMembers = 0;
        int expiredMembers = 0;
        int expiringSoon = 0;
        
        for (Member member : allMembers) {
            String status = member.getMembershipStatus();
            
            if (status.equals("Active")) {
                activeMembers++;
            } else if (status.equals("Expired")) {
                expiredMembers++;
            } else if (status.equals("Expiring Soon")) {
                expiringSoon++;
            }
        }
        
        double totalRevenue = databaseHelper.getTotalRevenue();
        
        // Get currency
        String currency = sharedPreferences.getString("currency", "PKR");
        String currencySymbol = getCurrencySymbol(currency);
        
        // Update UI
        tvTotalMembers.setText(String.valueOf(totalMembers));
        tvActiveMembers.setText(String.valueOf(activeMembers));
        tvExpiredMembers.setText(String.valueOf(expiredMembers));
        tvTotalRevenue.setText(currencySymbol + " " + String.format("%.0f", totalRevenue));
        tvExpiringSoon.setText(String.valueOf(expiringSoon));
    }
    
    private String getCurrencySymbol(String currency) {
        switch (currency) {
            case "PKR": return "₨";
            case "INR": return "₹";
            case "USD": return "$";
            case "AED": return "د.إ";
            default: return "₨";
        }
    }
}
