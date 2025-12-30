package com.module.ads.admob.inters;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdValue;
import com.google.android.gms.ads.AdapterResponseInfo;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnPaidEventListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAd;
import com.module.ads.callback.CallbackAd;
import com.module.ads.callback.OnInterListener;
import com.module.ads.callback.OnNativeListener;
import com.module.ads.mmp.AdjustTracking;
import com.module.ads.remote.FirebaseQuery;
import com.module.ads.utils.FBTracking;
import com.module.ads.utils.PurchaseUtils;

import java.util.HashMap;
import java.util.Map;


public class IntersInApp {

    public CallbackAd mCallbackAd;
    public final Map<String, InterstitialAd> interstitialAds = new HashMap<>();
    public final Map<String, Boolean> isLoading = new HashMap<>();
    public final Map<String, String> idHigh = new HashMap<>();
    public final Map<String, String> idNormal = new HashMap<>();
    public boolean isShowing = false;
    private long timeLoad = 0;

    private static IntersInApp mIntersInApp;

    public static IntersInApp getInstance() {
        if (mIntersInApp == null) {
            mIntersInApp = new IntersInApp();
        }
        return mIntersInApp;
    }

    public void loadAds(
            @NonNull Activity activity,
            @NonNull String highId,
            @Nullable String normalId,
            @NonNull String placementName,
            @NonNull boolean isReload,
            @Nullable OnInterListener callback
    ) {
        if (interstitialAds.get(placementName) != null || (isLoading.get(placementName) != null && isLoading.get(placementName)))
            return;

        idHigh.put(placementName, highId);
        idNormal.put(placementName, normalId);

        isLoading.put(placementName, true);

        String currentId = highId;
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(activity, highId, adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        if (normalId != null) {
                            Log.e("TAGTAM", placementName.toLowerCase() + " loadAds onAdLoaded: High");
                        } else {
                            Log.e("TAGTAM", placementName.toLowerCase() + " loadAds onAdLoaded: Normal");
                        }
                        isLoading.put(placementName, false);
                        interstitialAds.put(placementName, interstitialAd);
                        interstitialAd.setOnPaidEventListener(new OnPaidEventListener() {
                            @Override
                            public void onPaidEvent(@NonNull AdValue adValue) {
                                if (callback != null) callback.onPaidEvent(adValue);
                            }
                        });
                        interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                if (callback != null) callback.onAdDismissedFullScreenContent();
                                Log.e("TAGTAM", placementName.toLowerCase() + " loadAds onAdDismissedFullScreenContent: High");
                                isShowing = false;
                                isLoading.put(placementName, false);
                                interstitialAds.put(placementName, null);
                                if (isReload) {
                                    loadAds(activity, highId, normalId, placementName, isReload, callback);
                                }
                                timeLoad = System.currentTimeMillis();
                                IntersUtils.dismissDialogLoading();
                                if (mCallbackAd != null) {
                                    mCallbackAd.onNextAction();
                                }
                            }

                            @Override
                            public void onAdImpression() {
                                if (callback != null) callback.onAdImpression();
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                if (callback != null) callback.onAdShowedFullScreenContent();
                                isShowing = true;
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                if (callback != null)
                                    callback.onAdFailedToShowFullScreenContent(adError);
                            }

                            @Override
                            public void onAdClicked() {
                                if (callback != null) callback.onAdClicked();
                            }
                        });
                        if (callback != null) callback.onAdLoaded();
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        if (normalId != null) {
                            Log.e("TAGTAM", placementName.toLowerCase() + " loadAds onAdFailedToLoad: High" + loadAdError.getMessage());
                        } else {
                            Log.e("TAGTAM", placementName.toLowerCase() + " loadAds onAdFailedToLoad: Normal" + loadAdError.getMessage());
                        }
                        // Fallback chỉ khi đang load high và có normalId
                        if (normalId != null && currentId.equals(highId)) {
                            Log.i("NativeAll", "High failed → fallback to Normal for " + placementName);
                            loadAds(activity, normalId, null, placementName, isReload, callback);
                            return;
                        }

                        interstitialAds.put(placementName, null);
                        isLoading.put(placementName, false);
                        Log.e("TAG", "onAdFailedToLoad: inter all high");
                        if (callback != null) callback.onAdFailedToLoad(loadAdError);
                    }
                });
    }

    public void showInters(
            @NonNull Activity activity,
            @NonNull String placementName,
            @NonNull long timeShow,
            @NonNull boolean isReload,
            @NonNull CallbackAd callbackAd
    ) {
        Log.e("TAGTAM", placementName.toLowerCase() + " callShowInters: ");
        this.mCallbackAd = callbackAd;
        InterstitialAd interstitialAd;
        if (interstitialAds.get(placementName) != null) {
            interstitialAd = interstitialAds.get(placementName);
        } else {
            interstitialAd = null;
        }
        if (interstitialAd != null) {
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - timeLoad;
            if (elapsedTime >= timeShow) {
                Log.e("TAGTAM", placementName.toLowerCase() + " callShowInters: show");
                IntersUtils.showDialogLoading(activity);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (interstitialAd != null) {
                            interstitialAd.show(activity);
                        }
                    }
                }, 500L);
            } else {
                Log.e("TAGTAM", placementName.toLowerCase() + " callShowInters: timeCount");
                if (mCallbackAd != null) {
                    mCallbackAd.onNextAction();
                }
            }
        } else {
            Log.e("TAGTAM", placementName.toLowerCase() + " callShowInters: reload by null");
            loadAds(activity, idHigh.get(placementName), idNormal.get(placementName), placementName, isReload,null);
            if (callbackAd != null) {
                callbackAd.onNextAction();
            }
        }
    }
}
