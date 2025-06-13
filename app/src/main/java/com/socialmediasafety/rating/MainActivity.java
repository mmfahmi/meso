package com.socialmediasafety.rating;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.socialmediasafety.rating.analysis.RiskAnalysis;
import com.socialmediasafety.rating.analysis.RiskLevel;


public class MainActivity extends Activity {

    private static final int REQUEST_OVERLAY_PERMISSION = 1001;
    private static final int REQUEST_ACCESSIBILITY_PERMISSION = 1002;

    private TextView statusText;
    private Button enableAccessibilityButton;
    private Button enableOverlayButton;
    private Button startServiceButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        updateStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStatus();
    }

    private void initViews() {
        statusText = findViewById(R.id.statusText);
        enableAccessibilityButton = findViewById(R.id.enableAccessibilityButton);
        enableOverlayButton = findViewById(R.id.enableOverlayButton);
        startServiceButton = findViewById(R.id.startServiceButton);

        enableAccessibilityButton.setOnClickListener(v -> openAccessibilitySettings());
        enableOverlayButton.setOnClickListener(v -> requestOverlayPermission());
        startServiceButton.setOnClickListener(v -> startProtection());
    }

    private void updateStatus() {
        boolean hasAccessibility = isAccessibilityServiceEnabled();
        boolean hasOverlay = canDrawOverlays();

        StringBuilder status = new StringBuilder();
        status.append("Meso Protection Setup\n\n");

        status.append("✓ Accessibility Service: ");
        status.append(hasAccessibility ? "ENABLED" : "DISABLED");
        status.append("\n");

        status.append("✓ Overlay Permission: ");
        status.append(hasOverlay ? "GRANTED" : "NOT GRANTED");
        status.append("\n\n");

        if (hasAccessibility) {
            if (hasOverlay) {
                status.append("✅ Full protection ready! Overlays enabled for visual feedback.");
            } else {
                status.append("✅ Basic protection ready! Enable overlay for visual feedback (optional).");
            }
            startServiceButton.setEnabled(true);
            startServiceButton.setText("Start Protection");
        } else {
            status.append("⚠️ Accessibility service required for protection to work.");
            startServiceButton.setEnabled(false);
            startServiceButton.setText("Setup Required");
        }

        statusText.setText(status.toString());

        enableAccessibilityButton.setEnabled(!hasAccessibility);
        enableOverlayButton.setEnabled(!hasOverlay);
    }

    private void openAccessibilitySettings() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivityForResult(intent, REQUEST_ACCESSIBILITY_PERMISSION);
        Toast.makeText(this, "Find 'Meso' in the list and enable it", Toast.LENGTH_LONG).show();
    }

    private void requestOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, REQUEST_OVERLAY_PERMISSION);
        }
    }

    private void startProtection() {
        // The MonitoringService is an AccessibilityService and starts automatically
        // when the accessibility service is enabled. We just need to inform the user.
        
        // Run a quick test to verify components are working
        TestHelper.testRiskAnalysis();
        TestHelper.testRiskLevels();
        
        Toast.makeText(this, "Meso Protection is active! The service will monitor social media apps. Check your notification bar.",
                Toast.LENGTH_LONG).show();
        finish(); // Close the setup activity
    }

    private boolean isAccessibilityServiceEnabled() {
        int accessibilityEnabled = 0;
        final String service = getPackageName() + "/" +
                "com.socialmediasafety.rating.MonitoringService";

        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    getApplicationContext().getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            return false;
        }

        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(
                    getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);

            if (settingValue != null) {
                return settingValue.contains(service);
            }
        }

        return false;
    }

    private boolean canDrawOverlays() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(this);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_OVERLAY_PERMISSION ||
                requestCode == REQUEST_ACCESSIBILITY_PERMISSION) {
            updateStatus();
        }
    }
}