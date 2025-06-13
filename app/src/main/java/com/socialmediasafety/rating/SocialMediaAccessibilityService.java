package com.socialmediasafety.rating;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.accessibilityservice.AccessibilityService;
import android.widget.TextView;
import androidx.core.app.NotificationCompat;

import com.socialmediasafety.rating.R;
import com.socialmediasafety.rating.MainActivity;
import com.socialmediasafety.rating.analysis.RiskAnalysis;
import com.socialmediasafety.rating.analysis.Platform;

public class SocialMediaAccessibilityService extends AccessibilityService {

    private static final String CHANNEL_ID = "meso_foreground_service";
    private static final int NOTIFICATION_ID = 1001;

    private WindowManager windowManager;
    private NotificationManager notificationManager;
    private boolean isServiceRunning = false;
    private int postsAnalyzed = 0;
    private int threatsDetected = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        createNotificationChannel();
        startForegroundService();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (!isServiceRunning) return;

        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode == null) return;

        String packageName = event.getPackageName().toString();
        Platform platform = detectPlatform(packageName);

        // Focus on Twitter for now
        if (platform == Platform.TWITTER) {
            analyzeTwitterPosts(rootNode);
        }

        rootNode.recycle();
    }

    @Override
    public void onInterrupt() {
        stopForegroundService();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForegroundService();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Meso Protection Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Monitors social media for scams and threats");
            channel.setShowBadge(false);
            channel.setSound(null, null);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void startForegroundService() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Meso Protection Active")
                .setContentText("Monitoring social media - " + postsAnalyzed + " posts analyzed")
                .setSmallIcon(R.drawable.ic_shield)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setColor(Color.GREEN)
                .addAction(R.drawable.ic_pause, "Pause", createPauseIntent())
                .addAction(R.drawable.ic_settings, "Settings", createSettingsIntent())
                .build();

        startForeground(NOTIFICATION_ID, notification);
        isServiceRunning = true;
    }

    private void updateNotification() {
        if (!isServiceRunning) return;

        String statusText = String.format("Analyzed: %d posts | Threats: %d",
                postsAnalyzed, threatsDetected);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Meso Protection Active")
                .setContentText(statusText)
                .setSmallIcon(R.drawable.ic_shield)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setColor(threatsDetected > 0 ? Color.YELLOW : Color.GREEN)
                .build();

        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private void stopForegroundService() {
        isServiceRunning = false;
        stopForeground(true);
    }

    private PendingIntent createPauseIntent() {
        Intent intent = new Intent(this, MesoServiceReceiver.class);
        intent.setAction("PAUSE_SERVICE");
        return PendingIntent.getBroadcast(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    private PendingIntent createSettingsIntent() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction("OPEN_SETTINGS");
        return PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    private void analyzeTwitterPosts(AccessibilityNodeInfo rootNode) {
        // Find Twitter post containers
        findTwitterPosts(rootNode);
    }

    private void findTwitterPosts(AccessibilityNodeInfo node) {
        if (node == null) return;

        // Twitter post detection - look for tweet containers
        String resourceId = node.getViewIdResourceName();
        String className = node.getClassName() != null ? node.getClassName().toString() : "";

        // Common Twitter post identifiers
        if (resourceId != null && (
                resourceId.contains("tweet") ||
                        resourceId.contains("status") ||
                        resourceId.contains("timeline"))) {

            String postText = extractTextFromNode(node);
            if (!postText.isEmpty()) {
                analyzeAndOverlayPost(node, postText);
            }
        }

        // Recursively check child nodes
        for (int i = 0; i < node.getChildCount(); i++) {
            AccessibilityNodeInfo child = node.getChild(i);
            if (child != null) {
                findTwitterPosts(child);
                child.recycle();
            }
        }
    }

    private void analyzeAndOverlayPost(AccessibilityNodeInfo postNode, String text) {
        RiskAnalysis analysis = analyzeContent(text, Platform.TWITTER);
        postsAnalyzed++;

        // Determine if it's Clean or Scam
        boolean isScam = analysis.getTotalRiskScore() > 0.6; // Threshold for scam detection
        if (isScam) {
            threatsDetected++;
        }

        // Create overlay
        createPostOverlay(postNode, isScam ? "SCAM" : "CLEAN", isScam);

        // Update notification every 10 posts
        if (postsAnalyzed % 10 == 0) {
            updateNotification();
        }
    }

    private void createPostOverlay(AccessibilityNodeInfo postNode, String label, boolean isScam) {
        // Get post position on screen
        android.graphics.Rect bounds = new android.graphics.Rect();
        postNode.getBoundsInScreen(bounds);

        // Create overlay view
        View overlayView = LayoutInflater.from(this).inflate(R.layout.post_overlay, null);
        TextView labelView = overlayView.findViewById(R.id.labelText);

        labelView.setText(label);
        labelView.setBackgroundColor(isScam ? Color.RED : Color.GREEN);
        labelView.setTextColor(Color.WHITE);

        // Set overlay parameters
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                        WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT
        );

        // Position overlay at top-right of post
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = bounds.right - 150; // 150px from right edge
        params.y = bounds.top + 20;    // 20px from top

        try {
            windowManager.addView(overlayView, params);

            // Auto-remove overlay after 5 seconds
            overlayView.postDelayed(() -> {
                try {
                    windowManager.removeView(overlayView);
                } catch (Exception e) {
                    // View might already be removed
                }
            }, 5000);

        } catch (Exception e) {
            // Handle overlay permission issues
            e.printStackTrace();
        }
    }

    private String extractTextFromNode(AccessibilityNodeInfo node) {
        StringBuilder text = new StringBuilder();
        if (node.getText() != null) {
            text.append(node.getText().toString());
        }

        for (int i = 0; i < node.getChildCount(); i++) {
            AccessibilityNodeInfo child = node.getChild(i);
            if (child != null) {
                text.append(" ").append(extractTextFromNode(child));
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
            default:
                return Platform.UNKNOWN;
        }
    }

    private RiskAnalysis analyzeContent(String text, Platform platform) {
        RiskAnalysis analysis = new RiskAnalysis();

        // Basic scam detection patterns
        text = text.toLowerCase();

        // Financial scam indicators
        if (text.contains("crypto") && (text.contains("guaranteed") || text.contains("profit"))) {
            analysis.addRisk("Crypto scam indicators", 0.8);
        }

        if (text.contains("bitcoin") && text.contains("double")) {
            analysis.addRisk("Bitcoin doubling scam", 0.9);
        }

        if (text.contains("click here") && text.contains("win")) {
            analysis.addRisk("Click-bait scam", 0.7);
        }

        // Phishing indicators
        if (text.contains("verify") && text.contains("account")) {
            analysis.addRisk("Account verification phishing", 0.8);
        }

        if (text.contains("suspended") && text.contains("click")) {
            analysis.addRisk("Account suspension phishing", 0.9);
        }

        // Urgency indicators
        if (text.contains("urgent") || text.contains("limited time")) {
            analysis.addRisk("Urgency manipulation", 0.6);
        }

        // Giveaway scams
        if (text.contains("giveaway") && text.contains("retweet")) {
            analysis.addRisk("Fake giveaway", 0.7);
        }

        return analysis;
    }
}