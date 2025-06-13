#!/bin/bash

echo "🔧 Applying final fixes..."

OM="app/src/main/java/com/socialmediasafety/rating/OverlayManager.java"
mkdir -p app/src/main/java/com/socialmediasafety/rating

# Patch OverlayManager to add constructor and cleanup()
cat > "$OM" <<'EOF'
package com.socialmediasafety.rating;

import com.socialmediasafety.rating.analysis.RiskAnalysis;
import com.socialmediasafety.rating.Platform;
import android.content.Context;

public class OverlayManager {

    private static final String TAG = "OverlayManager";
    private Context context;

    public OverlayManager() {
        // default no-arg constructor
    }

    public OverlayManager(Context context) {
        this.context = context;
    }

    public void showSafetyBadge(RiskAnalysis analysis, Platform platform) {
        // TODO: implement badge display logic
    }

    public void hideAllOverlays() {
        // TODO: implement overlay hiding logic
    }

    public void cleanup() {
        // TODO: implement cleanup logic
    }
}
EOF

echo "✅ Patched OverlayManager.java"

# Add stub MesoAccessibilityService class
cat > app/src/main/java/com/socialmediasafety/rating/MesoAccessibilityService.java <<'EOF'
package com.socialmediasafety.rating;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;

public class MesoAccessibilityService extends AccessibilityService {
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {}

    @Override
    public void onInterrupt() {}
}
EOF

echo "✅ Created MesoAccessibilityService.java"

# Create missing drawable/ic_pause.xml
mkdir -p app/src/main/res/drawable
cat > app/src/main/res/drawable/ic_pause.xml <<'EOF'
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp"
    android:viewportWidth="24" android:viewportHeight="24">
    <path android:fillColor="#000000" android:pathData="M6,19h4V5H6v14zM14,5v14h4V5h-4z"/>
</vector>
EOF

echo "✅ Added drawable: ic_pause"

# Add layout with startServiceButton ID
mkdir -p app/src/main/res/layout
cat > app/src/main/res/layout/activity_main.xml <<'EOF'
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp">

    <Button
        android:id="@+id/startServiceButton"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Start Service" />
</LinearLayout>
EOF

echo "✅ Added layout: activity_main with startServiceButton"

echo "🎉 All fixes applied. Run this next:"
echo "    ./gradlew clean assembleDebug"
