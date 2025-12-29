package com.module.ads.admob.natives;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdValue;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnPaidEventListener;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.module.ads.callback.OnNativeListener;

import java.util.HashMap;
import java.util.Map;

public class NativeAll {

    private static final NativeAll instance = new NativeAll();

    public static NativeAll getInstance() {
        return instance;
    }

    private NativeAll() {
    }

    // Lưu NativeAd theo key (thường là placementName)
    private final Map<String, NativeAd> nativeAds = new HashMap<>();

    /**
     * Load and display native ads with high -> normal fallback.
     * Each placementName will be independent.
     */
    public void loadAndShow(
            @NonNull Activity activity,
            @NonNull LinearLayout container,
            @NonNull String highId,
            @Nullable String normalId,
            @NonNull String placementName,
            @Nullable OnNativeListener callback
    ) {
        // Xóa ad cũ của placement này trước (tránh memory leak)
        destroy(placementName);

        container.setVisibility(View.GONE);
        container.removeAllViews();

        loadInternal(activity, container, highId, normalId, placementName, true, callback);
    }

    /**
     * Preload native ad without displaying (used for pre-caching)
     */
    public void preLoad(
            @NonNull Activity activity,
            @NonNull String highId,
            @Nullable String normalId,
            @NonNull String placementName,
            @Nullable OnNativeListener callback
    ) {
        destroy(placementName);
        loadInternal(activity, null, highId, normalId, placementName, false, callback);
    }

    private void loadInternal(
            @NonNull Activity activity,
            @Nullable LinearLayout container,
            @NonNull String highId,
            @Nullable String normalId,
            @NonNull String placementName,
            boolean shouldShow,
            @Nullable OnNativeListener callback
    ) {
        String currentId = highId;

        AdLoader.Builder builder = new AdLoader.Builder(activity, currentId);

        builder.forNativeAd(nativeAd -> {
            if (activity.isFinishing() || activity.isDestroyed()) {
                nativeAd.destroy();
                return;
            }

            // Lưu ad vào map theo placement
            nativeAds.put(placementName, nativeAd);

            nativeAd.setOnPaidEventListener(adValue -> {
                if (callback != null) callback.onPaidEventListener(adValue);
            });

            if (shouldShow && container != null) {
                try {
                    NativeAdView adView = (NativeAdView) activity.getLayoutInflater()
                            .inflate(NativeUtils.getLayoutNative(placementName), null);

                    NativeUtils.populateNativeAdView(nativeAd, adView, placementName);

                    container.removeAllViews();
                    container.addView(adView);
                    container.setVisibility(View.VISIBLE);

                    if (callback != null) callback.onLoaded();
                } catch (Exception e) {
                    Log.e("NativeAll", "Populate native failed for " + placementName, e);
                    if (container != null) container.setVisibility(View.GONE);
                }
            } else {
                if (callback != null) callback.onLoaded();
            }
        });

        builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError error) {
                Log.w("NativeAll", "Failed to load ad for " + placementName + " id: " + currentId + " - " + error);

                // Fallback chỉ khi đang load high và có normalId
                if (normalId != null && currentId.equals(highId)) {
                    Log.i("NativeAll", "High failed → fallback to Normal for " + placementName);
                    loadInternal(activity, container, normalId, null, placementName, shouldShow, callback);
                    return;
                }

                if (callback != null) callback.onFailed(error);

                if (shouldShow && container != null) {
                    container.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if (callback != null) callback.onLoaded();
            }

            @Override
            public void onAdImpression() {
                if (callback != null) callback.onAdImpression();
            }

            @Override
            public void onAdClicked() {
                if (callback != null) callback.onAdClicked();
            }

            @Override
            public void onAdClosed() {
                if (callback != null) callback.onAdClosed();
            }

            @Override
            public void onAdOpened() {
                if (callback != null) callback.onAdOpened();
            }

            @Override
            public void onAdSwipeGestureClicked() {
                if (callback != null) callback.onAdSwipeGestureClicked();
            }
        });

        VideoOptions videoOptions = new VideoOptions.Builder().build();
        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build();

        builder.withNativeAdOptions(adOptions);
        builder.build().loadAd(new AdRequest.Builder().build());
    }

    /**
     * Show preloaded native ad (if available) for a specific placement
     */
    public void showAds(
            @NonNull Activity activity,
            @NonNull LinearLayout container,
            @NonNull String placementName
    ) {
        NativeAd nativeAd = nativeAds.get(placementName);
        if (nativeAd == null) {
            container.setVisibility(View.GONE);
            return;
        }

        try {
            container.setVisibility(View.VISIBLE);
            NativeAdView adView = (NativeAdView) activity.getLayoutInflater()
                    .inflate(NativeUtils.getLayoutNative(placementName), null);

            NativeUtils.populateNativeAdView(nativeAd, adView, placementName);

            container.removeAllViews();
            container.addView(adView);
        } catch (Exception e) {
            Log.e("NativeAll", "Show native failed for " + placementName, e);
            container.setVisibility(View.GONE);
        }
    }

    /**
     * Remove and destroy the NativeAd of a specific placement
     */
    public void destroy(String placementName) {
        NativeAd ad = nativeAds.remove(placementName);
        if (ad != null) {
            ad.destroy();
        }
    }

    /**
     * Remove all native ads (call when the app is destroyed or when you need to clear memory)
     */
    public void destroyAll() {
        for (NativeAd ad : nativeAds.values()) {
            if (ad != null) {
                ad.destroy();
            }
        }
        nativeAds.clear();
    }

    /**
     * Call in onDestroy() of Activity/Fragment if needed
     */
    public void onDestroy() {
        destroyAll();
    }
}