package com.socialmediasafety.rating;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import com.socialmediasafety.rating.analysis.RiskAnalysis;
import com.socialmediasafety.rating.analysis.RiskLevel;

/**
 * Risk analysis engine that evaluates social media content for potential threats.
 * Ports the same analysis logic from the browser extension to Android.
 */
public class RiskAnalyzer {
    
    private static final String TAG = "RiskAnalyzer";
    
    // Risk pattern categories - same as browser extension
    private static final List<String> FINANCIAL_PATTERNS = Arrays.asList(
        "guaranteed profit", "make money fast", "investment opportunity",
        "double your money", "risk free", "limited time offer",
        "bitcoin", "crypto", "trading signals", "forex", "nft",
        "pump and dump", "moonshot", "diamond hands", "hodl",
        "passive income", "financial freedom"
    );
    
    private static final List<String> PHISHING_PATTERNS = Arrays.asList(
        "verify your account", "suspended account", "click here now",
        "update payment", "confirm identity", "security alert",
        "account locked", "login required", "verification needed",
        "urgent action required", "account will be closed"
    );
    
    private static final List<String> URGENCY_PATTERNS = Arrays.asList(
        "act now", "expires today", "limited spots", "dont miss out",
        "hurry up", "last chance", "while supplies last",
        "only today", "ends soon", "going fast", "final hours"
    );
    
    private static final List<String> SPAM_PATTERNS = Arrays.asList(
        "click here", "visit now", "free gift", "congratulations",
        "youve won", "claim now", "special offer", "act fast"
    );
    
    private static final List<String> BOT_PATTERNS = Arrays.asList(
        "dm me for details", "check my bio", "link in bio",
        "follow for follow", "rt for rt", "check comments",
        "dm for info", "message me", "see my profile"
    );
    
    private static final List<String> MALICIOUS_PATTERNS = Arrays.asList(
        "hack", "exploit", "bypass security", "leaked data",
        "cracked software", "free download", "keygen", "patch"
    );
    
    // Platform-specific patterns
    private static final List<String> TWITTER_ENGAGEMENT_BAIT = Arrays.asList(
        "rt if you agree", "like if", "retweet to save",
        "follow back", "1k followers", "mutual follow"
    );
    
    private static final List<String> REDDIT_KARMA_FARMING = Arrays.asList(
        "upvote if", "karma please", "need karma",
        "upvote this", "get to front page", "needs visibility"
    );
    
    private static final List<String> DISCORD_SCAM_PATTERNS = Arrays.asList(
        "free nitro", "discord gift", "steam gift",
        "join my server", "invite reward", "boost reward"
    );
    
    // URL patterns that might indicate suspicious links
    private static final Pattern SUSPICIOUS_URL_PATTERN = Pattern.compile(
        ".*(bit\\.ly|tinyurl|t\\.co|goo\\.gl|ow\\.ly|short\\.link).*",
        Pattern.CASE_INSENSITIVE
    );
    
    public RiskAnalysis analyzeContent(String text, Platform platform) {
        if (text == null || text.trim().isEmpty()) {
            return new RiskAnalysis(0, RiskLevel.MINIMAL, new ArrayList<>(), platform);
        }
        
        String lowercaseText = text.toLowerCase();
        List<String> riskFactors = new ArrayList<>();
        int riskScore = 0;
        
        // Check financial scam patterns
        int financialMatches = countPatternMatches(lowercaseText, FINANCIAL_PATTERNS);
        if (financialMatches > 0) {
            riskScore += financialMatches * 15;
            riskFactors.add("Financial scam indicators (" + financialMatches + " matches)");
        }
        
        // Check phishing patterns
        int phishingMatches = countPatternMatches(lowercaseText, PHISHING_PATTERNS);
        if (phishingMatches > 0) {
            riskScore += phishingMatches * 20;
            riskFactors.add("Phishing indicators (" + phishingMatches + " matches)");
        }
        
        // Check urgency manipulation
        int urgencyMatches = countPatternMatches(lowercaseText, URGENCY_PATTERNS);
        if (urgencyMatches > 0) {
            riskScore += urgencyMatches * 8;
            riskFactors.add("Urgency manipulation (" + urgencyMatches + " matches)");
        }
        
        // Check spam patterns
        int spamMatches = countPatternMatches(lowercaseText, SPAM_PATTERNS);
        if (spamMatches > 0) {
            riskScore += spamMatches * 10;
            riskFactors.add("Spam indicators (" + spamMatches + " matches)");
        }
        
        // Check bot patterns
        int botMatches = countPatternMatches(lowercaseText, BOT_PATTERNS);
        if (botMatches > 0) {
            riskScore += botMatches * 8;
            riskFactors.add("Bot-like behavior (" + botMatches + " matches)");
        }
        
        // Check malicious patterns
        int maliciousMatches = countPatternMatches(lowercaseText, MALICIOUS_PATTERNS);
        if (maliciousMatches > 0) {
            riskScore += maliciousMatches * 25;
            riskFactors.add("Malicious content indicators (" + maliciousMatches + " matches)");
        }
        
        // Platform-specific analysis
        riskScore += analyzePlatformSpecific(lowercaseText, platform, riskFactors);
        
        // Additional analysis
        riskScore += analyzeTextCharacteristics(text, riskFactors);
        riskScore += analyzeSuspiciousUrls(text, riskFactors);
        
        // Determine risk level
        RiskLevel riskLevel = determineRiskLevel(riskScore);
        
        // Cap at maximum score
        riskScore = Math.min(riskScore, 100);
        
        return new RiskAnalysis(riskScore, riskLevel, riskFactors, platform);
    }
    
