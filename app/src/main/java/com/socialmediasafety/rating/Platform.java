// app/src/main/java/com/socialmediasafety/rating/analysis/Platform.java
package com.socialmediasafety.rating.platform;

public enum Platform {
    UNKNOWN("❓"),
    INSTAGRAM("📷"),
    TWITTER("🐦"),
    FACEBOOK("📘"),
    TIKTOK("🎵"),
    YOUTUBE("📺");

    private final String emoji;

    Platform(String emoji) {
        this.emoji = emoji;
    }

    public String getEmoji() {
        return emoji;
    }
}