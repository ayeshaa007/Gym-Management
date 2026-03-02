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

public class EditMemberActivity extends AppCompatActivity {

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
    private int memberId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_member);

        databaseHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("GymPrefs", MODE_PRIVATE);
        calendar = Calendar.getInstance();

        // Get member ID from intent
        memberId = getIntent().getIntExtra("member_id", -1);
        if (memberId == -1) {
            Toast.makeText(this, "Error: Member not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupToolbar();
        initializeViews();
        setupListeners();
        loadMemberData();
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
                updateMember();
            }
        });
    }

    private void loadMemberData() {
        Member member = databaseHelper.getMember(memberId);

        if (member == null) {
            Toast.makeText(this, "Error loading member data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Populate fields
        etFullName.setText(member.getFullName());
        etPhone.setText(member.getPhone());
        etJoinDate.setText(member.getJoinDate());
        etFeeAmount.setText(String.valueOf(member.getFeeAmount()));

        // Set plan
        selectedPlan = member.getPlan();
        if (selectedPlan.equals("1 Month")) {
            chipOneMonth.setChecked(true);
        } else if (selectedPlan.equals("3 Months")) {
            chipThreeMonths.setChecked(true);
        } else if (selectedPlan.equals("6 Months")) {
            chipSixMonths.setChecked(true);
        }

        // Set payment status
        if (member.getPaymentStatus().equals("Paid")) {
            radioPaid.setChecked(true);
        } else {
            radioUnpaid.setChecked(true);
        }
    }

    private void showDatePicker() {
        // Parse current date from EditText
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            calendar.setTime(sdf.parse(etJoinDate.getText().toString()));
        } catch (Exception e) {
            calendar = Calendar.getInstance();
        }

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

    private void updateMember() {
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
        member.setId(memberId);
        member.setFullName(fullName);
        member.setPhone(phone);
        member.setJoinDate(joinDate);
        member.setPlan(selectedPlan);
        member.setFeeAmount(feeAmount);
        member.setPaymentStatus(paymentStatus);
        member.setExpiryDate(expiryDate);

        // Update in database
        int result = databaseHelper.updateMember(member);

        if (result > 0) {
            Toast.makeText(this, R.string.member_updated_success, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error updating member", Toast.LENGTH_SHORT).show();
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