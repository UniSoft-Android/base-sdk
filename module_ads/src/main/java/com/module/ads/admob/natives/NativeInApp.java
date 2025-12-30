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
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.module.ads.callback.OnNativeListener;

import java.util.HashMap;
import java.util.Map;

public class NativeInApp {

    private static final NativeInApp instance = new NativeInApp();

    public static NativeInApp getInstance() {
        return instance;
    }

    private NativeInApp() {
    }

    // Lưu NativeAd theo key (thường là placementName)
    public final Map<String, NativeAd> nativeAds = new HashMap<>();
    public final Map<String, Boolean> isLoading = new HashMap<>();
    public final Map<String, Boolean> isStateLoad = new HashMap<>();

    public boolean isShowing = false;

    private OnNativeListener callbackNative;

    public void setOnNativeListener(OnNativeListener callbackNative) {
        this.callbackNative = callbackNative;
    }


    /**
     * Load and display native ads with high -> normal fallback.
     * Each placementName will be independent.
     */
    public void loadAndShow(
            @NonNull Activity activity,
            @NonNull LinearLayout container,
            @Nullable int layout,
            @NonNull String highId,
            @Nullable String normalId,
            @NonNull String placementName,
            @Nullable OnNativeListener callback
    ) {
        // Xóa ad cũ của placement này trước (tránh memory leak)
        destroy(placementName);
//        container.removeAllViews();
        if (isLoading.get(placementName) != null && isLoading.get(placementName)) return;
        loadInternal(activity, container, layout, highId, normalId, placementName, true, callback);
    }

    /**
     * Preload native ad without displaying (used for pre-caching)
     */
    public void preLoad(
            @NonNull Activity activity,
            @NonNull String highId,
            @Nullable String normalId,
            @NonNull String placementName
    ) {
        destroy(placementName);
        String currentId = highId;

        AdLoader.Builder builder = new AdLoader.Builder(activity, currentId);

        builder.forNativeAd(nativeAd -> {
            isLoading.put(placementName, false);
            if (activity.isFinishing() || activity.isDestroyed()) {
                nativeAd.destroy();
                return;
            }

            // Lưu ad vào map theo placement
            nativeAds.put(placementName, nativeAd);

            nativeAd.setOnPaidEventListener(adValue -> {
                if (callbackNative != null) callbackNative.onPaidEventListener(adValue);
            });
        });

        builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError error) {
                Log.w("NativeAll", "Failed to load ad for " + placementName + " id: " + currentId + " - " + error);
                if (normalId != null) {
                    Log.e("TAGTAM", placementName.toLowerCase() + " preLoad onAdFailedToLoad: High" + error.getMessage());
                } else {
                    Log.e("TAGTAM", placementName.toLowerCase() + " preLoad onAdFailedToLoad: Normal" + error.getMessage());
                }
                // Fallback chỉ khi đang load high và có normalId
                if (normalId != null && currentId.equals(highId)) {
                    Log.i("NativeAll", "High failed → fallback to Normal for " + placementName);
                    preLoad(activity, normalId, null, placementName);
                    return;
                }

                isLoading.put(placementName, false);
                isStateLoad.put(placementName, false);
                if (callbackNative != null) callbackNative.onFailed(error);
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if (normalId != null) {
                    Log.e("TAGTAM", placementName.toLowerCase() + " preLoad onAdLoaded: High");
                } else {
                    Log.e("TAGTAM", placementName.toLowerCase() + " preLoad onAdLoaded: Normal");
                }

                isStateLoad.put(placementName, true);
                if (callbackNative != null) callbackNative.onLoaded();
            }

            @Override
            public void onAdImpression() {
                if (normalId != null) {
                    Log.e("TAGTAM", placementName.toLowerCase() + " preLoad onAdImpression: High");
                } else {
                    Log.e("TAGTAM", placementName.toLowerCase() + " preLoad onAdImpression: Normal");
                }
                if (callbackNative != null) callbackNative.onAdImpression();
            }

            @Override
            public void onAdClicked() {
                Log.e("TAG", placementName.toLowerCase() + " preLoad onAdClicked: ");
                if (callbackNative != null) callbackNative.onAdClicked();
            }

