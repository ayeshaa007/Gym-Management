package com.ayesha.gym_management.activities;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import com.ayesha.gym_management.R;
import com.gymmanagement.app.database.DatabaseHelper;
import com.gymmanagement.app.models.Member;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddMemberActivity extends AppCompatActivity {

    private EditText etFullName, etPhone, etJoinDate, etFeeAmount;
    private ChipGroup chipGroupPlan;
    private Chip chipOneMonth, chipThreeMonths, chipSixMonths;
    private RadioGroup radioGroupPayment;
    private RadioButton radioPaid, radioUnpaid;
    private MaterialButton btnSaveMember;
    private TextView tvCurrency;

    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;
    private Calendar calendar;
    private String selectedPlan = "1 Month";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);

        databaseHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("GymPrefs", MODE_PRIVATE);
        calendar = Calendar.getInstance();

        setupToolbar();
        initializeViews();
        setupListeners();
        setCurrentDate();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initializeViews() {
        etFullName = findViewById(R.id.etFullName);
        etPhone = findViewById(R.id.etPhone);
        etJoinDate = findViewById(R.id.etJoinDate);
        etFeeAmount = findViewById(R.id.etFeeAmount);
        chipGroupPlan = findViewById(R.id.chipGroupPlan);
        chipOneMonth = findViewById(R.id.chipOneMonth);
        chipThreeMonths = findViewById(R.id.chipThreeMonths);
        chipSixMonths = findViewById(R.id.chipSixMonths);
        radioGroupPayment = findViewById(R.id.radioGroupPayment);
        radioPaid = findViewById(R.id.radioPaid);
        radioUnpaid = findViewById(R.id.radioUnpaid);
        btnSaveMember = findViewById(R.id.btnSaveMember);
        tvCurrency = findViewById(R.id.tvCurrency);

        // Set currency
        String currency = sharedPreferences.getString("currency", "PKR");
        tvCurrency.setText(getCurrencySymbol(currency));

        // Set default selections
        chipOneMonth.setChecked(true);
        radioPaid.setChecked(true);
    }

    private void setupListeners() {
        // Date picker
        etJoinDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        // Plan chips
        chipGroupPlan.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                if (checkedId == R.id.chipOneMonth) {
                    selectedPlan = "1 Month";
                } else if (checkedId == R.id.chipThreeMonths) {
                    selectedPlan = "3 Months";
                } else if (checkedId == R.id.chipSixMonths) {
                    selectedPlan = "6 Months";
                }
            }
        });

        // Save button
        btnSaveMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMember();
            }
        });
    }

    private void setCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        etJoinDate.setText(sdf.format(calendar.getTime()));
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(year, month, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        etJoinDate.setText(sdf.format(calendar.getTime()));
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void saveMember() {
        // Validate inputs
        String fullName = etFullName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String joinDate = etJoinDate.getText().toString().trim();
        String feeAmountStr = etFeeAmount.getText().toString().trim();
        String paymentStatus = radioPaid.isChecked() ? "Paid" : "Unpaid";

        if (fullName.isEmpty()) {
            Toast.makeText(this, R.string.please_enter_name, Toast.LENGTH_SHORT).show();
            etFullName.requestFocus();
            return;
        }

        if (phone.isEmpty()) {
            Toast.makeText(this, R.string.please_enter_phone, Toast.LENGTH_SHORT).show();
            etPhone.requestFocus();
            return;
        }

        if (feeAmountStr.isEmpty()) {
            Toast.makeText(this, R.string.please_enter_fee, Toast.LENGTH_SHORT).show();
            etFeeAmount.requestFocus();
            return;
        }

        double feeAmount;
        try {
            feeAmount = Double.parseDouble(feeAmountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.please_enter_fee, Toast.LENGTH_SHORT).show();
            etFeeAmount.requestFocus();
            return;
        }

        // Calculate expiry date
        String expiryDate = DatabaseHelper.calculateExpiryDate(joinDate, selectedPlan);

        // Create member object
        Member member = new Member();
        member.setFullName(fullName);
        member.setPhone(phone);
        member.setJoinDate(joinDate);
        member.setPlan(selectedPlan);
        member.setFeeAmount(feeAmount);
        member.setPaymentStatus(paymentStatus);
        member.setExpiryDate(expiryDate);

        // Save to database
        long result = databaseHelper.addMember(member);

        if (result > 0) {
            Toast.makeText(this, R.string.member_added_success, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error adding member", Toast.LENGTH_SHORT).show();
        }
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