package com.ayesha.gym_management.activities;


import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import com.ayesha.gym_management.R;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private CardView btnSetPin, btnChangePin, btnRemovePin;
    private ChipGroup chipGroupCurrency;
    private Chip chipPKR, chipINR, chipUSD, chipAED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPreferences = getSharedPreferences("GymPrefs", MODE_PRIVATE);

        setupToolbar();
        initializeViews();
        updatePinButtons();
        setupCurrency();
        setupClickListeners();
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
        btnSetPin = findViewById(R.id.btnSetPin);
        btnChangePin = findViewById(R.id.btnChangePin);
        btnRemovePin = findViewById(R.id.btnRemovePin);
        chipGroupCurrency = findViewById(R.id.chipGroupCurrency);
        chipPKR = findViewById(R.id.chipPKR);
        chipINR = findViewById(R.id.chipINR);
        chipUSD = findViewById(R.id.chipUSD);
        chipAED = findViewById(R.id.chipAED);
    }

    private void updatePinButtons() {
        String pin = sharedPreferences.getString("pin", null);
        boolean hasPin = (pin != null && !pin.isEmpty());

        btnSetPin.setVisibility(hasPin ? View.GONE : View.VISIBLE);
        btnChangePin.setVisibility(hasPin ? View.VISIBLE : View.GONE);
        btnRemovePin.setVisibility(hasPin ? View.VISIBLE : View.GONE);
    }

    private void setupCurrency() {
        String currency = sharedPreferences.getString("currency", "PKR");

        switch (currency) {
            case "PKR":
                chipPKR.setChecked(true);
                break;
            case "INR":
                chipINR.setChecked(true);
                break;
            case "USD":
                chipUSD.setChecked(true);
                break;
            case "AED":
                chipAED.setChecked(true);
                break;
        }
    }

    private void setupClickListeners() {
        btnSetPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSetPinDialog();
            }
        });

        btnChangePin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangePinDialog();
            }
        });

        btnRemovePin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRemovePinDialog();
            }
        });

        chipGroupCurrency.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                String newCurrency = "";

                if (checkedId == R.id.chipPKR) {
                    newCurrency = "PKR";
                } else if (checkedId == R.id.chipINR) {
                    newCurrency = "INR";
                } else if (checkedId == R.id.chipUSD) {
                    newCurrency = "USD";
                } else if (checkedId == R.id.chipAED) {
                    newCurrency = "AED";
                }

                sharedPreferences.edit().putString("currency", newCurrency).apply();
                Toast.makeText(SettingsActivity.this,
                        getString(R.string.currency_changed, newCurrency),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showSetPinDialog() {
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        input.setHint("Enter 4-digit PIN");

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);

        new AlertDialog.Builder(this)
                .setTitle(R.string.set_new_pin)
                .setView(input)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String pin = input.getText().toString().trim();

                        if (pin.length() != 4) {
                            Toast.makeText(SettingsActivity.this, R.string.pin_must_be_4_digits, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        sharedPreferences.edit().putString("pin", pin).apply();
                        Toast.makeText(SettingsActivity.this, R.string.pin_set_success, Toast.LENGTH_SHORT).show();
                        updatePinButtons();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void showChangePinDialog() {
        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_pin, null);
        final EditText etOldPin = dialogView.findViewById(R.id.etOldPin);
        final EditText etNewPin = dialogView.findViewById(R.id.etNewPin);
        final EditText etConfirmPin = dialogView.findViewById(R.id.etConfirmPin);

        new AlertDialog.Builder(this)
                .setTitle(R.string.change_pin)
                .setView(dialogView)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String oldPin = etOldPin.getText().toString().trim();
                        String newPin = etNewPin.getText().toString().trim();
                        String confirmPin = etConfirmPin.getText().toString().trim();
                        String savedPin = sharedPreferences.getString("pin", "");

                        if (!oldPin.equals(savedPin)) {
                            Toast.makeText(SettingsActivity.this, R.string.incorrect_pin, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (newPin.length() != 4) {
                            Toast.makeText(SettingsActivity.this, R.string.pin_must_be_4_digits, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (!newPin.equals(confirmPin)) {
                            Toast.makeText(SettingsActivity.this, R.string.pins_do_not_match, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        sharedPreferences.edit().putString("pin", newPin).apply();
                        Toast.makeText(SettingsActivity.this, R.string.pin_changed_success, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void showRemovePinDialog() {
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        input.setHint("Enter PIN");

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);

        new AlertDialog.Builder(this)
                .setTitle(R.string.remove_pin)
                .setMessage(R.string.enter_pin_to_remove)
                .setView(input)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String pin = input.getText().toString().trim();
                        String savedPin = sharedPreferences.getString("pin", "");

                        if (!pin.equals(savedPin)) {
                            Toast.makeText(SettingsActivity.this, R.string.incorrect_pin, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        sharedPreferences.edit().remove("pin").apply();
                        Toast.makeText(SettingsActivity.this, R.string.pin_removed_success, Toast.LENGTH_SHORT).show();
                        updatePinButtons();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}