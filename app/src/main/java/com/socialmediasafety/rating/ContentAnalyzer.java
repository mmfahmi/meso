package com.socialmediasafety.rating;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ContentAnalyzer {
    private static final String TAG = "ContentAnalyzer";

    // Financial risk patterns
    private static final Pattern[] FINANCIAL_PATTERNS = {
            Pattern.compile("(?i)\\b(bitcoin|btc|ethereum|eth|crypto|cryptocurrency)\\b.*\\b(invest|investment|profit|returns?)\\b"),
            Pattern.compile("(?i)\\b(make money|earn money|quick money|easy money)\\b.*\\b(home|online|fast)\\b"),
            Pattern.compile("(?i)\\b(forex|trading|trader|trade)\\b.*\\b(signals?|tips?|guaranteed)\\b"),
            Pattern.compile("(?i)\\b(pyramid|ponzi|mlm|multi.?level)\\b"),
            Pattern.compile("(?i)\\b(get rich|financial freedom)\\b.*\\b(quick|fast|easy)\\b")
    };

    // Phishing patterns
    private static final Pattern[] PHISHING_PATTERNS = {
            Pattern.compile("(?i)\\b(click here|click now|act now|limited time)\\b"),
            Pattern.compile("(?i)\\b(verify|confirm|update)\\b.*\\b(account|password|payment)\\b"),
            Pattern.compile("(?i)\\b(suspended|expired|locked)\\b.*\\b(account|access)\\b"),
            Pattern.compile("(?i)\\b(winner|won|congratulations)\\b.*\\b(prize|money|gift)\\b"),
            Pattern.compile("(?i)\\b(urgent|immediate|asap)\\b.*\\b(action|response)\\b")
    };

    // Spam patterns
    private static final Pattern[] SPAM_PATTERNS = {
            Pattern.compile("(?i)\\b(dm me|message me|contact me)\\b.*\\b(more info|details|how)\\b"),
            Pattern.compile("(?i)\\b(follow|like|share|retweet)\\b.*\\b(win|chance|opportunity)\\b"),
            Pattern.compile("(?i)\\b(free|100% free|completely free)\\b.*\\b(no cost|no charge|no fee)\\b"),
            Pattern.compile("(?i)\\b(work from home|make money online)\\b"),
            Pattern.compile("(?i)\\b(amazing|incredible|unbelievable)\\b.*\\b(opportunity|offer|deal)\\b")
    };

    // Suspicious URL patterns
    private static final Pattern[] URL_PATTERNS = {
            Pattern.compile("(?i)\\b(bit\\.ly|tinyurl|short\\.link|t\\.co)\\b"),
            Pattern.compile("(?i)\\b\\w+\\.(tk|ml|ga|cf)\\b"),
            Pattern.compile("(?i)\\b\\w+\\d+\\.(com|net|org)\\b")
    };

    public RiskAnalysis analyzeContent(String text, Platform platform) {
        RiskAnalysis analysis = new RiskAnalysis();

        if (text == null || text.trim().isEmpty()) {
            return analysis;
        }

        // Analyze different risk categories
        analysis.setFinancialRisk(analyzeFinancialRisk(text));
        analysis.setPhishingRisk(analyzePhishingRisk(text));
        analysis.setSpamRisk(analyzeSpamRisk(text));
        analysis.setUrlRisk(analyzeUrlRisk(text));

        // Platform-specific analysis
        switch (platform) {
            case TWITTER:
                analysis.addTwitterSpecificRisks(text);
                break;
            case REDDIT:
                analysis.addRedditSpecificRisks(text);
                break;
            case FACEBOOK:
                analysis.addFacebookSpecificRisks(text);
                break;
            case DISCORD:
                analysis.addDiscordSpecificRisks(text);
                break;
            case INSTAGRAM:
                analysis.addInstagramSpecificRisks(text);
                break;
        }

        // Calculate overall risk level
        analysis.calculateOverallRisk();

        return analysis;
    }

    private int analyzeFinancialRisk(String text) {
        int riskScore = 0;

        for (Pattern pattern : FINANCIAL_PATTERNS) {
            if (pattern.matcher(text).find()) {
                riskScore += 25;
            }
        }

        // Additional financial keywords
        if (text.toLowerCase().contains("guaranteed profit")) riskScore += 30;
        if (text.toLowerCase().contains("risk free")) riskScore += 25;
        if (text.toLowerCase().contains("100% returns")) riskScore += 35;

        return Math.min(riskScore, 100);
    }

    private int analyzePhishingRisk(String text) {
        int riskScore = 0;

        for (Pattern pattern : PHISHING_PATTERNS) {
            if (pattern.matcher(text).find()) {
                riskScore += 20;
            }
        }

        // Check for urgency indicators
        if (text.toLowerCase().contains("expires today")) riskScore += 25;
        if (text.toLowerCase().contains("act fast")) riskScore += 20;
        if (text.toLowerCase().contains("limited spots")) riskScore += 15;

        return Math.min(riskScore, 100);
    }

    private int analyzeSpamRisk(String text) {
        int riskScore = 0;

        for (Pattern pattern : SPAM_PATTERNS) {
            if (pattern.matcher(text).find()) {
                riskScore += 15;
            }
        }

        // Check for excessive punctuation/caps
        long exclamationCount = text.chars().filter(ch -> ch == '!').count();
        if (exclamationCount > 3) riskScore += 10;

        long capsCount = text.chars().filter(Character::isUpperCase).count();
        if (capsCount > text.length() * 0.3) riskScore += 15;

        return Math.min(riskScore, 100);
    }

    private int analyzeUrlRisk(String text) {
        int riskScore = 0;

        for (Pattern pattern : URL_PATTERNS) {
            if (pattern.matcher(text).find()) {
                riskScore += 20;
            }
        }

        // Count total URLs
        long urlCount = Pattern.compile("https?://\\S+").matcher(text).results().count();
        if (urlCount > 2) riskScore += 15;

        return Math.min(riskScore, 100);
    }
}

