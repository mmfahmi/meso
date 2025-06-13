package com.socialmediasafety.rating;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import com.socialmediasafety.rating.analysis.RiskAnalysis;
import com.socialmediasafety.rating.analysis.RiskLevel;


/**
 * Service to manage overlay windows and badge display.
 */
public class OverlayService extends Service {
    
    private static final String TAG = "OverlayService";
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Overlay service created");
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Overlay service started");
        return START_STICKY;
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Overlay service destroyed");
    }
}
