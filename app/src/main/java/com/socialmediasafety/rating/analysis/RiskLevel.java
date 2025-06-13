package com.socialmediasafety.rating.analysis;

public enum RiskLevel {
    MINIMAL("Minimal"),
    SAFE("Safe"),
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High");

    private final String displayName;

    RiskLevel(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