    private int countPatternMatches(String text, List<String> patterns) {
        int matches = 0;
        for (String pattern : patterns) {
            if (text.contains(pattern)) {
                matches++;
            }
        }
        return matches;
    }
    
    private int analyzePlatformSpecific(String text, Platform platform, List<String> riskFactors) {
        int platformRiskScore = 0;
        
        switch (platform) {
            case TWITTER:
                int twitterMatches = countPatternMatches(text, TWITTER_ENGAGEMENT_BAIT);
                if (twitterMatches > 0) {
                    platformRiskScore += twitterMatches * 8;
                    riskFactors.add("Twitter engagement bait (" + twitterMatches + " matches)");
                }
                break;
                
            case REDDIT:
                int redditMatches = countPatternMatches(text, REDDIT_KARMA_FARMING);
                if (redditMatches > 0) {
                    platformRiskScore += redditMatches * 10;
                    riskFactors.add("Reddit karma farming (" + redditMatches + " matches)");
                }
                break;
                
            case DISCORD:
                int discordMatches = countPatternMatches(text, DISCORD_SCAM_PATTERNS);
                if (discordMatches > 0) {
                    platformRiskScore += discordMatches * 15;
                    riskFactors.add("Discord scam patterns (" + discordMatches + " matches)");
                }
                break;
                
            case FACEBOOK:
                // Facebook-specific patterns can be added here
                break;
                
            case INSTAGRAM:
                // Instagram-specific patterns can be added here
                break;
        }
        
        return platformRiskScore;
    }
    
    private int analyzeTextCharacteristics(String text, List<String> riskFactors) {
        int characteristicsScore = 0;
        
        // Check for excessive uppercase (shouting/spam indicator)
        if (text.length() > 20) {
            long uppercaseCount = text.chars().filter(Character::isUpperCase).count();
            double uppercaseRatio = (double) uppercaseCount / text.length();
            
            if (uppercaseRatio > 0.5) {
                characteristicsScore += 12;
                riskFactors.add("Excessive uppercase text");
            }
        }
        
        // Check for very short text with suspicious content
        if (text.length() < 30 && (text.contains("http") || text.contains("link"))) {
            characteristicsScore += 8;
            riskFactors.add("Short text with links");
        }
        
        // Check for excessive punctuation
        long punctuationCount = text.chars().filter(ch -> "!?.,;:".indexOf(ch) >= 0).count();
        if (punctuationCount > text.length() * 0.1 && text.length() > 10) {
            characteristicsScore += 6;
            riskFactors.add("Excessive punctuation");
        }
        
        return characteristicsScore;
    }
    
    private int analyzeSuspiciousUrls(String text, List<String> riskFactors) {
        int urlRiskScore = 0;
        
        // Check for shortened URLs
        if (SUSPICIOUS_URL_PATTERN.matcher(text).find()) {
            urlRiskScore += 15;
            riskFactors.add("Contains shortened URLs");
        }
        
        // Count total number of URLs
        long urlCount = text.split("\\s+").length - 
                       text.replaceAll("http[s]?://\\S+", "").split("\\s+").length;
        
        if (urlCount > 2) {
            urlRiskScore += Math.min(urlCount * 5, 20);
            riskFactors.add("Multiple URLs (" + urlCount + " links)");
        }
        
        return urlRiskScore;
    }
    
    private RiskLevel determineRiskLevel(int riskScore) {
        if (riskScore >= 60) {
            return RiskLevel.HIGH;
        } else if (riskScore >= 30) {
            return RiskLevel.MEDIUM;
        } else if (riskScore >= 10) {
            return RiskLevel.LOW;
        } else {
            return RiskLevel.MINIMAL;
        }
    }
}
