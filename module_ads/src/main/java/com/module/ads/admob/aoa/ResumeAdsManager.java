package com.module.ads.admob.aoa;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.module.ads.BuildConfig;
import com.module.ads.remote.FirebaseQuery;
import com.module.ads.utils.PurchaseUtils;
import com.module.ads.views.LoadingAdDialog;

import java.util.Date;

public class ResumeAdsManager {
    private static final String TAG = "ResumeAdsManager";
    private AppOpenAd appOpenAd = null;
    private boolean isLoadingAd = false;
    public boolean isShowingAd = false;

    private LoadingAdDialog loadingAdsDialog = null;

    private long loadTime = 0;

    public static boolean isAdFullShowing = false;
    public static boolean shouldReloadAd = false;

    public interface AppOpenListener {
        void onAdLoaded();

        void onAdFailedToLoad();
    }

    public interface OnShowAdCompleteListener {
        void onShowAdComplete();
    }

    public void loadAd(Context context, AppOpenListener listener) {
        if (isLoadingAd || isAdAvailable()) {
            return;
        }
        isLoadingAd = true;

        AdManagerAdRequest request = new AdManagerAdRequest.Builder().build();
        AppOpenAd.load(
                context,
                BuildConfig.open_resume,
                request,
                new AppOpenAd.AppOpenAdLoadCallback() {

                    @Override
                    public void onAdLoaded(@NonNull AppOpenAd ad) {
                        appOpenAd = ad;
                        isLoadingAd = false;
                        loadTime = new Date().getTime();

                        if (listener != null) {
                            listener.onAdLoaded();
                        }
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        isLoadingAd = false;
                        if (listener != null) {
                            listener.onAdFailedToLoad();
                        }
                    }
                }
        );
    }

    public void loadAd(Context context) {
        loadAd(context, null);
    }

    // ================================
    // Check if ad is fresh
    // ================================
    private boolean wasLoadTimeLessThanNHoursAgo(long numHours) {
        long dateDifference = new Date().getTime() - loadTime;
        long numMilliSecondsPerHour = 3600000;
        return dateDifference < numMilliSecondsPerHour * numHours;
    }

    private boolean isAdAvailable() {
        return (appOpenAd != null) && wasLoadTimeLessThanNHoursAgo(4);
    }


    // ================================
    // Show Ad (wrapper)
    // ================================
    public void showAdIfAvailable(Activity activity) {
        showAdIfAvailable(activity, () -> {
            // empty
        });
    }

    // ================================
    // Show Ad - main logic
    // ================================
    public void showAdIfAvailable(Activity activity, OnShowAdCompleteListener listener) {
        if (activity == null || activity.isFinishing()) return;

        if (isShowingAd || isAdFullShowing) {
            return;
        }

        if (!isAdAvailable()) {
            listener.onShowAdComplete();
            loadAd(activity);
            return;
        }

        appOpenAd.setFullScreenContentCallback(new FullScreenContentCallback() {

            @Override
            public void onAdDismissedFullScreenContent() {
                Log.d(TAG, "onAdDismissedFullScreenContent.");
                shouldReloadAd = true;
                appOpenAd = null;
                isShowingAd = false;

                dismissDialog(activity);
                listener.onShowAdComplete();
                loadAd(activity);
            }

            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                Log.d(TAG, "onAdFailedToShowFullScreenContent: " + adError.getMessage());
                appOpenAd = null;
                isShowingAd = false;

                listener.onShowAdComplete();
                loadAd(activity);
            }

            @Override
            public void onAdShowedFullScreenContent() {
                Log.d(TAG, "onAdShowedFullScreenContent.");
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
            }
        });

        // Kotlin coroutine → Java Handler
        new Handler(Looper.getMainLooper()).post(() -> {
            try {
                if (!activity.isFinishing()) {
                    loadingAdsDialog = new LoadingAdDialog(activity);
                    loadingAdsDialog.show();
                }
            } catch (Exception ignored) {
            }
        });

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            try {
                isShowingAd = true;
                appOpenAd.show(activity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 500);
    }

    public void dismissDialog(Activity activity) {
        try {
            if (activity != null && !activity.isFinishing()
                    && loadingAdsDialog != null
                    && loadingAdsDialog.isShowing()) {

                loadingAdsDialog.dismiss();
                loadingAdsDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
