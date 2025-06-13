package com.socialmediasafety.rating;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.graphics.Rect;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Accessibility Service that monitors social media apps for potentially harmful content
 * and displays safety ratings using overlay badges.
 */
public class SocialMediaAccessibilityService extends AccessibilityService {
    
    private static final String TAG = "SocialMediaAccess";
    
    // Supported social media package names
    private static final Set<String> SUPPORTED_PACKAGES = new HashSet<String>() {{
        add("com.twitter.android");
        add("com.reddit.frontpage");
        add("com.facebook.katana");
        add("com.discord");
        add("com.instagram.android");
    }};
    
    private RiskAnalyzer riskAnalyzer;
    private OverlayManager overlayManager;
    private ExecutorService executor;
    private boolean isEnabled = false;
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Social Media Accessibility Service created");
        
        riskAnalyzer = new RiskAnalyzer();
        overlayManager = new OverlayManager(this);
        executor = Executors.newSingleThreadExecutor();
        
        // Initialize service components
        initializeService();
    }
    
    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG, "Accessibility service connected");
        
        // Configure service info
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED | 
                         AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        info.flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS |
                    AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS;
        info.notificationTimeout = 100;
        
        setServiceInfo(info);
        isEnabled = true;
    }
    
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (!isEnabled) return;
        
        String packageName = event.getPackageName() != null ? 
                           event.getPackageName().toString() : "";
        
        // Only process supported social media apps
        if (!SUPPORTED_PACKAGES.contains(packageName)) {
            return;
        }
        
        Log.d(TAG, "Processing event for: " + packageName);
        
        // Process the event asynchronously to avoid blocking
        executor.submit(() -> processAccessibilityEvent(event, packageName));
    }
    
    private void processAccessibilityEvent(AccessibilityEvent event, String packageName) {
        try {
            AccessibilityNodeInfo rootNode = getRootInActiveWindow();
            if (rootNode == null) return;
            
            Platform platform = detectPlatform(packageName);
            List<ContentNode> contentNodes = extractContentNodes(rootNode, platform);
            
            for (ContentNode contentNode : contentNodes) {
                analyzeAndDisplayRating(contentNode, platform);
            }
            
            rootNode.recycle();
            
        } catch (Exception e) {
            Log.e(TAG, "Error processing accessibility event", e);
        }
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
    
    private List<ContentNode> extractContentNodes(AccessibilityNodeInfo rootNode, Platform platform) {
        List<ContentNode> contentNodes = new ArrayList<>();
        
        switch (platform) {
            case TWITTER:
                extractTwitterContent(rootNode, contentNodes);
                break;
            case REDDIT:
                extractRedditContent(rootNode, contentNodes);
                break;
            case FACEBOOK:
                extractFacebookContent(rootNode, contentNodes);
                break;
            case DISCORD:
                extractDiscordContent(rootNode, contentNodes);
                break;
        }
        
        return contentNodes;
    }
    
    private void extractTwitterContent(AccessibilityNodeInfo node, List<ContentNode> contentNodes) {
        // Look for tweet content containers
        if (node.getClassName() != null && 
            (node.getClassName().toString().contains("Tweet") ||
             node.getViewIdResourceName() != null && 
             node.getViewIdResourceName().contains("tweet"))) {
            
            String text = extractTextFromNode(node);
            if (text.length() > 10) { // Minimum text length
                Rect bounds = new Rect();
                node.getBoundsInScreen(bounds);
                contentNodes.add(new ContentNode(text, bounds, Platform.TWITTER));
            }
        }
        
        // Recursively search child nodes
        for (int i = 0; i < node.getChildCount(); i++) {
            AccessibilityNodeInfo child = node.getChild(i);
            if (child != null) {
                extractTwitterContent(child, contentNodes);
                child.recycle();
            }
        }
    }
    
    private void extractRedditContent(AccessibilityNodeInfo node, List<ContentNode> contentNodes) {
        // Look for Reddit post containers
        if (node.getClassName() != null && 
            (node.getClassName().toString().contains("Post") ||
             node.getViewIdResourceName() != null && 
             (node.getViewIdResourceName().contains("post") ||
              node.getViewIdResourceName().contains("title")))) {
            
            String text = extractTextFromNode(node);
            if (text.length() > 10) {
                Rect bounds = new Rect();
                node.getBoundsInScreen(bounds);
                contentNodes.add(new ContentNode(text, bounds, Platform.REDDIT));
            }
        }
        
        for (int i = 0; i < node.getChildCount(); i++) {
            AccessibilityNodeInfo child = node.getChild(i);
            if (child != null) {
                extractRedditContent(child, contentNodes);
                child.recycle();
            }
        }
    }
    
    private void extractFacebookContent(AccessibilityNodeInfo node, List<ContentNode> contentNodes) {
        // Facebook content extraction logic
        if (node.getText() != null && node.getText().length() > 10) {
            Rect bounds = new Rect();
            node.getBoundsInScreen(bounds);
            if (bounds.width() > 200 && bounds.height() > 50) { // Reasonable post size
                contentNodes.add(new ContentNode(node.getText().toString(), bounds, Platform.FACEBOOK));
            }
        }
        
        for (int i = 0; i < node.getChildCount(); i++) {
            AccessibilityNodeInfo child = node.getChild(i);
            if (child != null) {
                extractFacebookContent(child, contentNodes);
                child.recycle();
            }
        }
    }
    
    private void extractDiscordContent(AccessibilityNodeInfo node, List<ContentNode> contentNodes) {
        // Discord message extraction
        if (node.getClassName() != null && 
            (node.getClassName().toString().contains("Message") ||
             node.getViewIdResourceName() != null && 
             node.getViewIdResourceName().contains("message"))) {
            
            String text = extractTextFromNode(node);
            if (text.length() > 5) {
                Rect bounds = new Rect();
                node.getBoundsInScreen(bounds);
                contentNodes.add(new ContentNode(text, bounds, Platform.DISCORD));
            }
        }
        
        for (int i = 0; i < node.getChildCount(); i++) {
            AccessibilityNodeInfo child = node.getChild(i);
            if (child != null) {
                extractDiscordContent(child, contentNodes);
                child.recycle();
            }
        }
    }
    
    private String extractTextFromNode(AccessibilityNodeInfo node) {
        StringBuilder text = new StringBuilder();
        
        if (node.getText() != null) {
            text.append(node.getText().toString().trim());
        }
        
        if (node.getContentDescription() != null) {
            text.append(" ").append(node.getContentDescription().toString().trim());
        }
        
        // Recursively extract text from children
        for (int i = 0; i < node.getChildCount(); i++) {
            AccessibilityNodeInfo child = node.getChild(i);
            if (child != null) {
                String childText = extractTextFromNode(child);
                if (!childText.isEmpty()) {
                    text.append(" ").append(childText);
                }
                child.recycle();
            }
        }
        
        return text.toString().trim();
    }
    
    private void analyzeAndDisplayRating(ContentNode contentNode, Platform platform) {
        // Analyze content for risks
        RiskAnalysis analysis = riskAnalyzer.analyzeContent(contentNode.getText(), platform);
        
        // Only show badges for content with some risk
        if (analysis.getRiskScore() > 0) {
            overlayManager.showRatingBadge(contentNode.getBounds(), analysis);
            Log.d(TAG, String.format("Displayed %s risk badge for %s content", 
                  analysis.getRiskLevel(), platform.name()));
        }
    }
    
    private void initializeService() {
        // Start overlay service
        Intent overlayIntent = new Intent(this, OverlayService.class);
        startService(overlayIntent);
        
        // Start monitoring service
        Intent monitoringIntent = new Intent(this, MonitoringService.class);
        startService(monitoringIntent);
    }
    
    @Override
    public void onInterrupt() {
        Log.d(TAG, "Accessibility service interrupted");
        isEnabled = false;
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Accessibility service destroyed");
        
        isEnabled = false;
        
        if (executor != null) {
            executor.shutdown();
        }
        
        if (overlayManager != null) {
            overlayManager.cleanup();
        }
        
        // Stop services
        stopService(new Intent(this, OverlayService.class));
        stopService(new Intent(this, MonitoringService.class));
    }
    
    /**
     * Inner class to represent extracted content with its screen position
     */
    private static class ContentNode {
        private final String text;
        private final Rect bounds;
        private final Platform platform;
        
        public ContentNode(String text, Rect bounds, Platform platform) {
            this.text = text;
            this.bounds = bounds;
            this.platform = platform;
        }
        
        public String getText() { return text; }
        public Rect getBounds() { return bounds; }
        public Platform getPlatform() { return platform; }
    }
}
