package com.module.ads.callback;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdValue;
import com.google.android.gms.ads.LoadAdError;

public abstract class OnBannerListener {

    public void onAdLoaded() {}

    public void onPaidEventListener(AdValue adValue) {}

    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {}

    public void onAdImpression() {}

    public void onAdClosed() {}

    public void onAdOpened() {}

    public void onAdClicked() {}

    public void onAdSwipeGestureClicked() {}
}
