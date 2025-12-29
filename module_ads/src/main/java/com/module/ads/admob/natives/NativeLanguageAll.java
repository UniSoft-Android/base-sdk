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

public class NativeLanguageAll {

    private static NativeLanguageAll nativeLanguage;
    public static NativeAd nativeAdHigh;
    public static NativeAd nativeAdNormal;
    public static NativeAd nativeAdAll;
    private boolean isLoadingHigh = false;
    private boolean isLoadingNormal = false;
    private boolean isLoadingAll = false;

    public static NativeLanguageAll getInstance() {
        if (nativeLanguage == null) {
            nativeLanguage = new NativeLanguageAll();
        }
        return nativeLanguage;
    }

    private CallbackNative callback;

    public void setCallback(CallbackNative callback) {
        this.callback = callback;
    }

    private void loadAndShow(final Activity activity, LinearLayout lnNative, String id, CallbackNative callbackNative, String namePlace) {
        try {
            if (isLoadingAll) return;
            if (!PurchaseUtils.isNoAds(activity) && FirebaseQuery.getEnableAds() && FirebaseQuery.getEnableNative()) {
                isLoadingAll = true;
                AdLoader.Builder builder = new AdLoader.Builder(activity, id);
                builder.forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                        isLoadingAll = false;
                        nativeAdAll = nativeAd;
                        nativeAd.setOnPaidEventListener(new OnPaidEventListener() {
                            @Override
                            public void onPaidEvent(@NonNull AdValue adValue) {
                                long revenue = adValue.getValueMicros() / 1000000;
                                AdapterResponseInfo loadedAdapterResponseInfo = nativeAd.getResponseInfo().getLoadedAdapterResponseInfo();
                                AdjustTracking.adjustTrackingRev(adValue.getValueMicros() / 1000000.0, adValue.getCurrencyCode(), loadedAdapterResponseInfo.getAdSourceName());
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
                        isLoadingAll = false;
                        if (callbackNative != null) {
                            callbackNative.onLoaded();
                        }
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Log.e("TAG", "onAdFailedToLoad: load and show native language");
                        isLoadingAll = false;
                        if (callbackNative != null) {
                            callbackNative.onFailed();
                        }
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
            e.printStackTrace();
        }
    }

    private void loadAndShowAll(Activity activity, LinearLayout lnNative, CallbackNative callbackNative, String namePlace) {
        Log.e("TAG", "loadAndShowAll: native language");
        if (!PurchaseUtils.isNoAds(activity) && FirebaseQuery.getEnableAds() && FirebaseQuery.getEnableNative()) {
            if (FirebaseQuery.getEnableNativeLanguage2Floor()) {
                loadAndShow(activity, lnNative, FirebaseQuery.getIdNativeLanguage2Floor(), new CallbackNative() {
                    @Override
                    public void onLoaded() {
                        if (nativeAdAll != null) {
                            NativeUtils.handleNativeMedia(nativeAdAll, namePlace, () -> {
                                loadAndShowAll(activity, lnNative, callbackNative, namePlace);
                            });
                        }
                    }

                    @Override
                    public void onFailed() {
                        if (FirebaseQuery.getEnableNativeLanguage()) {
                            loadAndShow(activity, lnNative, FirebaseQuery.getIdNativeLanguage(), new CallbackNative() {
                                @Override
                                public void onLoaded() {
                                    if (nativeAdAll != null) {
                                        NativeUtils.handleNativeMedia(nativeAdAll, namePlace, () -> {
                                            loadAndShowAll(activity, lnNative, callbackNative, namePlace);
                                        });
                                    }
                                }

                                @Override
                                public void onFailed() {
                                }

                                @Override
                                public void onAdImpression() {
                                }
                            }, namePlace);
                        }
                    }

                    @Override
                    public void onAdImpression() {
                        FBTracking.funcTrackingIAA(activity, FBTracking.EVENT_AD_IMPRESSION);
                        if (callbackNative != null) {
                            callbackNative.onAdImpression();
                        }
                    }
                }, namePlace);
            } else {
                if (FirebaseQuery.getEnableNativeLanguage()) {
                    loadAndShow(activity, lnNative, FirebaseQuery.getIdNativeLanguage(), new CallbackNative() {
                        @Override
                        public void onLoaded() {
                            if (nativeAdAll != null) {
                                NativeUtils.handleNativeMedia(nativeAdAll, namePlace, () -> {
                                    loadAndShowAll(activity, lnNative, callbackNative, namePlace);
                                });
                            }
                        }

                        @Override
                        public void onFailed() {
                        }

                        @Override
                        public void onAdImpression() {
                        }
                    }, namePlace);
                }
            }
        }
    }

    private void loadAdsNormal(final Activity activity) {
        try {
            if (isLoadingNormal || nativeAdNormal != null) return;
            if (!PurchaseUtils.isNoAds(activity) && FirebaseQuery.getEnableAds() && FirebaseQuery.getEnableNative() && FirebaseQuery.getEnableNativeLanguage()) {
                isLoadingNormal = true;
                AdLoader.Builder builder = new AdLoader.Builder(activity, FirebaseQuery.getIdNativeLanguage());
                builder.forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                        isLoadingNormal = false;
                        nativeAdNormal = nativeAd;
                        nativeAd.setOnPaidEventListener(new OnPaidEventListener() {
                            @Override
                            public void onPaidEvent(@NonNull AdValue adValue) {
                                long revenue = adValue.getValueMicros() / 1000000;
                                AdapterResponseInfo loadedAdapterResponseInfo = nativeAd.getResponseInfo().getLoadedAdapterResponseInfo();
                                AdjustTracking.adjustTrackingRev(adValue.getValueMicros() / 1000000.0, adValue.getCurrencyCode(), loadedAdapterResponseInfo.getAdSourceName());
                                FBTracking.funcTrackingIAA(activity, FBTracking.EVENT_AD_IMPRESSION, String.valueOf(revenue), activity.getClass(), "Native");
                            }
                        });
                    }
                });

                AdLoader adLoader = builder.withAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        isLoadingNormal = false;
                        if (callback != null) {
                            callback.onLoaded();
                        }
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Log.e("TAG", "onAdFailedToLoad: native language normal - " + loadAdError.getMessage());
                        isLoadingNormal = false;
                        if (callback != null) {
                            callback.onFailed();
                        }
                    }

                    @Override
                    public void onAdImpression() {
                        FBTracking.funcTrackingIAA(activity, FBTracking.EVENT_AD_IMPRESSION);
                        if (callback != null) {
                            callback.onAdImpression();
                        }
                    }
                }).build();

                VideoOptions videoOptions = new VideoOptions.Builder().build();
                NativeAdOptions adOptions = new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();
                builder.withNativeAdOptions(adOptions);
                adLoader.loadAd(new AdRequest.Builder().build());
            } else {
                isLoadingNormal = false;
                if (callback != null) {
                    callback.onFailed();
                }
            }
        } catch (Exception e) {
            isLoadingNormal = false;
            e.printStackTrace();
        }
    }

    private void loadAdsHigh(final Activity activity) {
        try {
            if (isLoadingHigh || nativeAdHigh != null) return;
            if (!PurchaseUtils.isNoAds(activity) && FirebaseQuery.getEnableAds() && FirebaseQuery.getEnableNative()) {
                if (FirebaseQuery.getEnableNativeLanguage2Floor()) {
                    isLoadingHigh = true;
                    AdLoader.Builder builder = new AdLoader.Builder(activity, FirebaseQuery.getIdNativeLanguage2Floor());
                    builder.forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                        @Override
                        public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                            isLoadingHigh = false;
                            nativeAdHigh = nativeAd;
                            nativeAd.setOnPaidEventListener(new OnPaidEventListener() {
                                @Override
                                public void onPaidEvent(@NonNull AdValue adValue) {
                                    double revenue = adValue.getValueMicros() / 1000000.0;
                                    AdapterResponseInfo loadedAdapterResponseInfo = nativeAd.getResponseInfo().getLoadedAdapterResponseInfo();
                                    AdjustTracking.adjustTrackingRev(revenue, adValue.getCurrencyCode(), loadedAdapterResponseInfo.getAdSourceName());
                                    FBTracking.funcTrackingIAA(activity, FBTracking.EVENT_AD_IMPRESSION, String.valueOf(revenue), activity.getClass(), "Native");
                                }
                            });
                        }
                    });

                    AdLoader adLoader = builder.withAdListener(new AdListener() {
                        @Override
                        public void onAdLoaded() {
                            super.onAdLoaded();
                            isLoadingHigh = false;
                            Log.e("TAG", "onAdLoaded: native language high");
                            if (callback != null) {
                                callback.onLoaded();
                            }
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            Log.e("TAG", "onAdFailedToLoad: native language high");
                            isLoadingHigh = false;
                            if (FirebaseQuery.getEnableNativeLanguage()) {
                                loadAdsNormal(activity);
                            } else {
                                if (callback != null) {
                                    callback.onFailed();
                                }
                            }
                        }

                        @Override
                        public void onAdImpression() {
                            FBTracking.funcTrackingIAA(activity, FBTracking.EVENT_AD_IMPRESSION);
                            if (callback != null) {
                                callback.onAdImpression();
                            }
                        }
                    }).build();

                    VideoOptions videoOptions = new VideoOptions.Builder().build();
                    NativeAdOptions adOptions = new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();
                    builder.withNativeAdOptions(adOptions);
                    adLoader.loadAd(new AdRequest.Builder().build());
                } else {
                    isLoadingHigh = false;
                    if (FirebaseQuery.getEnableNativeLanguage()) {
                        loadAdsNormal(activity);
                    } else {
                        if (callback != null) {
                            callback.onFailed();
                        }
                    }
                }
            } else {
                isLoadingHigh = false;
                if (callback != null) {
                    callback.onFailed();
                }
            }
        } catch (Exception e) {
            isLoadingHigh = false;
            e.printStackTrace();
        }
    }

    public void loadAdsAll(Activity activity) {
        if (FirebaseQuery.getEnableNativeLanguage2Floor()) {
            loadAdsHigh(activity);
        } else if (FirebaseQuery.getEnableNativeLanguage()) {
            loadAdsNormal(activity);
        } else {
            if (callback != null) {
                callback.onFailed();
            }
        }
    }

    public void showAdsAll(Activity activity, LinearLayout lnNative, String namePlace) {
        try {
            NativeAd nativeAd = null;
            if (nativeAdHigh != null) {
                nativeAd = nativeAdHigh;
            } else if (nativeAdNormal != null) {
                nativeAd = nativeAdNormal;
            }
            if (nativeAd != null) {
                lnNative.setVisibility(View.VISIBLE);
                NativeAdView nativeAdView = (NativeAdView) activity.getLayoutInflater().inflate(NativeUtils.getLayoutNative(namePlace), null);
                NativeUtils.populateNativeAdView(nativeAd, nativeAdView, namePlace);
                lnNative.removeAllViews();
                lnNative.addView(nativeAdView);

                NativeUtils.handleNativeMedia(nativeAd, namePlace, () -> {
                    loadAndShowAll(activity, lnNative, null, namePlace);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void destroyAd() {
        nativeAdHigh = null;
        nativeAdNormal = null;
        callback = null;
    }

    public void destroyAdAll() {
        nativeAdHigh = null;
        nativeAdNormal = null;
        nativeAdAll = null;
        callback = null;
    }
}
