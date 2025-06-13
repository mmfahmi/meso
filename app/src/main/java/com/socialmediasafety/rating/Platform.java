package com.socialmediasafety.rating;

public enum Platform {
    TWITTER, REDDIT, FACEBOOK, DISCORD, INSTAGRAM, UNKNOWN;

    public String getEmoji() {
        switch (this) {
            case TWITTER: return "🐦";
            case REDDIT: return "👽";
            case FACEBOOK: return "📘";
            case DISCORD: return "🎮";
            case INSTAGRAM: return "📸";
            default: return "❓";
        }
    }
}
