package com.socialmediasafety.rating;

import android.os.Bundle;
import android.widget.Switch;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

/**
 * Settings activity for configuring app behavior and preferences.
 */
public class SettingsActivity extends AppCompatActivity {
    
    private Switch enableTwitterSwitch;
    private Switch enableRedditSwitch;
    private Switch enableFacebookSwitch;
    private Switch enableDiscordSwitch;
    private Switch enableInstagramSwitch;
    private Switch showMinimalRiskSwitch;
    private SeekBar sensitivitySeekBar;
    private TextView sensitivityText;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        initializeViews();
        loadSettings();
        setupListeners();
    }
    
    private void initializeViews() {
        enableTwitterSwitch = findViewById(R.id.enableTwitterSwitch);
        enableRedditSwitch = findViewById(R.id.enableRedditSwitch);
        enableFacebookSwitch = findViewById(R.id.enableFacebookSwitch);
        enableDiscordSwitch = findViewById(R.id.enableDiscordSwitch);
        enableInstagramSwitch = findViewById(R.id.enableInstagramSwitch);
        showMinimalRiskSwitch = findViewById(R.id.showMinimalRiskSwitch);
        sensitivitySeekBar = findViewById(R.id.sensitivitySeekBar);
        sensitivityText = findViewById(R.id.sensitivityText);
    }
    
    private void loadSettings() {
        // Load saved preferences or use defaults
        enableTwitterSwitch.setChecked(
            PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("enable_twitter", true));
        enableRedditSwitch.setChecked(
            PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("enable_reddit", true));
        enableFacebookSwitch.setChecked(
            PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("enable_facebook", true));
        enableDiscordSwitch.setChecked(
            PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("enable_discord", true));
        enableInstagramSwitch.setChecked(
            PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("enable_instagram", true));
        showMinimalRiskSwitch.setChecked(
            PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("show_minimal_risk", false));
        
        int sensitivity = PreferenceManager.getDefaultSharedPreferences(this)
                .getInt("sensitivity", 50);
        sensitivitySeekBar.setProgress(sensitivity);
        updateSensitivityText(sensitivity);
    }
    
    private void setupListeners() {
        enableTwitterSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> 
            savePreference("enable_twitter", isChecked));
        
        enableRedditSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> 
            savePreference("enable_reddit", isChecked));
        
        enableFacebookSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> 
            savePreference("enable_facebook", isChecked));
        
        enableDiscordSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> 
            savePreference("enable_discord", isChecked));
        
        enableInstagramSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> 
            savePreference("enable_instagram", isChecked));
        
        showMinimalRiskSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> 
            savePreference("show_minimal_risk", isChecked));
        
        sensitivitySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateSensitivityText(progress);
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                savePreference("sensitivity", seekBar.getProgress());
            }
        });
    }
    
    private void updateSensitivityText(int sensitivity) {
        String level;
        if (sensitivity < 25) {
            level = "Low (fewer warnings)";
        } else if (sensitivity < 75) {
            level = "Medium (balanced)";
        } else {
            level = "High (more warnings)";
        }
        sensitivityText.setText("Sensitivity: " + level);
    }
    
    private void savePreference(String key, boolean value) {
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putBoolean(key, value)
                .apply();
    }
    
    private void savePreference(String key, int value) {
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putInt(key, value)
                .apply();
    }
}
