package com.socialmediasafety.rating;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

public class MesoServiceReceiver extends BroadcastReceiver {

    private static final String TAG = "MesoServiceReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "Received action: " + action);

        if ("PAUSE_SERVICE".equals(action)) {
            // Note: AccessibilityService cannot be paused/resumed programmatically
            // This would need to be implemented within the MonitoringService itself
            Log.d(TAG, "Pause/Resume action received - not implemented for AccessibilityService");

        } else if ("OPEN_SETTINGS".equals(action)) {
            // Open accessibility settings
            Intent settingsIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(settingsIntent);
            Log.d(TAG, "Opening accessibility settings");
        }
    }
}