class RiskAnalysis {
    private int financialRisk = 0;
    private int phishingRisk = 0;
    private int spamRisk = 0;
    private int urlRisk = 0;
    private int overallRisk = 0;
    private List<String> warnings = new ArrayList<>();
    private RiskLevel riskLevel = RiskLevel.SAFE;

    // Getters and setters
    public int getFinancialRisk() { return financialRisk; }
    public void setFinancialRisk(int risk) { this.financialRisk = risk; }

    public int getPhishingRisk() { return phishingRisk; }
    public void setPhishingRisk(int risk) { this.phishingRisk = risk; }

    public int getSpamRisk() { return spamRisk; }
    public void setSpamRisk(int risk) { this.spamRisk = risk; }

    public int getUrlRisk() { return urlRisk; }
    public void setUrlRisk(int risk) { this.urlRisk = risk; }

    public int getOverallRisk() { return overallRisk; }
    public RiskLevel getRiskLevel() { return riskLevel; }
    public List<String> getWarnings() { return warnings; }

    public void calculateOverallRisk() {
        // Weighted calculation of overall risk
        overallRisk = (int) (financialRisk * 0.3 + phishingRisk * 0.3 +
                spamRisk * 0.2 + urlRisk * 0.2);

        // Determine risk level
        if (overallRisk < 20) {
            riskLevel = RiskLevel.SAFE;
        } else if (overallRisk < 40) {
            riskLevel = RiskLevel.LOW;
        } else if (overallRisk < 70) {
            riskLevel = RiskLevel.MEDIUM;
        } else {
            riskLevel = RiskLevel.HIGH;
        }

        // Generate warnings
        generateWarnings();
    }

    private void generateWarnings() {
        warnings.clear();

        if (financialRisk > 30) {
            warnings.add("⚠️ Financial scam indicators detected");
        }
        if (phishingRisk > 30) {
            warnings.add("🎣 Phishing attempt suspected");
        }
        if (spamRisk > 30) {
            warnings.add("📧 Spam-like content detected");
        }
        if (urlRisk > 30) {
            warnings.add("🔗 Suspicious links found");
        }
    }

    // Platform-specific risk analysis methods
    public void addTwitterSpecificRisks(String text) {
        if (text.contains("RT to win") || text.contains("retweet to win")) {
            spamRisk += 15;
            warnings.add("🐦 Twitter engagement scam detected");
        }
    }

    public void addRedditSpecificRisks(String text) {
        if (text.toLowerCase().contains("pm me") || text.toLowerCase().contains("dm me")) {
            spamRisk += 10;
            warnings.add("📮 Reddit PM solicitation detected");
        }
    }

    public void addFacebookSpecificRisks(String text) {
        if (text.toLowerCase().contains("share to win") || text.toLowerCase().contains("tag friends")) {
            spamRisk += 15;
            warnings.add("📘 Facebook engagement scam detected");
        }
    }

    public void addDiscordSpecificRisks(String text) {
        if (text.toLowerCase().contains("nitro") && text.toLowerCase().contains("free")) {
            phishingRisk += 25;
            warnings.add("💬 Discord Nitro scam detected");
        }
    }

    public void addInstagramSpecificRisks(String text) {
        if (text.toLowerCase().contains("follow for follow") || text.toLowerCase().contains("f4f")) {
            spamRisk += 10;
            warnings.add("📸 Instagram spam pattern detected");
        }
    }
}

enum Platform {
    TWITTER, REDDIT, FACEBOOK, DISCORD, INSTAGRAM, UNKNOWN
}

enum RiskLevel {
    SAFE, LOW, MEDIUM, HIGH
}