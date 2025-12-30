package com.module.ads.mmp;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustAdRevenue;
import com.adjust.sdk.AdjustConfig;
import com.adjust.sdk.AdjustEvent;
import com.adjust.sdk.LogLevel;
import com.module.ads.BuildConfig;
import com.module.ads.R;

public class AdjustTracking {

    public static void adjustTrackingEvent(String token, double value, String currency) {
        AdjustEvent event = new AdjustEvent(token);
        event.setRevenue(value, currency);
        Adjust.trackEvent(event);
    }

    public static void adjustTrackingRev(double revenue, String currency, String network) {
        AdjustAdRevenue event = new AdjustAdRevenue("admob_sdk");
        event.setRevenue(revenue, currency);
        event.setAdRevenueNetwork(network);
        Adjust.trackAdRevenue(event);
    }

    public static void initAdjust(Application application, String keyAppToken) {
        String environment;
        if (BuildConfig.DEBUG) {
            environment = AdjustConfig.ENVIRONMENT_SANDBOX; // Debug mới sử dụng dòng này
        } else {
            environment = AdjustConfig.ENVIRONMENT_PRODUCTION; // Live Store sử dụng dòng này
        }

        AdjustConfig config = new AdjustConfig(application, keyAppToken, environment);
        config.setLogLevel(LogLevel.VERBOSE);
        Adjust.initSdk(config);
        application.registerActivityLifecycleCallbacks(new AdjustLifecycleCallbacks());
    }

    private static final class AdjustLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
        @Override
        public void onActivityResumed(@NonNull Activity activity) {
            Adjust.onResume();
        }

        @Override
        public void onActivityPaused(@NonNull Activity activity) {
            Adjust.onPause();
        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {
        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {
        }

        @Override
        public void onActivityCreated(@NonNull Activity activity, Bundle savedInstanceState) {
        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {
        }
    }
}
