package com.module.ads.admob.banner;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdValue;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.AdapterResponseInfo;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnPaidEventListener;
import com.module.ads.callback.OnBannerListener;
import com.module.ads.mmp.AdjustTracking;
import com.module.ads.remote.FirebaseQuery;
import com.module.ads.utils.FBTracking;
import com.module.ads.utils.PurchaseUtils;


public class BannerInApp {

    private static BannerInApp bannerInApp;

    public static BannerInApp getInstance() {
        if (bannerInApp == null) {
            bannerInApp = new BannerInApp();
        }
        return bannerInApp;
    }

    private boolean isLoadingAd = false;

    public void loadAndShow(final Activity activity, final LinearLayout lnBanner, String idBanner, boolean isCollapse, OnBannerListener callback) {
        if (isLoadingAd) return;

        AdView mAdView = new AdView(activity);
        mAdView.setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, BannerUtils.getAdWidth(activity)));

        mAdView.setOnPaidEventListener(new OnPaidEventListener() {
            @Override
            public void onPaidEvent(@NonNull AdValue adValue) {
                if (callback != null) callback.onPaidEventListener(adValue);
            }
        });
        mAdView.setAdUnitId(idBanner);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                isLoadingAd = false;
                if (lnBanner != null) {
                    lnBanner.setVisibility(ViewGroup.VISIBLE);
                    lnBanner.removeAllViews();
                    lnBanner.addView(mAdView);
                }

                if (callback != null) callback.onAdLoaded();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                isLoadingAd = false;
                if (callback != null) callback.onAdFailedToLoad(loadAdError);
            }

            @Override
            public void onAdImpression() {
                FBTracking.funcTrackingIAA(activity, FBTracking.EVENT_AD_IMPRESSION);
                if (callback != null) callback.onAdImpression();
            }

            @Override
            public void onAdClosed() {
                if (callback != null) callback.onAdClosed();
            }

            @Override
            public void onAdOpened() {
                if (callback != null) callback.onAdOpened();
            }

            @Override
            public void onAdClicked() {
                if (callback != null) callback.onAdClicked();
            }

            @Override
            public void onAdSwipeGestureClicked() {
                if (callback != null) callback.onAdSwipeGestureClicked();
            }
        });

        isLoadingAd = true;
        mAdView.loadAd(BannerUtils.getAdRequest(isCollapse));
    }
}
