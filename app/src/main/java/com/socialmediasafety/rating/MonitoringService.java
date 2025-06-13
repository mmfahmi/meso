package com.socialmediasafety.rating;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import androidx.core.app.NotificationCompat;
import com.socialmediasafety.rating.analysis.RiskAnalysis;
import com.socialmediasafety.rating.analysis.RiskLevel;


public class MonitoringService extends AccessibilityService {
    private static final String TAG = "MonitoringService";
    private static final String CHANNEL_ID = "safety_monitoring_channel";
    private static final int NOTIFICATION_ID = 1001;

    private OverlayManager overlayManager;
    private ContentAnalyzer contentAnalyzer;

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG, "Accessibility service connected");

        // Configure accessibility service
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED |
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        info.flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS;
        info.packageNames = new String[]{
                "com.twitter.android",      // Twitter/X
                "com.reddit.frontpage",     // Reddit
                "com.facebook.katana",      // Facebook
                "com.discord",              // Discord
                "com.instagram.android"     // Instagram
        };
        setServiceInfo(info);

        // Initialize components
        overlayManager = new OverlayManager(this);
        contentAnalyzer = new ContentAnalyzer();

        // Start foreground notification
        startForegroundNotification();

        Log.d(TAG, "Monitoring service initialized");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null) return;

        String packageName = event.getPackageName() != null ?
                event.getPackageName().toString() : "";

        // Detect platform
        Platform platform = detectPlatform(packageName);
        if (platform == Platform.UNKNOWN) return;

        Log.d(TAG, "Detected platform: " + platform + " in package: " + packageName);

        // Extract content from screen
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode != null) {
            String extractedText = extractTextFromNode(rootNode);
            if (!extractedText.isEmpty()) {
                // Analyze content for risks
                RiskAnalysis analysis = contentAnalyzer.analyzeContent(extractedText, platform);

                // Show overlay with results
                overlayManager.showSafetyBadge(analysis, platform);

                Log.d(TAG, "Analysis complete. Risk level: " + analysis.getRiskLevel());
            }
            rootNode.recycle();
        }
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "Accessibility service interrupted");
        if (overlayManager != null) {
            overlayManager.hideAllOverlays();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (overlayManager != null) {
            overlayManager.cleanup();
        }
        Log.d(TAG, "Monitoring service destroyed");
    }

    private void startForegroundNotification() {
        createNotificationChannel();

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Social Media Safety Active")
                .setContentText("Monitoring Twitter, Reddit, Facebook, Discord for threats")
                .setSmallIcon(R.drawable.ic_shield)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .addAction(R.drawable.ic_settings, "Settings",
                        PendingIntent.getActivity(this, 1, notificationIntent,
                                PendingIntent.FLAG_IMMUTABLE))
                .build();

        startForeground(NOTIFICATION_ID, notification);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Social Media Safety Monitor",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Real-time monitoring of social media for safety threats");
            channel.setShowBadge(false);
            channel.enableLights(false);
            channel.enableVibration(false);

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private String extractTextFromNode(AccessibilityNodeInfo node) {
        if (node == null) return "";

        StringBuilder text = new StringBuilder();

        // Get text from current node
        if (node.getText() != null) {
            text.append(node.getText().toString()).append(" ");
        }

        // Get content description
        if (node.getContentDescription() != null) {
            text.append(node.getContentDescription().toString()).append(" ");
        }

        // Recursively extract from children
        for (int i = 0; i < node.getChildCount(); i++) {
            AccessibilityNodeInfo child = node.getChild(i);
            if (child != null) {
                text.append(extractTextFromNode(child));
                child.recycle();
            }
        }

        return text.toString().trim();
    }

    private Platform detectPlatform(String packageName) {
        switch (packageName) {
            case "com.twitter.android":
                return Platform.TWITTER;
            case "com.reddit.frontpage":
                return Platform.REDDIT;
            case "com.facebook.katana":
                return Platform.FACEBOOK;
            case "com.discord":
                return Platform.DISCORD;
            case "com.instagram.android":
                return Platform.INSTAGRAM;
            default:
                return Platform.UNKNOWN;
        }
    }
}