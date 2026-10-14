package com.example.smartpantrymanager;

import com.example.smartpantrymanager.util.NavHelper;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class SettingsActivity extends AppCompatActivity {

    public static final String PREFS = "pantry_prefs";
    public static final String KEY_EXPIRY_ALERTS = "expiry_alerts";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        SwitchCompat switchExpiry = findViewById(R.id.switchExpiry);

        // Load the saved value, and save every change straight away
        switchExpiry.setChecked(prefs.getBoolean(KEY_EXPIRY_ALERTS, true));
        switchExpiry.setOnCheckedChangeListener((button, isChecked) ->
                prefs.edit().putBoolean(KEY_EXPIRY_ALERTS, isChecked).apply());
        NavHelper.setup(this, R.id.nav_settings);
    }
}