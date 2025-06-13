package com.socialmediasafety.rating;

import java.util.List;

/**
 * Represents the result of a risk analysis for social media content.
 */
public class RiskAnalysis {
    private final int riskScore;
    private final RiskLevel riskLevel;
    private final List<String> riskFactors;
    private final Platform platform;
    private final long timestamp;
    
    public RiskAnalysis(int riskScore, RiskLevel riskLevel, List<String> riskFactors, Platform platform) {
        this.riskScore = Math.max(0, Math.min(100, riskScore)); // Clamp between 0-100
        this.riskLevel = riskLevel;
        this.riskFactors = riskFactors;
        this.platform = platform;
        this.timestamp = System.currentTimeMillis();
    }
    
    public int getRiskScore() {
        return riskScore;
    }
    
    public RiskLevel getRiskLevel() {
        return riskLevel;
    }
    
    public List<String> getRiskFactors() {
        return riskFactors;
    }
    
    public Platform getPlatform() {
        return platform;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public boolean hasRisk() {
        return riskScore > 0;
    }
    
    public String getRiskDescription() {
        switch (riskLevel) {
            case HIGH:
                return "High risk content detected. Exercise extreme caution.";
            case MEDIUM:
                return "Moderate risk detected. Be cautious before engaging.";
            case LOW:
                return "Low risk detected. Minor concerns identified.";
            case MINIMAL:
            default:
                return "Content appears safe.";
        }
    }
    
    @Override
    public String toString() {
        return String.format("RiskAnalysis{score=%d, level=%s, factors=%d, platform=%s}", 
                riskScore, riskLevel, riskFactors.size(), platform);
    }
}

/**
 * Enumeration of risk levels.
 */
enum RiskLevel {
    MINIMAL("Safe", "#28a745"),
    LOW("Low Risk", "#20c997"), 
    MEDIUM("Medium Risk", "#fd7e14"),
    HIGH("High Risk", "#dc3545");
    
    private final String displayName;
    private final String color;
    
    RiskLevel(String displayName, String color) {
        this.displayName = displayName;
        this.color = color;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getColor() {
        return color;
    }
}

/**
 * Enumeration of supported platforms.
 */
enum Platform {
    TWITTER("Twitter", "🐦"),
    REDDIT("Reddit", "🤖"),
    FACEBOOK("Facebook", "📘"),
    DISCORD("Discord", "💬"),
    INSTAGRAM("Instagram", "📷"),
    UNKNOWN("Unknown", "❓");
    
    private final String displayName;
    private final String emoji;
    
    Platform(String displayName, String emoji) {
        this.displayName = displayName;
        this.emoji = emoji;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getEmoji() {
        return emoji;
    }
}
