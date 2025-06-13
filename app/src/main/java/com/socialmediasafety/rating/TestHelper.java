package com.socialmediasafety.rating;

import android.util.Log;
import com.socialmediasafety.rating.analysis.RiskAnalysis;
import com.socialmediasafety.rating.analysis.RiskLevel;

/**
 * Helper class for testing the risk analysis functionality
 */
public class TestHelper {
    private static final String TAG = "TestHelper";
    
    public static void testRiskAnalysis() {
        ContentAnalyzer analyzer = new ContentAnalyzer();
        
        // Test financial scam
        String financialScam = "GUARANTEED PROFITS! Make $5000 a day with Bitcoin! NO RISK! Click here now!";
        RiskAnalysis analysis1 = analyzer.analyzeContent(financialScam, Platform.TWITTER);
        Log.d(TAG, "Financial scam test - Risk Level: " + analysis1.getRiskLevel() + ", Score: " + analysis1.getRiskScore());
        
        // Test phishing
        String phishing = "URGENT: Your account has been suspended! Click here to verify your password immediately!";
        RiskAnalysis analysis2 = analyzer.analyzeContent(phishing, Platform.FACEBOOK);
        Log.d(TAG, "Phishing test - Risk Level: " + analysis2.getRiskLevel() + ", Score: " + analysis2.getRiskScore());
        
        // Test safe content
        String safeContent = "Just had a great coffee this morning. Hope everyone has a wonderful day!";
        RiskAnalysis analysis3 = analyzer.analyzeContent(safeContent, Platform.TWITTER);
        Log.d(TAG, "Safe content test - Risk Level: " + analysis3.getRiskLevel() + ", Score: " + analysis3.getRiskScore());
        
        // Test platform detection
        Log.d(TAG, "Platform emoji tests:");
        Log.d(TAG, "Twitter: " + Platform.TWITTER.getEmoji());
        Log.d(TAG, "Reddit: " + Platform.REDDIT.getEmoji());
        Log.d(TAG, "Facebook: " + Platform.FACEBOOK.getEmoji());
        Log.d(TAG, "Discord: " + Platform.DISCORD.getEmoji());
        Log.d(TAG, "Instagram: " + Platform.INSTAGRAM.getEmoji());
    }
    
    public static void testRiskLevels() {
        Log.d(TAG, "Risk level display names:");
        for (RiskLevel level : RiskLevel.values()) {
            Log.d(TAG, level.name() + " -> " + level.getDisplayName());
        }
    }
}
