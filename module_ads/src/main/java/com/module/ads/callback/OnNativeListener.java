package com.module.ads.callback;

import com.google.android.gms.ads.AdValue;
import com.google.android.gms.ads.LoadAdError;

public abstract class OnNativeListener {
    public void onLoaded() {}

    public void onPaidEventListener(AdValue adValue) {}

    public void onFailed(LoadAdError loadAdError) {}

    public void onAdImpression() {}

    public void onAdSwipeGestureClicked() {}

    public void onAdClicked() {}

    public void onAdOpened() {}

    public void onAdClosed() {}
}
