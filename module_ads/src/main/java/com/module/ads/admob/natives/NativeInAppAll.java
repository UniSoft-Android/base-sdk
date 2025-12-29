package com.module.ads.admob.natives;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdValue;
import com.google.android.gms.ads.AdapterResponseInfo;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnPaidEventListener;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.module.ads.callback.CallbackNative;
import com.module.ads.mmp.AdjustTracking;
import com.module.ads.remote.FirebaseQuery;
import com.module.ads.utils.FBTracking;
import com.module.ads.utils.PurchaseUtils;


public class NativeInAppAll {
    private static NativeInAppAll nativeInApp;
    public static NativeAd nativeAd;

    public static NativeInAppAll getInstance() {
        if (nativeInApp == null) {
            nativeInApp = new NativeInAppAll();
        }
        return nativeInApp;
    }

    public void loadAndShow(
            Activity activity,
            LinearLayout lnNative,
            String idAds,
            CallbackNative callbackNative,
            String namePlace,
            boolean isNativeInApp
    ) {
        try {
            if (!PurchaseUtils.isNoAds(activity) && FirebaseQuery.getEnableAds() && FirebaseQuery.getEnableNative()) {
                AdLoader.Builder builder = new AdLoader.Builder(activity, idAds);
                builder.forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                        Log.e("TAG", "onNativeAdLoaded: loadAndShow native in app");
                        NativeInAppAll.nativeAd = nativeAd;
                        nativeAd.setOnPaidEventListener(new OnPaidEventListener() {
                            @Override
                            public void onPaidEvent(@NonNull AdValue adValue) {
                                double revenue = adValue.getValueMicros() / 1000000.0;
                                AdapterResponseInfo loadedAdapterResponseInfo = nativeAd.getResponseInfo().getLoadedAdapterResponseInfo();
                                AdjustTracking.adjustTrackingRev(revenue, adValue.getCurrencyCode(), loadedAdapterResponseInfo.getAdSourceName());
                                FBTracking.funcTrackingIAA(activity, FBTracking.EVENT_AD_IMPRESSION, String.valueOf(revenue), activity.getClass(), "Native");
                            }
                        });
                        NativeAdView nativeAdView = (NativeAdView) activity.getLayoutInflater().inflate(NativeUtils.getLayoutNative(namePlace), null);
                        NativeUtils.populateNativeAdView(nativeAd, nativeAdView, namePlace);
                        lnNative.setVisibility(View.VISIBLE);
                        lnNative.removeAllViews();
                        lnNative.addView(nativeAdView);
                    }
                });

                AdLoader adLoader = builder.withAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        if (callbackNative != null) {
                            callbackNative.onLoaded();
                        }
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        if (callbackNative != null) {
                            callbackNative.onFailed();
                        }
                        lnNative.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAdImpression() {
                        FBTracking.funcTrackingIAA(activity, FBTracking.EVENT_AD_IMPRESSION);
                        if (callbackNative != null) {
                            callbackNative.onAdImpression();
                        }
                    }
                }).build();

                VideoOptions videoOptions = new VideoOptions.Builder().build();
                NativeAdOptions adOptions = new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();
                builder.withNativeAdOptions(adOptions);
                adLoader.loadAd(new AdRequest.Builder().build());
            } else {
                lnNative.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            lnNative.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }

    public void loadAndShowAll(Activity activity, LinearLayout lnNative, CallbackNative callbackNative, String namePlace) {
        destroyAdAll();
        if (!PurchaseUtils.isNoAds(activity) && FirebaseQuery.getEnableAds() && FirebaseQuery.getEnableNative()) {
            if (FirebaseQuery.getEnableNativeInApp2Floor()) {
                loadAndShow(activity, lnNative, FirebaseQuery.getIdNativeInApp2Floor(), new CallbackNative() {
                    @Override
                    public void onLoaded() {
                    }

                    @Override
                    public void onFailed() {
                        if (FirebaseQuery.getEnableNativeInApp()) {
                            loadAndShow(activity, lnNative, FirebaseQuery.getIdNativeInApp(), callbackNative, namePlace, true);
                        } else {
                            lnNative.setVisibility(View.GONE);
                            if (callbackNative != null) {
                                callbackNative.onFailed();
                            }
                        }
                    }

                    @Override
                    public void onAdImpression() {
                        FBTracking.funcTrackingIAA(activity, FBTracking.EVENT_AD_IMPRESSION);
                        if (callbackNative != null) {
                            callbackNative.onAdImpression();
                        }
                    }
                }, namePlace, true);
            } else {
                if (FirebaseQuery.getEnableNativeInApp()) {
                    loadAndShow(activity, lnNative, FirebaseQuery.getIdNativeInApp(), callbackNative, namePlace, true);
                } else {
                    lnNative.setVisibility(View.GONE);
                    if (callbackNative != null) {
                        callbackNative.onFailed();
                    }
                }
            }
        }
    }

    public void showAds(Activity activity, LinearLayout lnNative, String namePlace) {
        try {
            if (!PurchaseUtils.isNoAds(activity) && FirebaseQuery.getEnableAds() && nativeAd != null) {
                lnNative.setVisibility(View.VISIBLE);
                NativeAdView nativeAdView = (NativeAdView) activity.getLayoutInflater().inflate(NativeUtils.getLayoutNative(namePlace), null);
                NativeUtils.populateNativeAdView(nativeAd, nativeAdView, namePlace);
                lnNative.removeAllViews();
                lnNative.addView(nativeAdView);
            } else {
                lnNative.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void destroyAdAll() {
        nativeAd = null;
    }
}
