package com.module.ads.callback;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdValue;
import com.google.android.gms.ads.LoadAdError;

public abstract class OnAoaListener {

    public void onAdLoaded() {
    }

    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
    }

    public void onPaidEvent(@NonNull AdValue adValue) {
    }

    public void onAdDismissedFullScreenContent() {
    }

    public void onAdFailedToShowFullScreenContent(@NonNull AdError var1) {
    }

    public void onAdClicked() {
    }

    public void onAdImpression() {
    }

    public void onAdShowedFullScreenContent() {
    }

}
