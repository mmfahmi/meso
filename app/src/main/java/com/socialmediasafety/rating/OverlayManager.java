package com.socialmediasafety.rating;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.ImageView;

import com.socialmediasafety.rating.analysis.RiskAnalysis;

public class OverlayManager {

    private static final String TAG = "OverlayManager";
    private final Context context;
    private final WindowManager windowManager;
    private View badgeView;

    public OverlayManager(Context context) {
        this.context = context;
        this.windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    public void showSafetyBadge(RiskAnalysis analysis, Platform platform) {
        if (context == null || windowManager == null) {
            Log.w(TAG, "OverlayManager not properly initialized");
            return;
        }

        // Check if overlay permission is granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(context)) {
                Log.d(TAG, "Overlay permission not granted, skipping badge display");
                return;
            }
        }

        hideAllOverlays(); // Clear existing overlays before showing new one

        try {
            LayoutInflater inflater = LayoutInflater.from(context);
            badgeView = inflater.inflate(R.layout.overlay_safety_badge, null);

            TextView riskText = badgeView.findViewById(R.id.riskText);
            TextView platformText = badgeView.findViewById(R.id.platformText);
            ImageView badgeIcon = badgeView.findViewById(R.id.badgeIcon);

            // Populate badge
            riskText.setText(analysis.getRiskLevel().getDisplayName());
            platformText.setText(platform.getEmoji() + " " + platform.name());
            badgeIcon.setImageResource(R.drawable.ic_shield); // Replace with your own icon

            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                            WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    PixelFormat.TRANSLUCENT
            );

            params.gravity = Gravity.TOP | Gravity.END;
            params.x = 20;
            params.y = 100;

            windowManager.addView(badgeView, params);
            Log.d(TAG, "Safety badge displayed for " + platform + " with risk level " + analysis.getRiskLevel());
        } catch (Exception e) {
            Log.e(TAG, "Error displaying safety badge", e);
        }
    }

    public void hideAllOverlays() {
        if (badgeView != null && windowManager != null) {
            try {
                windowManager.removeView(badgeView);
            } catch (Exception e) {
                e.printStackTrace();
            }
            badgeView = null;
        }
    }

    public void cleanup() {
        hideAllOverlays();
    }
}
