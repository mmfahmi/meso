// app/src/main/java/com/socialmediasafety/rating/RiskLevel.java
package com.socialmediasafety.rating;

public enum RiskLevel {
    MINIMAL("Minimal"),
    LOW("Low"),
    MODERATE("Moderate"),
    HIGH("High"),
    CRITICAL("Critical");

    private final String displayName;

    RiskLevel(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}