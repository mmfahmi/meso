53
# Social Media Safety Rating - Android Mobile Port

This port adapts the browser extension functionality to work on Android mobile apps (Twitter, Reddit, Facebook, Discord) using Android's Accessibility Service API.

## 🚀 Approaches to Mobile Implementation

### 1. Accessibility Service (Recommended)
- **What it does**: Monitors screen content in real-time across all apps
- **Advantages**: Works with any app, no root required, official Android API
- **Limitations**: Requires accessibility permission, may impact performance

### 2. Overlay App
- **What it does**: Draws floating analysis badges over other apps
- **Advantages**: Visual feedback, works across apps
- **Limitations**: Requires system alert permission, Android 10+ restrictions

### 3. Screen OCR Analysis
- **What it does**: Takes screenshots and analyzes visible text
- **Advantages**: Works with any visual content
- **Limitations**: Privacy concerns, battery intensive

## 📱 Current Implementation: Accessibility Service

### Features
- ✅ Real-time text analysis across all social media apps
- ✅ Floating overlay badges showing safety ratings
- ✅ Detailed analysis popup on tap
- ✅ Platform-specific detection (Twitter, Reddit, Facebook, Discord)
- ✅ Same risk analysis engine as browser extension
- ✅ Privacy-focused (all processing on device)

### Supported Apps
- Twitter/X
- Reddit
- Facebook
- Discord
- Instagram (basic support)
- Any text-based social media app

## 🔧 Technical Architecture

```
┌─────────────────────────────────────┐
│           Android App               │
├─────────────────────────────────────┤
│  1. AccessibilityService           │
│     - Screen content monitoring     │
│     - Text extraction               │
│     - App detection                 │
├─────────────────────────────────────┤
│  2. Analysis Engine                 │
│     - Risk pattern matching        │
│     - Platform-specific rules      │
│     - Scoring algorithm             │
├─────────────────────────────────────┤
│  3. Overlay System                  │
│     - Floating badges               │
│     - Detailed analysis popups     │
│     - Touch handling               │
├─────────────────────────────────────┤
│  4. Settings & Configuration       │
│     - App whitelist/blacklist      │
│     - Sensitivity settings         │
│     - Privacy controls             │
└─────────────────────────────────────┘
```

## 🛠️ Installation & Setup

### Prerequisites
- Android 6.0+ (API level 23+)
- Accessibility service permissions
- System alert window permission (for overlays)

### Build Instructions
1. Clone this repository
2. Open in Android Studio
3. Build and install APK
4. Enable accessibility service in Settings
5. Grant overlay permissions

### Permissions Required
```xml
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
<uses-permission android:name="android.permission.BIND_ACCESSIBILITY_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
```

## 🎯 Usage Guide

### Step 1: Setup
1. Install the app
2. Go to Settings > Accessibility > Social Media Safety Rating
3. Enable the service
4. Grant overlay permissions when prompted

### Step 2: Use
1. Open any social media app (Twitter, Reddit, etc.)
2. Scroll through posts normally
3. Safety badges will appear automatically:
   - 🟢 **GREEN**: Safe content
   - 🟡 **YELLOW**: Low risk  
   - 🟠 **ORANGE**: Medium risk
   - 🔴 **RED**: High risk
4. Tap badges for detailed analysis

### Step 3: Customize
- Access settings through notification or app icon
- Adjust sensitivity levels
- Enable/disable specific platforms
- Configure badge appearance

## 🔍 How It Works

### Text Extraction
```java
// Extract text from accessibility nodes
public String extractTextFromNode(AccessibilityNodeInfo node) {
    StringBuilder text = new StringBuilder();
    if (node.getText() != null) {
        text.append(node.getText().toString());
    }
    
    for (int i = 0; i < node.getChildCount(); i++) {
        AccessibilityNodeInfo child = node.getChild(i);
        if (child != null) {
            text.append(" ").append(extractTextFromNode(child));
            child.recycle();
        }
    }
    return text.toString().trim();
}
```

### Platform Detection
```java
public Platform detectPlatform(String packageName) {
    switch (packageName) {
        case "com.twitter.android":
            return Platform.TWITTER;
        case "com.reddit.frontpage":
            return Platform.REDDIT;
        case "com.facebook.katana":
            return Platform.FACEBOOK;
        case "com.discord":
            return Platform.DISCORD;
        default:
            return Platform.UNKNOWN;
    }
}
```

