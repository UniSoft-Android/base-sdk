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


public class NativeFullScreenOnboardAll {

    private static NativeFullScreenOnboardAll nativeFullscreenOnboard;
    public static NativeAd nativeAdHigh;
    public static NativeAd nativeAdNormal;
    public static NativeAd nativeAdAll;
    private boolean isLoadingHigh = false;
    private boolean isLoadingNormal = false;
    private boolean isLoadingAll = false;
    public boolean isShowing = false;

    public static NativeFullScreenOnboardAll getInstance() {
        if (nativeFullscreenOnboard == null) {
            nativeFullscreenOnboard = new NativeFullScreenOnboardAll();
        }
        return nativeFullscreenOnboard;
    }

    private CallbackNative callbackNative;

    public void setCallbackNative(CallbackNative callbackNative) {
        this.callbackNative = callbackNative;
    }

    private void loadAndShow(final Activity activity, LinearLayout lnNative, String idAds, CallbackNative callbackNative, String namePlace) {
        if (isLoadingAll) return;
        try {
            if (!PurchaseUtils.isNoAds(activity) && FirebaseQuery.getEnableAds() && FirebaseQuery.getEnableNative()) {
                isLoadingAll = true;
                AdLoader.Builder builder = new AdLoader.Builder(activity, idAds); // Store thay lại id
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
                        NativeAdView nativeAdView = (NativeAdView) activity.getLayoutInflater().inflate(NativeUtils.getLayoutNativeFull(namePlace), null);
                        NativeUtils.populateNativeAdView(nativeAd, nativeAdView, namePlace);
                        lnNative.setVisibility(View.VISIBLE);
                        lnNative.removeAllViews();
                        lnNative.addView(nativeAdView);
                        if (callbackNative != null) {
                            callbackNative.onLoaded();
                        }
                    }
                });

                AdLoader adLoader = builder.withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        isLoadingAll = false;
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
            }
        } catch (Exception e) {
            isLoadingAll = false;
            e.printStackTrace();
        }
    }

    public void loadAndShowAll(Activity activity, LinearLayout lnNative, String idHigh, String idNormal, CallbackNative callbackNative, String namePlace) {
        if (!PurchaseUtils.isNoAds(activity) && FirebaseQuery.getEnableAds() && FirebaseQuery.getEnableNative()) {
            if (FirebaseQuery.getEnableNativeFullScr2Floor()) {
                loadAndShow(activity, lnNative, idHigh, new CallbackNative() {
                    @Override
                    public void onLoaded() {
                    }

                    @Override
                    public void onFailed() {
                        if (FirebaseQuery.getEnableNativeFullScr()) {
                            loadAndShow(activity, lnNative, FirebaseQuery.getIdNativeFullOnboard(), new CallbackNative() {
                                @Override
                                public void onLoaded() {
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
                if (FirebaseQuery.getEnableNativeFullScr()) {
                    loadAndShow(activity, lnNative, idNormal, new CallbackNative() {
                        @Override
                        public void onLoaded() {
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

    private void loadAdsHigh(final Activity activity, String idHigh, String idNormal) {
        try {
            if (isLoadingHigh || nativeAdHigh != null) return;
            if (!PurchaseUtils.isNoAds(activity) && FirebaseQuery.getEnableAds() && FirebaseQuery.getEnableNative()) {
                if (FirebaseQuery.getEnableNativeFullScr2Floor()) {
                    isLoadingHigh = true;
                    AdLoader.Builder builder = new AdLoader.Builder(activity, idHigh);
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
                            Log.e("TAG", "onAdLoaded: native full onboard high");
                            if (callbackNative != null) {
                                callbackNative.onLoaded();
                            }
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            isLoadingHigh = false;
                            nativeAdHigh = null;
                            Log.e("TAG", "onAdFailedToLoad: native full onboard high - " + loadAdError.getMessage());
                            if (FirebaseQuery.getEnableNativeFullScr()) {
                                loadAdsNormal(activity, idNormal);
                            } else {
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
                    }).build();

                    VideoOptions videoOptions = new VideoOptions.Builder().build();
                    NativeAdOptions adOptions = new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();
                    builder.withNativeAdOptions(adOptions);
                    adLoader.loadAd(new AdRequest.Builder().build());
                } else {
                    isLoadingHigh = false;
                    if (FirebaseQuery.getEnableNativeFullScr()) {
                        loadAdsNormal(activity, idNormal);
                    } else {
                        if (callbackNative != null) {
                            callbackNative.onFailed();
                        }
                    }
                }
            } else {
                isLoadingHigh = false;
                if (callbackNative != null) {
                    callbackNative.onFailed();
                }
            }
        } catch (Exception e) {
            isLoadingHigh = false;
            e.printStackTrace();
        }
    }

    private void loadAdsNormal(final Activity activity, String idNormal) {
        try {
            if (isLoadingNormal || nativeAdNormal != null) return;
            if (!PurchaseUtils.isNoAds(activity) && FirebaseQuery.getEnableAds() && FirebaseQuery.getEnableNative() && FirebaseQuery.getEnableNativeFullScr()) {
                isLoadingNormal = true;
                AdLoader.Builder builder = new AdLoader.Builder(activity, idNormal);
                builder.forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                        isLoadingNormal = false;
                        nativeAdNormal = nativeAd;
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
                        Log.e("TAG", "onAdLoaded: native full onboard normal");
                        isLoadingNormal = false;
                        if (callbackNative != null) {
                            callbackNative.onLoaded();
                        }
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Log.e("TAG", "onAdFailedToLoad: native full onboard normal - " + loadAdError.getMessage());
                        isLoadingNormal = false;
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
                isLoadingNormal = false;
                if (callbackNative != null) {
                    callbackNative.onFailed();
                }
            }
        } catch (Exception e) {
            isLoadingNormal = false;
            e.printStackTrace();
        }
    }

    public void loadAdsAll(Activity activity, String idHigh, String idNormal) {
        if (FirebaseQuery.getEnableNativeFullScr2Floor()) {
            loadAdsHigh(activity, idHigh, idNormal);
        } else if (FirebaseQuery.getEnableNativeFullScr()) {
            loadAdsNormal(activity, idNormal);
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
                NativeAdView nativeAdView = (NativeAdView) activity.getLayoutInflater().inflate(NativeUtils.getLayoutNativeFull(namePlace), null);
                NativeUtils.populateNativeAdView(nativeAd, nativeAdView, namePlace);
                lnNative.setVisibility(View.VISIBLE);
                lnNative.removeAllViews();
                lnNative.addView(nativeAdView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void destroyNativeAd() {
        nativeAdHigh = null;
        nativeAdNormal = null;
    }

    public void destroyNativeAdAll() {
        nativeAdHigh = null;
        nativeAdNormal = null;
        nativeAdAll = null;
    }
}
