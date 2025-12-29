package com.module.ads.callback;

import com.google.android.gms.ads.AdValue;
import com.google.android.gms.ads.LoadAdError;

public interface OnNativeListener {
    void onLoaded();

    void onPaidEventListener(AdValue adValue);

    void onFailed(LoadAdError loadAdError);

    void onAdImpression();

    void onAdSwipeGestureClicked();

    void onAdClicked();

    void onAdOpened();

    void onAdClosed();
}
