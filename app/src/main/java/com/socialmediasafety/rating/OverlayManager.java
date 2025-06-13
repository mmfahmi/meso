package com.socialmediasafety.rating;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.socialmediasafety.rating.analysis.RiskAnalysis;
import com.socialmediasafety.rating.analysis.RiskLevel;


/**
 * Manages overlay badges that display safety ratings on top of social media content.
 */
public class OverlayManager {
    
    private static final String TAG = "OverlayManager";
    private static final int BADGE_DISPLAY_DURATION = 10000; // 10 seconds
    
    private final Context context;
    private final WindowManager windowManager;
    private final List<OverlayBadge> activeBadges;
    
    public OverlayManager(Context context) {
        this.context = context;
        this.windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        this.activeBadges = new ArrayList<>();
    }
    
    public void showRatingBadge(Rect contentBounds, RiskAnalysis analysis) {
        // Clean up expired badges first
        cleanupExpiredBadges();
        
        // Don't show badges for minimal risk unless configured otherwise
        if (analysis.getRiskLevel() == RiskLevel.MINIMAL && !shouldShowMinimalRisk()) {
            return;
        }
        
        try {
            OverlayBadge badge = createRatingBadge(contentBounds, analysis);
            windowManager.addView(badge.getView(), badge.getLayoutParams());
            activeBadges.add(badge);
            
            Log.d(TAG, "Displayed rating badge: " + analysis.getRiskLevel());
            
        } catch (Exception e) {
            Log.e(TAG, "Failed to show rating badge", e);
        }
    }
    
    private OverlayBadge createRatingBadge(Rect contentBounds, RiskAnalysis analysis) {
        // Create badge view
        View badgeView = createBadgeView(analysis);
        
        // Calculate position (top-right of content)
        int x = contentBounds.right - 120; // Badge width consideration
        int y = contentBounds.top + 10;
        
        // Create layout parameters for overlay
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                getOverlayType(),
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | 
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT
        );
        
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = x;
        params.y = y;
        
        return new OverlayBadge(badgeView, params, analysis, System.currentTimeMillis());
    }
    
    private View createBadgeView(RiskAnalysis analysis) {
        // Create container
        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(12, 8, 12, 8);
        container.setBackground(createBadgeBackground(analysis.getRiskLevel()));
        
        // Risk level text
        TextView riskText = new TextView(context);
        riskText.setText(analysis.getRiskLevel().getDisplayName().toUpperCase());
        riskText.setTextColor(Color.WHITE);
        riskText.setTextSize(10);
        riskText.setGravity(Gravity.CENTER);
        
        // Risk score text
        TextView scoreText = new TextView(context);
        scoreText.setText(analysis.getRiskScore() + "/100");
        scoreText.setTextColor(Color.WHITE);
        scoreText.setTextSize(8);
        scoreText.setGravity(Gravity.CENTER);
        scoreText.setAlpha(0.8f);
        
        container.addView(riskText);
        container.addView(scoreText);
        
        // Add platform emoji if available
        if (analysis.getPlatform() != Platform.UNKNOWN) {
            TextView platformText = new TextView(context);
            platformText.setText(analysis.getPlatform().getEmoji());
            platformText.setTextSize(8);
            platformText.setGravity(Gravity.CENTER);
            container.addView(platformText);
        }
        
        // Add click listener for detailed view
        container.setOnClickListener(v -> showDetailedAnalysis(analysis));
        
        return container;
    }
    
    private android.graphics.drawable.Drawable createBadgeBackground(RiskLevel riskLevel) {
        android.graphics.drawable.GradientDrawable drawable = new android.graphics.drawable.GradientDrawable();
        drawable.setShape(android.graphics.drawable.GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(12);
        
        int color;
        switch (riskLevel) {
            case HIGH:
                color = Color.parseColor("#dc3545");
                break;
            case MEDIUM:
                color = Color.parseColor("#fd7e14");
                break;
            case LOW:
                color = Color.parseColor("#20c997");
                break;
            case MINIMAL:
            default:
                color = Color.parseColor("#28a745");
                break;
        }
        
        drawable.setColor(color);
        drawable.setAlpha(220); // Slight transparency
        
        return drawable;
    }
    
    private void showDetailedAnalysis(RiskAnalysis analysis) {
        // Create detailed popup - this could be a new activity or dialog
        Log.d(TAG, "Showing detailed analysis: " + analysis);
        
        // For now, just log the details - in a full implementation,
        // this would show a detailed popup with all risk factors
        for (String factor : analysis.getRiskFactors()) {
            Log.d(TAG, "Risk factor: " + factor);
        }
    }
    
    private int getOverlayType() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            return WindowManager.LayoutParams.TYPE_PHONE;
        }
    }
    
    private boolean shouldShowMinimalRisk() {
        // This could be a user preference
        return false; // Default: don't show minimal risk badges
    }
    
    private void cleanupExpiredBadges() {
        long currentTime = System.currentTimeMillis();
        Iterator<OverlayBadge> iterator = activeBadges.iterator();
        
        while (iterator.hasNext()) {
            OverlayBadge badge = iterator.next();
            if (currentTime - badge.getCreationTime() > BADGE_DISPLAY_DURATION) {
                try {
                    windowManager.removeView(badge.getView());
                    iterator.remove();
                    Log.d(TAG, "Removed expired badge");
                } catch (Exception e) {
                    Log.e(TAG, "Failed to remove expired badge", e);
                }
            }
        }
    }
    
    public void cleanup() {
        Log.d(TAG, "Cleaning up all overlay badges");
        
        for (OverlayBadge badge : activeBadges) {
            try {
                windowManager.removeView(badge.getView());
            } catch (Exception e) {
                Log.e(TAG, "Failed to remove badge during cleanup", e);
            }
        }
        
        activeBadges.clear();
    }
    
    /**
     * Inner class to represent an overlay badge with its associated data.
     */
    private static class OverlayBadge {
        private final View view;
        private final WindowManager.LayoutParams layoutParams;
        private final RiskAnalysis analysis;
        private final long creationTime;
        
        public OverlayBadge(View view, WindowManager.LayoutParams layoutParams, 
                           RiskAnalysis analysis, long creationTime) {
            this.view = view;
            this.layoutParams = layoutParams;
            this.analysis = analysis;
            this.creationTime = creationTime;
        }
        
        public View getView() { return view; }
        public WindowManager.LayoutParams getLayoutParams() { return layoutParams; }
        public RiskAnalysis getAnalysis() { return analysis; }
        public long getCreationTime() { return creationTime; }
    }
}
