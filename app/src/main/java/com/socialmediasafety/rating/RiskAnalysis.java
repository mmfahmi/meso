// app/src/main/java/com/socialmediasafety/rating/RiskAnalysis.java
package com.socialmediasafety.rating;

import com.socialmediasafety.rating.analysis.Platform;
import java.util.List;

public class RiskAnalysis {
    private int riskScore;
    private RiskLevel riskLevel;
    private List<String> riskFactors;
    private Platform platform;

    public RiskAnalysis() {
        // Default constructor
    }

    public RiskAnalysis(int riskScore, RiskLevel riskLevel, List<String> riskFactors, Platform platform) {
        this.riskScore = riskScore;
        this.riskLevel = riskLevel;
        this.riskFactors = riskFactors;
        this.platform = platform;
    }

    // Getters
    public int getRiskScore() { return riskScore; }
    public RiskLevel getRiskLevel() { return riskLevel; }
    public List<String> getRiskFactors() { return riskFactors; }
    public Platform getPlatform() { return platform; }

    // Setters
    public void setRiskScore(int riskScore) { this.riskScore = riskScore; }
    public void setRiskLevel(RiskLevel riskLevel) { this.riskLevel = riskLevel; }
    public void setRiskFactors(List<String> riskFactors) { this.riskFactors = riskFactors; }
    public void setPlatform(Platform platform) { this.platform = platform; }
}