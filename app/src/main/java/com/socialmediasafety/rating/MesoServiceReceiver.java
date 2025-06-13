package com.socialmediasafety.rating;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import com.socialmediasafety.rating.analysis.RiskAnalysis;
import com.socialmediasafety.rating.analysis.RiskLevel;


public class MesoServiceReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if ("PAUSE_SERVICE".equals(action)) {
            // Toggle service pause/resume
            Intent serviceIntent = new Intent(context, MesoAccessibilityService.class);
            serviceIntent.setAction("TOGGLE_PAUSE");
            context.startService(serviceIntent);

        } else if ("OPEN_SETTINGS".equals(action)) {
            // Open accessibility settings
            Intent settingsIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(settingsIntent);
        }
    }
}