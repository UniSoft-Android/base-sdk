package com.module.ads.admob.reward;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdValue;
import com.google.android.gms.ads.AdapterResponseInfo;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnPaidEventListener;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.module.ads.admob.inters.IntersUtils;
import com.module.ads.callback.CallbackAd;
import com.module.ads.callback.OnRewardListener;
import com.module.ads.mmp.AdjustTracking;
import com.module.ads.remote.FirebaseQuery;
import com.module.ads.utils.FBTracking;
import com.module.ads.utils.PurchaseUtils;

import java.util.HashMap;
import java.util.Map;

public class RewardInApp {

    private CallbackAd mCallbackAd;
    public final Map<String, RewardedAd> rewardAds = new HashMap<>();
    public final Map<String, Boolean> isLoading = new HashMap<>();
    public final Map<String, String> idHigh = new HashMap<>();
    public final Map<String, String> idNormal = new HashMap<>();
    public boolean isShowing = false;

    private static RewardInApp rewardInApp;

    public static RewardInApp getInstance() {
        if (rewardInApp == null) {
            rewardInApp = new RewardInApp();
        }
        return rewardInApp;
    }


    public void loadReward(
            @NonNull Activity activity,
            @NonNull String highId,
            @Nullable String normalId,
            @NonNull String placementName,
            @Nullable OnRewardListener callback
    ) {
        if (rewardAds.get(placementName) != null || (isLoading.get(placementName) != null && isLoading.get(placementName)))
            return;

        idHigh.put(placementName, highId);
        idNormal.put(placementName, normalId);

        isLoading.put(placementName, true);

        String currentId = highId;

        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(activity, highId,
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        if (normalId != null) {
                            Log.e("TAGTAM", placementName.toLowerCase() + " loadAds onAdFailedToLoad: High" + loadAdError.getMessage());
                        } else {
                            Log.e("TAGTAM", placementName.toLowerCase() + " loadAds onAdFailedToLoad: Normal" + loadAdError.getMessage());
                        }
                        isLoading.put(placementName, false);
                        // Fallback chỉ khi đang load high và có normalId
                        if (normalId != null && currentId.equals(highId)) {
                            Log.e("TAGTAM", "High failed → fallback to Normal for " + placementName);
                            loadReward(activity, normalId, null, placementName, callback);
                            return;
                        }
                        Log.e("TAG", "onAdFailedToLoad: inter all high");
                        if (callback != null) callback.onAdFailedToLoad(loadAdError);
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd ad) {
                        Log.e("TAG", "onAdLoaded: reward in app");
                        if (normalId != null) {
                            Log.e("TAGTAM", placementName.toLowerCase() + " loadAds onAdLoaded: High");
                        } else {
                            Log.e("TAGTAM", placementName.toLowerCase() + " loadAds onAdLoaded: Normal");
                        }
                        isLoading.put(placementName, false);
                        rewardAds.put(placementName, ad);
                        ad.setOnPaidEventListener(new OnPaidEventListener() {
                            @Override
                            public void onPaidEvent(@NonNull AdValue adValue) {
                                if (callback != null) callback.onPaidEvent(adValue);
                            }
                        });
                        ad.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdClicked() {
                                if (callback != null) callback.onAdClicked();
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                if (callback != null) callback.onAdDismissedFullScreenContent();
                                Log.e("TAGTAM", placementName.toLowerCase() + " loadAds onAdDismissedFullScreenContent: High");
                                isShowing = false;
                                isLoading.put(placementName, false);
                                rewardAds.put(placementName, null);
                                loadReward(activity, highId, normalId, placementName, callback);
                                IntersUtils.dismissDialogLoading();
                                if (mCallbackAd != null) {
                                    mCallbackAd.onNextAction();
                                }
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                if (callback != null)
                                    callback.onAdFailedToShowFullScreenContent(adError);
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
                        });
                        if (callback != null) callback.onAdLoaded();
                    }
                });

    }

    public void showReward(
            @NonNull Activity activity,
            @NonNull String placementName,
            @NonNull CallbackAd callbackAd
    ) {
        Log.e("TAGTAM", placementName.toLowerCase() + " callShowReward: ");
        RewardedAd rewardAd;
        if (rewardAds.get(placementName) != null) {
            rewardAd = rewardAds.get(placementName);
        } else {
            rewardAd = null;
        }
        if (rewardAd != null) {
            Log.e("TAGTAM", placementName.toLowerCase() + " callShowInters: show");
            IntersUtils.showDialogLoading(activity);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (rewardAd != null) {
                        rewardAd.show(activity, new OnUserEarnedRewardListener() {
                            @Override
                            public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                                mCallbackAd = callbackAd;
                            }
                        });
                    }
                }
            }, 500L);
        } else {
            Log.e("TAGTAM", placementName.toLowerCase() + " callShowInters: reload by null");
            Toast.makeText(activity, "Reward Ad not available!", Toast.LENGTH_SHORT).show();
            loadReward(activity, idHigh.get(placementName), idNormal.get(placementName), placementName, null);
        }
    }
}
