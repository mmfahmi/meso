package com.socialmediasafety.rating;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Main activity for the Social Media Safety Rating app.
 * Handles initial setup, permissions, and navigation to settings.
 */
public class MainActivity extends AppCompatActivity {
    
    private static final int OVERLAY_PERMISSION_REQUEST = 1;
    private static final int ACCESSIBILITY_PERMISSION_REQUEST = 2;
    
    private Button enableAccessibilityButton;
    private Button enableOverlayButton;
    private Button openSettingsButton;
    private TextView statusText;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initializeViews();
        setupClickListeners();
        updateUI();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }
    
    private void initializeViews() {
        enableAccessibilityButton = findViewById(R.id.enableAccessibilityButton);
        enableOverlayButton = findViewById(R.id.enableOverlayButton);
        openSettingsButton = findViewById(R.id.openSettingsButton);
        statusText = findViewById(R.id.statusText);
    }
    
    private void setupClickListeners() {
        enableAccessibilityButton.setOnClickListener(v -> openAccessibilitySettings());
        enableOverlayButton.setOnClickListener(v -> requestOverlayPermission());
        openSettingsButton.setOnClickListener(v -> openAppSettings());
    }
    
    private void updateUI() {
        boolean hasAccessibilityPermission = isAccessibilityServiceEnabled();
        boolean hasOverlayPermission = canDrawOverlays();
        
        enableAccessibilityButton.setEnabled(!hasAccessibilityPermission);
        enableOverlayButton.setEnabled(!hasOverlayPermission);
        
        if (hasAccessibilityPermission && hasOverlayPermission) {
            statusText.setText("✅ Social Media Safety Rating is active!\n\n" +
                    "The app is now monitoring your social media apps for potentially harmful content. " +
                    "Safety badges will appear automatically on risky posts.\n\n" +
                    "Supported apps:\n" +
                    "• Twitter/X\n" +
                    "• Reddit\n" +
                    "• Facebook\n" +
                    "• Discord\n" +
                    "• Instagram");
        } else if (!hasAccessibilityPermission && !hasOverlayPermission) {
            statusText.setText("⚠️ Setup Required\n\n" +
                    "Please enable both permissions below to start protecting yourself from harmful social media content:");
        } else if (!hasAccessibilityPermission) {
            statusText.setText("⚠️ Accessibility Permission Required\n\n" +
                    "Please enable the accessibility service to allow the app to monitor social media content.");
        } else {
            statusText.setText("⚠️ Overlay Permission Required\n\n" +
                    "Please enable overlay permission to display safety badges over other apps.");
        }
    }
    
    private void openAccessibilitySettings() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
        
        Toast.makeText(this, 
                "Find 'Social Media Safety Rating' in the list and enable it", 
                Toast.LENGTH_LONG).show();
    }
    
    private void requestOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST);
            }
        }
    }
    
    private void openAppSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
    
    private boolean isAccessibilityServiceEnabled() {
        // Check if our accessibility service is enabled
        String service = getPackageName() + "/" + SocialMediaAccessibilityService.class.getName();
        String enabledServices = Settings.Secure.getString(
                getContentResolver(),
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        
        return enabledServices != null && enabledServices.contains(service);
    }
    
    private boolean canDrawOverlays() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(this);
        }
        return true; // Assume permission granted for older versions
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == OVERLAY_PERMISSION_REQUEST) {
            if (canDrawOverlays()) {
                Toast.makeText(this, "Overlay permission granted!", Toast.LENGTH_SHORT).show();
                updateUI();
            } else {
                Toast.makeText(this, "Overlay permission is required for the app to work", 
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}
