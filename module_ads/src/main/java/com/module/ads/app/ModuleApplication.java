package com.module.ads.app;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.android.gms.ads.AdActivity;
import com.google.firebase.FirebaseApp;
import com.module.ads.admob.aoa.ResumeAdsManager;
import com.module.ads.admob.inters.IntersInApp;
import com.module.ads.admob.inters.IntersUtils;
import com.module.ads.admob.reward.RewardInApp;
import com.module.ads.utils.SharePreferUtils;

import java.util.ArrayList;
import java.util.List;

public class ModuleApplication extends android.app.Application implements DefaultLifecycleObserver {

    private Activity currentActivity;
    private ResumeAdsManager appOpenAdManager;

    private final List<Class<?>> excludedActivities = new ArrayList<>();

    // Tách riêng implementation của ActivityLifecycleCallbacks
    private final android.app.Application.ActivityLifecycleCallbacks lifecycleCallbacks = new android.app.Application.ActivityLifecycleCallbacks() {

        @Override
        public void onActivityCreated(@NonNull Activity activity, Bundle savedInstanceState) {
            // Để trống hoặc thêm logic nếu cần
        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {
            if (!appOpenAdManager.isShowingAd) {
                currentActivity = activity;
            }
            if (!isActivityExcluded(currentActivity)) {
                appOpenAdManager.loadAd(currentActivity);
            }
        }

        @Override
        public void onActivityResumed(@NonNull Activity activity) {
            // Để trống hoặc thêm logic nếu cần
        }

        @Override
        public void onActivityPaused(@NonNull Activity activity) {
            // Để trống hoặc thêm logic nếu cần
        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {
            // Để trống hoặc thêm logic nếu cần
        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
            // Để trống hoặc thêm logic nếu cần
        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {
            appOpenAdManager.dismissDialog(activity);
            IntersUtils.dismissDialogLoading();
        }
    };

    public void addExcludedActivity(Class<?> activityClass) {
        excludedActivities.add(activityClass);
    }

    public boolean isActivityExcluded(Activity activity) {
        return excludedActivities.contains(activity.getClass());
    }


    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        SharePreferUtils.init(this);
        addExcludedActivity(AdActivity.class);
        registerActivityLifecycleCallbacks(lifecycleCallbacks);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        appOpenAdManager = new ResumeAdsManager();

    }

    // ============================================
    // Ứng dụng vào foreground
    // ============================================
    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        if (currentActivity == null) return;
        if (isActivityExcluded(currentActivity)) {
            return;
        }
        if (IntersInApp.getInstance().isShowing ||
                RewardInApp.getInstance().isShowing) {
            return;
        }
        appOpenAdManager.showAdIfAvailable(currentActivity);
    }

    // ============================================
    // Activity Lifecycle
    // ============================================
}