            @Override
            public void onAdClosed() {
                Log.e("TAG", placementName.toLowerCase() + " preLoad onAdClosed: ");
                if (callbackNative != null) callbackNative.onAdClosed();
            }

            @Override
            public void onAdOpened() {
                Log.e("TAG", placementName.toLowerCase() + " preLoad onAdOpened: ");
                if (callbackNative != null) callbackNative.onAdOpened();
            }

            @Override
            public void onAdSwipeGestureClicked() {
                Log.e("TAG", placementName.toLowerCase() + " preLoad onAdSwipeGestureClicked: ");
                if (callbackNative != null) callbackNative.onAdSwipeGestureClicked();
            }
        });

        VideoOptions videoOptions = new VideoOptions.Builder().build();
        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build();

        builder.withNativeAdOptions(adOptions);
        isLoading.put(placementName, true);
        if (normalId != null) {
            Log.e("TAGTAM", placementName.toLowerCase() + " preLoad : High");
        } else {
            Log.e("TAGTAM", placementName.toLowerCase() + " preLoad : Normal");
        }

        builder.build().loadAd(new AdRequest.Builder().build());
    }

    private void loadInternal(
            @NonNull Activity activity,
            @Nullable LinearLayout container,
            @Nullable int layout,
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
                            .inflate(layout, null);

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
                if (normalId != null) {
                    Log.e("TAGTAM", placementName.toLowerCase() + " loadInternal onAdFailedToLoad: High" + error.getMessage());
                } else {
                    Log.e("TAGTAM", placementName.toLowerCase() + " loadInternal onAdFailedToLoad: Normal" + error.getMessage());
                }
                // Fallback chỉ khi đang load high và có normalId
                if (normalId != null && currentId.equals(highId)) {
                    Log.i("NativeAll", "High failed → fallback to Normal for " + placementName);
                    loadInternal(activity, container, layout, normalId, null, placementName, shouldShow, callback);
                    return;
                }
                isLoading.put(placementName, false);
                if (callback != null) callback.onFailed(error);

                if (shouldShow && container != null) {
                    container.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                isLoading.put(placementName, false);
                if (callback != null) callback.onLoaded();
                if (normalId != null) {
                    Log.e("TAGTAM", placementName.toLowerCase() + " loadInternal onAdLoaded: High");
                } else {
                    Log.e("TAGTAM", placementName.toLowerCase() + " loadInternal onAdLoaded: Normal");
                }

            }

            @Override
            public void onAdImpression() {
                if (callback != null) callback.onAdImpression();
                if (normalId != null) {
                    Log.e("TAGTAM", placementName.toLowerCase() + " loadInternal onAdImpression: High");
                } else {
                    Log.e("TAGTAM", placementName.toLowerCase() + " loadInternal onAdImpression: Normal");
                }
            }

            @Override
            public void onAdClicked() {
                if (callback != null) callback.onAdClicked();
                Log.e("TAG", placementName.toLowerCase() + " loadInternal onAdClicked: ");
            }

            @Override
            public void onAdClosed() {
                if (callback != null) callback.onAdClosed();
                Log.e("TAG", placementName.toLowerCase() + " loadInternal onAdClosed: ");
            }

            @Override
            public void onAdOpened() {
                if (callback != null) callback.onAdOpened();
                Log.e("TAG", placementName.toLowerCase() + " loadInternal onAdOpened: ");
            }

            @Override
            public void onAdSwipeGestureClicked() {
                if (callback != null) callback.onAdSwipeGestureClicked();
                Log.e("TAG", placementName.toLowerCase() + " loadInternal onAdSwipeGestureClicked: ");
            }
        });

        VideoOptions videoOptions = new VideoOptions.Builder().build();
        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build();

        builder.withNativeAdOptions(adOptions);
        isLoading.put(placementName, true);

        if (normalId != null) {
            Log.e("TAGTAM", placementName.toLowerCase() + " loadInternal : High");
        } else {
            Log.e("TAGTAM", placementName.toLowerCase() + " loadInternal : Normal");
        }
        builder.build().loadAd(new AdRequest.Builder().build());
    }

    /**
     * Show preloaded native ad (if available) for a specific placement
     */
    public void showAds(
            @NonNull Activity activity,
            @NonNull LinearLayout container,
            @NonNull int layout,
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
                    .inflate(layout, null);

            NativeUtils.populateNativeAdView(nativeAd, adView, placementName);

            container.removeAllViews();
            container.addView(adView);
        } catch (Exception e) {
            Log.e("NativeAll", "Show native failed for " + placementName, e);
            container.setVisibility(View.GONE);
        }
    }

    public void showOrLoadNativeAd(
            @NonNull Activity activity,
            @Nullable LinearLayout container,
            @Nullable int layout,
            @NonNull String highId,
            @Nullable String normalId,
            @NonNull String placementName,
            @Nullable OnNativeListener callback) {

        if (isLoading.get(placementName) != null && isLoading.get(placementName)) {
            Log.e("TAGTAM", placementName.toLowerCase() + " showCallback: ");
            NativeInApp.getInstance().setOnNativeListener(new OnNativeListener() {
                @Override
                public void onLoaded() {
                    if (callback != null) callback.onLoaded();
                    Log.e("TAGTAM", placementName.toLowerCase() + " showCallback onLoaded: ");
                    Log.e("TAGTAM", placementName.toLowerCase() + " showCallback showAds: ");
                    NativeInApp.getInstance().showAds(
                            activity,
                            container,
                            layout,
                            placementName
                    );
                }

                @Override
                public void onPaidEventListener(AdValue adValue) {
                    if (callback != null) callback.onPaidEventListener(adValue);
                    Log.e("TAG", placementName.toLowerCase() + " showCallback onPaidEventListener: ");
                }

                @Override
                public void onFailed(LoadAdError loadAdError) {
                    if (callback != null) callback.onFailed(loadAdError);
                    Log.e("TAGTAM", placementName.toLowerCase() + " showCallback onFailed: " + loadAdError.getMessage());

                }

                @Override
                public void onAdImpression() {
                    if (callback != null) callback.onAdImpression();
                    Log.e("TAGTAM", placementName.toLowerCase() + " showCallback onAdImpression: ");
                }

                @Override
                public void onAdSwipeGestureClicked() {
                    if (callback != null) callback.onAdSwipeGestureClicked();
                    Log.e("TAG", placementName.toLowerCase() + " showCallback onAdSwipeGestureClicked: ");
                }

                @Override
                public void onAdClicked() {
                    if (callback != null) callback.onAdClicked();
                    Log.e("TAG", placementName.toLowerCase() + " showCallback onAdClicked: ");
                }

                @Override
                public void onAdOpened() {
                    if (callback != null) callback.onAdOpened();
                    Log.e("TAG", placementName.toLowerCase() + " showCallback onAdOpened: ");
                }

                @Override
                public void onAdClosed() {
                    if (callback != null) callback.onAdClosed();
                    Log.e("TAG", placementName.toLowerCase() + " showCallback onAdClosed: ");
                }

            });
        } else {
            Log.e("TAGTAM", placementName.toLowerCase() + " showOrLoadNativeAd : ");
            if (isStateLoad.get(placementName) != null && isStateLoad.get(placementName) == true) {
                if (NativeInApp.getInstance().nativeAds.get(placementName) != null) {
                    Log.e("TAGTAM", placementName.toLowerCase() + " showOrLoadNativeAd : showAds");
                    NativeInApp.getInstance().showAds(
                            activity,
                            container,
                            layout,
                            placementName
                    );
                } else {
                    Log.e("TAGTAM", placementName.toLowerCase() + " showOrLoadNativeAd : ads null reload ads");
                    loadAndShow(activity, container, layout, highId, normalId, placementName, callback);
                }
            } else {
                Log.e("TAGTAM", placementName.toLowerCase() + " showOrLoadNativeAd : load false reload ads");
                loadAndShow(activity, container, layout, highId, normalId, placementName, callback);
            }
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