### Risk Analysis
```java
public RiskAnalysis analyzeContent(String text, Platform platform) {
    RiskAnalysis analysis = new RiskAnalysis();
    
    // Apply same risk patterns as browser extension
    analysis.setFinancialRisk(checkFinancialPatterns(text));
    analysis.setPhishingRisk(checkPhishingPatterns(text));
    analysis.setSpamRisk(checkSpamPatterns(text));
    
    // Platform-specific analysis
    switch (platform) {
        case TWITTER:
            analysis.addTwitterSpecificRisks(text);
            break;
        case REDDIT:
            analysis.addRedditSpecificRisks(text);
            break;
    }
    
    return analysis;
}
```

## 🎨 UI Components

### Floating Badge
- Appears as small colored circle on posts
- Non-intrusive, positioned at top-right of content
- Animated appearance to draw attention
- Automatically disappears after timeout

### Analysis Popup
- Shows detailed risk breakdown
- Lists specific warning factors
- Provides educational information
- Includes "Report False Positive" option

## 🔒 Privacy & Security

### Privacy Features
- ✅ All analysis happens locally on device
- ✅ No data sent to external servers
- ✅ No persistent storage of analyzed content
- ✅ User can disable service anytime
- ✅ Selective app monitoring (whitelist mode)

### Security Considerations
- Accessibility service permissions are powerful
- App is open source for transparency
- Regular security audits recommended
- User education about permissions

## 📊 Performance Optimization

### Battery Efficiency
- Intelligent screen change detection
- Debounced analysis to avoid excessive processing
- Background service management
- Selective monitoring based on user activity

### Memory Management
- Recycle accessibility nodes properly
- Limit cached analysis results
- Garbage collection optimization
- Memory leak prevention

## 🔧 Configuration Options

### User Settings
- **Sensitivity Levels**: Adjust how strict the analysis is
- **Platform Selection**: Choose which apps to monitor
- **Badge Appearance**: Customize colors and positioning
- **Analysis Depth**: Quick scan vs. detailed analysis
- **Privacy Mode**: Disable certain types of analysis

### Developer Options
- Debug mode for testing
- Performance monitoring
- Custom risk pattern addition
- Analysis logging

## 🚀 Deployment Strategies

### 1. Google Play Store
- Requires careful permission justification
- Accessibility service apps need detailed explanation
- Privacy policy must be comprehensive
- Regular security reviews

### 2. F-Droid (Open Source)
- Better suited for privacy-focused users
- No Google Play restrictions
- Community-driven development
- Easier to maintain open source

### 3. Side-loading (APK)
- Direct distribution to users
- No store restrictions
- Requires user education about installation
- Manual update process

## 🔄 Sync with Browser Extension

### Shared Components
- Risk analysis patterns
- Scoring algorithms
- User preferences
- Whitelist/blacklist data

### Sync Methods
- Cloud sync via user account
- Local Wi-Fi sync between devices
- QR code configuration transfer
- Manual import/export

## 🧪 Testing Strategy

### Unit Tests
- Risk pattern matching
- Platform detection
- Analysis algorithm accuracy
- Performance benchmarks

### Integration Tests
- Real app compatibility
- Accessibility service reliability
- Overlay system functionality
- Memory leak detection

### User Testing
- Accessibility for users with disabilities
- Performance impact measurement
- False positive/negative rates
- User experience evaluation

## 🔮 Future Enhancements

### Short Term
- Machine learning-based analysis
- Multi-language support
- Improved platform detection
- Better battery optimization

### Long Term
- Cross-platform sync
- Advanced threat intelligence
- Community-driven pattern updates
- Integration with security platforms

## 🤝 Contributing

### Development Setup
1. Fork the repository
2. Set up Android development environment
3. Follow code style guidelines
4. Submit pull requests with tests

### Areas for Contribution
- Platform-specific analysis patterns
- Performance optimizations
- UI/UX improvements
- Security enhancements
- Documentation updates

## 📄 License

Open source under MIT License - same as browser extension.

## 🆘 Support & Troubleshooting

### Common Issues
- **Badges not appearing**: Check accessibility permissions
- **High battery usage**: Adjust sensitivity settings
- **App crashes**: Enable debug mode and report logs
- **False positives**: Use "Report Issue" feature

### Getting Help
- GitHub Issues for bugs
- Discussions for feature requests
- Documentation wiki
- Community forums

---

**Note**: This mobile implementation maintains the same core functionality as the browser extension while adapting to Android's security model and UI constraints. The accessibility service approach provides the best balance of functionality and user privacy.
# meso
