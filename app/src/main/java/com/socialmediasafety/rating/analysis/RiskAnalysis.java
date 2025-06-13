package com.socialmediasafety.rating.analysis;

import java.util.ArrayList;
import java.util.List;
import com.socialmediasafety.rating.Platform;


public class RiskAnalysis {
    private int riskScore;
    private int financialRisk;
    private int phishingRisk;
    private int spamRisk;
    private int urlRisk;

    private double totalRiskScore;
    private RiskLevel riskLevel;
    private List<String> riskFactors;
    private Platform platform;

    public RiskAnalysis() {
        this.riskFactors = new ArrayList<>();
        this.riskLevel = RiskLevel.SAFE;
    }

    public RiskAnalysis(int riskScore, RiskLevel riskLevel, List<String> riskFactors, Platform platform) {
        this.riskScore = riskScore;
        this.riskLevel = riskLevel;
        this.riskFactors = riskFactors != null ? riskFactors : new ArrayList<>();
        this.platform = platform;
    }

    // Getters
    public int getRiskScore() { return riskScore; }
    public RiskLevel getRiskLevel() { return riskLevel; }
    public List<String> getRiskFactors() { return riskFactors; }
    public Platform getPlatform() { return platform; }
    public double getTotalRiskScore() { return totalRiskScore; }

    // Setters
    public void setRiskScore(int riskScore) { this.riskScore = riskScore; }
    public void setRiskLevel(RiskLevel riskLevel) { this.riskLevel = riskLevel; }
    public void setRiskFactors(List<String> riskFactors) { this.riskFactors = riskFactors; }
    public void setPlatform(Platform platform) { this.platform = platform; }

    public void setFinancialRisk(int value) { this.financialRisk = value; }
    public void setPhishingRisk(int value) { this.phishingRisk = value; }
    public void setSpamRisk(int value) { this.spamRisk = value; }
    public void setUrlRisk(int value) { this.urlRisk = value; }

    public void calculateOverallRisk() {
        int total = financialRisk + phishingRisk + spamRisk + urlRisk;
        this.riskScore = total;
        this.totalRiskScore = total / 4.0;

        if (totalRiskScore >= 0.7) {
            this.riskLevel = RiskLevel.HIGH;
        } else if (totalRiskScore >= 0.4) {
            this.riskLevel = RiskLevel.MEDIUM;
        } else {
            this.riskLevel = RiskLevel.SAFE;
        }
    }

    public void addRisk(String reason, double weight) {
        this.riskFactors.add(reason);
        this.totalRiskScore += weight;
        this.riskScore = (int)(totalRiskScore * 100);
    }

    public void addTwitterSpecificRisks(String text) {
        if (text.contains("giveaway") || text.contains("elon")) {
            addRisk("Potential Twitter crypto scam", 0.7);
        }
    }

    public void addRedditSpecificRisks(String text) {
        if (text.contains("modmail scam")) {
            addRisk("Reddit modmail scam pattern", 0.6);
        }
    }

    public void addFacebookSpecificRisks(String text) {
        if (text.contains("account suspended")) {
            addRisk("Facebook suspension scam", 0.6);
        }
    }

    public void addDiscordSpecificRisks(String text) {
        if (text.contains("NFT drop") || text.contains("airdrop")) {
            addRisk("Discord crypto/NFT scam", 0.8);
        }
    }

    public void addInstagramSpecificRisks(String text) {
        if (text.contains("sugar daddy") || text.contains("dm to earn")) {
            addRisk("Instagram scam indicators", 0.5);
        }
    }
}
