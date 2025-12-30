package com.module.ads.remote;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigFetchThrottledException;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.gson.Gson;
import com.module.ads.BuildConfig;
import com.module.ads.admob.aoa.OpenAdsManager;
import com.module.ads.callback.CallbackAd;
import com.module.ads.callback.CallbackBanner;
import com.module.ads.callback.CallbackTimeout;
import com.module.ads.models.ConfigUpdate;
import com.module.ads.models.config.ConfigAd;
import com.module.ads.models.config.ConfigAdPlaceNative;
import com.module.ads.utils.PurchaseUtils;
import com.module.ads.utils.SharePreferUtils;

import java.util.HashMap;
import java.util.Map;

public class FirebaseQuery {
    public static final String TIME_SHOW_INTERS = "time_show_inters";

    public static final String ID_INTERS_SPLASH = "inter_splash";
    public static final String ID_INTERS_SPLASH_2FLOOR = "inter_splash_2floor";

    public static final String ID_INTERS_ONBOARD = "inter_onboard";
    public static final String ID_INTERS_ONBOARD_2FLOOR = "inter_onboard_2floor";

    public static final String ID_INTERS_IN_APP = "inter_in_app";
    public static final String ID_INTERS_IN_APP_2FLOOR = "inter_in_app_2floor";

    public static final String ID_NATIVE_LANGUAGE = "native_language";
    public static final String ID_NATIVE_LANGUAGE_2FLOOR = "native_language_2floor";
    public static final String ID_NATIVE_LANGUAGE_DUP = "native_language_dup";
    public static final String ID_NATIVE_LANGUAGE_DUP_2FLOOR = "native_language_dup_2floor";
    public static final String ID_NATIVE_ONBOARD = "native_onboard";
    public static final String ID_NATIVE_ONBOARD_2FLOOR = "native_onboard_2floor";
    public static final String ID_NATIVE_FULL_ONBOARD = "native_full_scr";
    public static final String ID_NATIVE_FULL_ONBOARD_2FLOOR = "native_full_scr_2floor";

    public static final String ID_NATIVE_IN_APP = "native_in_app";
    public static final String ID_NATIVE_IN_APP_2FLOOR = "native_in_app_2floor";

    public static final String ID_OPEN_START = "open_start";
    public static final String ID_OPEN_RESUME = "open_resume";

    public static final String ID_BANNER_SPLASH = "banner_splash";
    public static final String ID_BANNER_IN_APP = "banner_in_app";

    public static final String ID_REWARD_IN_APP = "reward_in_app";

    public static final String ENABLE_ADS = "enable_ads";
    public static final String ENABLE_OPEN_RESUME = "enable_open_resume";
    public static final String ENABLE_OPEN_START = "enable_open_start";
    public static final String ENABLE_BANNER = "enable_banner";
    public static final String ENABLE_NATIVE = "enable_native";
    public static final String ENABLE_INTERS = "enable_inters";
    public static final String ENABLE_REWARD = "enable_reward";
    public static final String ENABLE_BANNER_COLLAPSE = "enable_banner_collapsible";
    public static final String ENABLE_NATIVE_COLLAPSE = "enable_native_collapsible";

    public static final String IS_SHOW_LANGUAGE_REOPEN = "is_show_language_reopen";
    public static final String IS_SHOW_OPEN_START = "is_show_open_start";

    public static final String CONFIG_ADS = "config_ads";
    public static final String CONFIG_UPDATE = "config_update";

    public static final String ENABLE_BANNER_SPLASH = "enable_banner_splash";
    public static final String ENABLE_INTERS_SPLASH = "enable_inter_splash";
    public static final String ENABLE_INTERS_SPLASH_2FLOOR = "enable_inter_splash_2floor";
    public static final String ENABLE_NATIVE_LANGUAGE = "enable_native_language";
    public static final String ENABLE_NATIVE_LANGUAGE_2FLOOR = "enable_native_language_2floor";
    public static final String ENABLE_NATIVE_LANGUAGE_DUP = "enable_native_language_dup";
    public static final String ENABLE_NATIVE_LANGUAGE_DUP_2FLOOR = "enable_native_language_dup_2floor";
    public static final String ENABLE_NATIVE_ONBOARD = "enable_native_onboard";
    public static final String ENABLE_NATIVE_ONBOARD_2FLOOR = "enable_native_onboard_2floor";
    public static final String ENABLE_NATIVE_FULL_SCR = "enable_native_full_scr";
    public static final String ENABLE_NATIVE_FULL_SCR_2FLOOR = "enable_native_full_scr_2floor";
    public static final String ENABLE_INTERS_ONBOARD = "enable_inters_onboard";
    public static final String ENABLE_INTERS_ONBOARD_2FLOOR = "enable_inters_onboard_2floor";
    public static final String ENABLE_INTERS_IN_APP = "enable_inters_in_app";
    public static final String ENABLE_INTERS_IN_APP_2FLOOR = "enable_inters_in_app_2floor";
    public static final String ENABLE_NATIVE_IN_APP = "enable_native_in_app";
    public static final String ENABLE_NATIVE_IN_APP_2FLOOR = "enable_native_in_app_2floor";

    public static final String ENABLE_SHOW_NATIVE_FULL_SCR_1 = "enable_show_native_full_scr_1";
    public static final String ENABLE_SHOW_NATIVE_FULL_SCR_2 = "enable_show_native_full_scr_2";
    public static final String NFS_TIME_SCROLL = "nfs_time_scroll";
    public static final String LAYOUT_BUTTON_ADS = "layout_button_ads";

    public static final String ENABLE_LFO_RELOAD_ITEM = "enable_lfo_reload_item";
    public static final String ENABLE_NATIVE_RELOAD_MEDIA = "enable_native_reload_media";

    public static final String NOTIFICATION_LOCK_SCR = "notification_lock_scr";
    public static final String NOTIFICATION_SCR = "notification_scr";


    private FirebaseRemoteConfig remoteConfig;
    private GoogleMobileAdsConsentManager googleMobileAdsConsentManager;
    public static boolean canRequestAds = false;

    public ConfigAd configAd;
    public final Map<String, ConfigAdPlaceNative> configMap = new HashMap<>();
    public ConfigUpdate configUpdate;
    public static boolean isChooseLanguage = false;
    public static boolean isFromNotification = false;

    private static FirebaseQuery configController;

    public static FirebaseQuery getConfigController() {
        if (configController == null) {
            configController = new FirebaseQuery();
        }
        return configController;
    }

    private FirebaseQuery() {
    }

    private CallbackTimeout callbackTimeout;

    public void setCallbackTimeout(CallbackTimeout callbackTimeout) {
        this.callbackTimeout = callbackTimeout;
    }

    private CallbackBanner callbackBannerSplash;

    public void setCallbackBannerSplash(CallbackBanner callbackBannerSplash) {
        this.callbackBannerSplash = callbackBannerSplash;
    }

    public void initFirebase(Activity activity, CallbackAd callbackAd) {
        canRequestAds = false;
        remoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings settings = new FirebaseRemoteConfigSettings
                .Builder()
                .setMinimumFetchIntervalInSeconds(BuildConfig.DEBUG ? 0 : 3600)
                .setFetchTimeoutInSeconds(30)
                .build();
        remoteConfig.setConfigSettingsAsync(settings);
        remoteConfig.fetchAndActivate()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        googleMobileAdsConsentManager = GoogleMobileAdsConsentManager.getInstance(activity);
                        googleMobileAdsConsentManager.gatherConsent(
                                activity,
                                consentError -> {
                                    if (consentError != null) {
                                    }
                                    if (googleMobileAdsConsentManager.canRequestAds()) {
                                        if (!canRequestAds) {
                                            queryData(remoteConfig, activity, callbackAd);
                                        }
                                    }
                                }
                        );
                        // This sample attempts to load ads using consent obtained in the previous session.
                        if (googleMobileAdsConsentManager.canRequestAds()) {
                            if (!canRequestAds) {
                                queryData(remoteConfig, activity, callbackAd);
                            }
                        }
                    } else {
                        Exception e = task.getException();
                        if (e instanceof FirebaseRemoteConfigFetchThrottledException) {
                            if (callbackAd != null) {
                                callbackAd.onNextAction();
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("TAG", "addOnFailureListener: " + e.getMessage());
                    if (callbackAd != null) {
                        callbackAd.onNextAction();
                    }
                    e.printStackTrace();
                });
    }

    private void queryData(FirebaseRemoteConfig firebaseRemoteConfig, Activity activity, CallbackAd callbackAd) {
        canRequestAds = true;
        try {
            String configAds = firebaseRemoteConfig.getString(CONFIG_ADS);
            Log.e("TAG", "queryData: configAds = " + configAds);
            configAd = new Gson().fromJson(configAds, ConfigAd.class);
            if (configAd != null && configAd.getListAdPlaceNative() != null) {
                for (ConfigAdPlaceNative place : configAd.getListAdPlaceNative()) {
                    configMap.put(place.getName(), place); // Lưu theo name
                }
            }

            String configUpdates = firebaseRemoteConfig.getString(CONFIG_UPDATE);
            Log.e("TAG", "queryData: configUpdate = " + configUpdates);
            configUpdate = new Gson().fromJson(configUpdates, ConfigUpdate.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        boolean isShowOpenStart = firebaseRemoteConfig.getBoolean(IS_SHOW_OPEN_START);
        setIsShowOpenStart(isShowOpenStart);

        boolean enableAds = firebaseRemoteConfig.getBoolean(ENABLE_ADS);
        setEnableAds(enableAds);

        boolean enableOpenStart = firebaseRemoteConfig.getBoolean(ENABLE_OPEN_START);
        setEnableOpenStart(enableOpenStart);

        boolean enableOpenResume = firebaseRemoteConfig.getBoolean(ENABLE_OPEN_RESUME);
        setEnableOpenResume(enableOpenResume);

        boolean enableBanner = firebaseRemoteConfig.getBoolean(ENABLE_BANNER);
        setEnableBanner(enableBanner);

        boolean enableNative = firebaseRemoteConfig.getBoolean(ENABLE_NATIVE);
        setEnableNative(enableNative);

        boolean enableInters = firebaseRemoteConfig.getBoolean(ENABLE_INTERS);
        setEnableInters(enableInters);

        boolean enableReward = firebaseRemoteConfig.getBoolean(ENABLE_REWARD);
        setEnableReward(enableReward);

        boolean enableNativeCollapse = firebaseRemoteConfig.getBoolean(ENABLE_NATIVE_COLLAPSE);
        setEnableNativeCollapse(enableNativeCollapse);

        boolean enableBannerCollapse = firebaseRemoteConfig.getBoolean(ENABLE_BANNER_COLLAPSE);
        setEnableBannerCollapse(enableBannerCollapse);

        String idOpenStart = firebaseRemoteConfig.getString(ID_OPEN_START);
        setIdOpenStart(idOpenStart);

        String idOpenResume = firebaseRemoteConfig.getString(ID_OPEN_RESUME);
        setIdOpenResume(idOpenResume);

        String idIntersSplash = firebaseRemoteConfig.getString(ID_INTERS_SPLASH);
        setIdIntersSplash(idIntersSplash);

        String idIntersSplash2Floor = firebaseRemoteConfig.getString(ID_INTERS_SPLASH_2FLOOR);
        setIdIntersSplash2Floor(idIntersSplash2Floor);

        String idIntersOnboard = firebaseRemoteConfig.getString(ID_INTERS_ONBOARD);
        setIdIntersOnboard(idIntersOnboard);

        String idIntersOnboard2Floor = firebaseRemoteConfig.getString(ID_INTERS_ONBOARD_2FLOOR);
        setIdIntersOnBoard2Floor(idIntersOnboard2Floor);

        String idInterInApp = firebaseRemoteConfig.getString(ID_INTERS_IN_APP);
        setIdIntersInApp(idInterInApp);

        String idInterInApp2Floor = firebaseRemoteConfig.getString(ID_INTERS_IN_APP_2FLOOR);
        setIdIntersInApp2Floor(idInterInApp2Floor);

        String idNativeLanguage = firebaseRemoteConfig.getString(ID_NATIVE_LANGUAGE);
        setIdNativeLanguage(idNativeLanguage);

        String idNativeLanguage2Floor = firebaseRemoteConfig.getString(ID_NATIVE_LANGUAGE_2FLOOR);
        setIdNativeLanguage2Floor(idNativeLanguage2Floor);

        String idNativeLanguageDup = firebaseRemoteConfig.getString(ID_NATIVE_LANGUAGE_DUP);
        setIdNativeLanguageDup(idNativeLanguageDup);

        String idNativeLanguageDup2Floor = firebaseRemoteConfig.getString(ID_NATIVE_LANGUAGE_DUP_2FLOOR);
        setIdNativeLanguageDup2Floor(idNativeLanguageDup2Floor);

        String idNativeOnboard = firebaseRemoteConfig.getString(ID_NATIVE_ONBOARD);
        setIdNativeOnboard(idNativeOnboard);

        String idNativeOnboard2Floor = firebaseRemoteConfig.getString(ID_NATIVE_ONBOARD_2FLOOR);
        setIdNativeOnboard2Floor(idNativeOnboard2Floor);

        String idNativeFullOnboard = firebaseRemoteConfig.getString(ID_NATIVE_FULL_ONBOARD);
        setIdNativeFullOnboard(idNativeFullOnboard);

        String idNativeFullOnboard2Floor = firebaseRemoteConfig.getString(ID_NATIVE_FULL_ONBOARD_2FLOOR);
        setIdNativeFullOnboard2Floor(idNativeFullOnboard2Floor);

        String idNativeInApp = firebaseRemoteConfig.getString(ID_NATIVE_IN_APP);
        setIdNativeInApp(idNativeInApp);

        String idNativeInApp2Floor = firebaseRemoteConfig.getString(ID_NATIVE_IN_APP_2FLOOR);
        setIdNativeInApp2Floor(idNativeInApp2Floor);

        String idBannerSplash = firebaseRemoteConfig.getString(ID_BANNER_SPLASH);
        setIdBannerSplash(idBannerSplash);

        String idBannerInApp = firebaseRemoteConfig.getString(ID_BANNER_IN_APP);
        setIdBannerInApp(idBannerInApp);

        String idRewardInApp = firebaseRemoteConfig.getString(ID_REWARD_IN_APP);
        setIdRewardInApp(idRewardInApp);

        boolean isShowLanguageReopen = firebaseRemoteConfig.getBoolean(IS_SHOW_LANGUAGE_REOPEN);
        setIsShowLanguageReopen(isShowLanguageReopen);

        String timeShowInters = firebaseRemoteConfig.getString(TIME_SHOW_INTERS);
        setTimeShowInters(timeShowInters);

        boolean enableBannerSplash = firebaseRemoteConfig.getBoolean(ENABLE_BANNER_SPLASH);
        setEnableBannerSplash(enableBannerSplash);

        boolean enableIntersSplash = firebaseRemoteConfig.getBoolean(ENABLE_INTERS_SPLASH);
        setEnableIntersSplash(enableIntersSplash);

        boolean enableIntersSplash2Floor = firebaseRemoteConfig.getBoolean(ENABLE_INTERS_SPLASH_2FLOOR);
        setEnableIntersSplash2Floor(enableIntersSplash2Floor);

        boolean enableNativeLanguage = firebaseRemoteConfig.getBoolean(ENABLE_NATIVE_LANGUAGE);
        setEnableNativeLanguage(enableNativeLanguage);

        boolean enableNativeLanguage2Floor = firebaseRemoteConfig.getBoolean(ENABLE_NATIVE_LANGUAGE_2FLOOR);
        setEnableNativeLanguage2Floor(enableNativeLanguage2Floor);

        boolean enableNativeLanguageDup = firebaseRemoteConfig.getBoolean(ENABLE_NATIVE_LANGUAGE_DUP);
        setEnableNativeLanguageDup(enableNativeLanguageDup);

        boolean enableNativeLanguageDup2Floor = firebaseRemoteConfig.getBoolean(ENABLE_NATIVE_LANGUAGE_DUP_2FLOOR);
        setEnableNativeLanguageDup2Floor(enableNativeLanguageDup2Floor);

        boolean enableNativeOnboard = firebaseRemoteConfig.getBoolean(ENABLE_NATIVE_ONBOARD);
        setEnableNativeOnboard(enableNativeOnboard);

        boolean enableNativeOnboard2Floor = firebaseRemoteConfig.getBoolean(ENABLE_NATIVE_ONBOARD_2FLOOR);
        setEnableNativeOnboard2Floor(enableNativeOnboard2Floor);

        boolean enableNativeFullScr = firebaseRemoteConfig.getBoolean(ENABLE_NATIVE_FULL_SCR);
        setEnableNativeFullScr(enableNativeFullScr);

        boolean enableNativeFullScr2Floor = firebaseRemoteConfig.getBoolean(ENABLE_NATIVE_FULL_SCR_2FLOOR);
        setEnableNativeFullScr2Floor(enableNativeFullScr2Floor);

        boolean enableIntersOnboard = firebaseRemoteConfig.getBoolean(ENABLE_INTERS_ONBOARD);
        setEnableIntersOnboard(enableIntersOnboard);

        boolean enableIntersOnboard2Floor = firebaseRemoteConfig.getBoolean(ENABLE_INTERS_ONBOARD_2FLOOR);
        setEnableIntersOnboard2Floor(enableIntersOnboard2Floor);

        boolean enableIntersInApp = firebaseRemoteConfig.getBoolean(ENABLE_INTERS_IN_APP);
        setEnableIntersInApp(enableIntersInApp);

        boolean enableIntersInApp2Floor = firebaseRemoteConfig.getBoolean(ENABLE_INTERS_IN_APP_2FLOOR);
        setEnableIntersInApp2Floor(enableIntersInApp2Floor);

        long nfsTimeScroll = firebaseRemoteConfig.getLong(NFS_TIME_SCROLL);
        setNfsTimeScroll(nfsTimeScroll);

        String layoutButtonAds = firebaseRemoteConfig.getString(LAYOUT_BUTTON_ADS);
        setLayoutButtonAds(layoutButtonAds);

        boolean enableLfoReloadItem = firebaseRemoteConfig.getBoolean(ENABLE_LFO_RELOAD_ITEM);
        setEnableLfoReloadItem(enableLfoReloadItem);

        boolean enableNativeReloadMedia = firebaseRemoteConfig.getBoolean(ENABLE_NATIVE_RELOAD_MEDIA);
        setEnableNativeReloadMedia(enableNativeReloadMedia);

        boolean enableNativeInApp = firebaseRemoteConfig.getBoolean(ENABLE_NATIVE_IN_APP);
        setEnableNativeInApp(enableNativeInApp);

        boolean enableNativeInApp2Floor = firebaseRemoteConfig.getBoolean(ENABLE_NATIVE_IN_APP_2FLOOR);
        setEnableNativeInApp2Floor(enableNativeInApp2Floor);

        boolean enableShowNativeFullScr1 = firebaseRemoteConfig.getBoolean(ENABLE_SHOW_NATIVE_FULL_SCR_1);
        setEnableShowNativeFullScr1(enableShowNativeFullScr1);

        boolean enableShowNativeFullScr2 = firebaseRemoteConfig.getBoolean(ENABLE_SHOW_NATIVE_FULL_SCR_2);
        setEnableShowNativeFullScr2(enableShowNativeFullScr2);

        if (getEnableAds() && getEnableBannerSplash()) {
//            BannerSplash.getInstance().loadAds(activity, callbackBannerSplash);
        } else {
            if (callbackBannerSplash != null) {
                callbackBannerSplash.onFailed();
            }
        }

        if (!PurchaseUtils.isNoAds(activity)) {
            MobileAds.initialize(activity, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                    if (callbackTimeout != null) {
                        callbackTimeout.cancelTimeout();
                    }
                    if (enableAds) {
//                        if (!isChooseLanguage || !isFromNotification) {
//                            NativeLanguageAll.getInstance().loadAdsAll(activity);
//                        } else {
//                            if (isShowLanguageReopen) {
//                                NativeLanguageAll.getInstance().loadAdsAll(activity);
//                            }
//                        }

//                        if (isShowOpenStart) {
//                            OpenAdsManager.getOpenAds().showOpenAds(activity, callbackAd);
//                        } else {
//                            IntersSplashAll.getInstance().loadAnsShow(activity, callbackAd);
//                        }
                    } else {
                        if (callbackAd != null) {
                            callbackAd.onNextAction();
                        }
                    }
                }
            });
            MobileAds.openAdInspector(activity, adInspectorError -> {
                if (adInspectorError != null) {
                    Log.e("AdInspector", "Lỗi khi mở Ad Inspector: ${error.message}");
                } else {
                    Log.d("AdInspector", "Ad Inspector đã được mở");
                }
            });
        } else {
            if (callbackAd != null) {
                callbackAd.onNextAction();
            }
        }

        boolean notificationLockScr = firebaseRemoteConfig.getBoolean(NOTIFICATION_LOCK_SCR);
        setNotificationLockScr(notificationLockScr);

        boolean notificationScr = firebaseRemoteConfig.getBoolean(NOTIFICATION_SCR);
        setNotificationScr(notificationScr);
    }

    public static void setIsShowOpenStart(boolean enable) {
        SharePreferUtils.putBoolean(IS_SHOW_OPEN_START, enable);
    }

    public static boolean getIsShowOpenStart() {
        return SharePreferUtils.getBoolean(IS_SHOW_OPEN_START, false);
    }

    public static void setEnableAds(boolean enable) {
        SharePreferUtils.putBoolean(ENABLE_ADS, enable);
    }

    public static boolean getEnableAds() {
        return SharePreferUtils.getBoolean(ENABLE_ADS, true);
    }

    public static void setEnableOpenResume(boolean enable) {
        SharePreferUtils.putBoolean(ENABLE_OPEN_RESUME, enable);
    }

    public static boolean getEnableOpenResume() {
        return SharePreferUtils.getBoolean(ENABLE_OPEN_RESUME, true);
    }

    public static void setEnableOpenStart(boolean enable) {
        SharePreferUtils.putBoolean(ENABLE_OPEN_START, enable);
    }

    public static boolean getEnableOpenStart() {
        return SharePreferUtils.getBoolean(ENABLE_OPEN_START, true);
    }

    public static void setEnableBannerCollapse(boolean enable) {
        SharePreferUtils.putBoolean(ENABLE_BANNER_COLLAPSE, enable);
    }

    public static boolean getEnableBannerCollapse() {
        return SharePreferUtils.getBoolean(ENABLE_BANNER_COLLAPSE, true);
    }

    public static void setEnableBanner(boolean enable) {
        SharePreferUtils.putBoolean(ENABLE_BANNER, enable);
    }

    public static boolean getEnableBanner() {
        return SharePreferUtils.getBoolean(ENABLE_BANNER, true);
    }

    public static void setEnableNative(boolean enable) {
        SharePreferUtils.putBoolean(ENABLE_NATIVE, enable);
    }

    public static boolean getEnableNative() {
        return SharePreferUtils.getBoolean(ENABLE_NATIVE, true);
    }

    public static void setEnableInters(boolean enable) {
        SharePreferUtils.putBoolean(ENABLE_INTERS, enable);
    }

    public static boolean getEnableInters() {
        return SharePreferUtils.getBoolean(ENABLE_INTERS, true);
    }

    public static void setEnableReward(boolean enable) {
        SharePreferUtils.putBoolean(ENABLE_REWARD, enable);
    }

    public static boolean getEnableReward() {
        return SharePreferUtils.getBoolean(ENABLE_REWARD, true);
    }

    public static void setEnableNativeCollapse(boolean enable) {
        SharePreferUtils.putBoolean(ENABLE_NATIVE_COLLAPSE, enable);
    }

    public static boolean getEnableNativeCollapse() {
        return SharePreferUtils.getBoolean(ENABLE_NATIVE_COLLAPSE, true);
    }

    public static void setTimeShowInters(String timeShow) {
        SharePreferUtils.putString(TIME_SHOW_INTERS, timeShow);
    }

    public static String getTimeShowInters() {
        return SharePreferUtils.getString(TIME_SHOW_INTERS, "15000");
    }

    public static void setIdIntersSplash(String idAds) {
        SharePreferUtils.putString(ID_INTERS_SPLASH, idAds);
    }

    public static String getIdIntersSplash() {
        return SharePreferUtils.getString(ID_INTERS_SPLASH, "");
    }

    public static void setIdIntersSplash2Floor(String idAds) {
        SharePreferUtils.putString(ID_INTERS_SPLASH_2FLOOR, idAds);
    }

    public static String getIdIntersSplash2Floor() {
        return SharePreferUtils.getString(ID_INTERS_SPLASH_2FLOOR, "");
    }

    public static void setIdIntersOnboard(String idAds) {
        SharePreferUtils.putString(ID_INTERS_ONBOARD, idAds);
    }

    public static String getIdIntersOnboard() {
        return SharePreferUtils.getString(ID_INTERS_ONBOARD, "");
    }

    public static void setIdIntersOnBoard2Floor(String idAds) {
        SharePreferUtils.putString(ID_INTERS_ONBOARD_2FLOOR, idAds);
    }

    public static String getIdIntersOnboard2floor() {
        return SharePreferUtils.getString(ID_INTERS_ONBOARD_2FLOOR, "");
    }

    public static void setIdIntersInApp(String idAds) {
        SharePreferUtils.putString(ID_INTERS_IN_APP, idAds);
    }

    public static String getIdIntersInApp() {
        return SharePreferUtils.getString(ID_INTERS_IN_APP, "");
    }

    public static void setIdIntersInApp2Floor(String idAds) {
        SharePreferUtils.putString(ID_INTERS_IN_APP_2FLOOR, idAds);
    }

    public static String getIdIntersInApp2Floor() {
        return SharePreferUtils.getString(ID_INTERS_IN_APP_2FLOOR, "");
    }

    public static void setIdNativeLanguage(String idAds) {
        SharePreferUtils.putString(ID_NATIVE_LANGUAGE, idAds);
    }

    public static String getIdNativeLanguage() {
        return SharePreferUtils.getString(ID_NATIVE_LANGUAGE, "");
    }

    public static void setIdNativeLanguage2Floor(String idAds) {
        SharePreferUtils.putString(ID_NATIVE_LANGUAGE_2FLOOR, idAds);
    }

    public static String getIdNativeLanguage2Floor() {
        return SharePreferUtils.getString(ID_NATIVE_LANGUAGE_2FLOOR, "");
    }

    public static void setIdNativeLanguageDup(String idAds) {
        SharePreferUtils.putString(ID_NATIVE_LANGUAGE_DUP, idAds);
    }

    public static String getIdNativeLanguageDup() {
        return SharePreferUtils.getString(ID_NATIVE_LANGUAGE_DUP, "");
    }

    public static void setIdNativeLanguageDup2Floor(String idAds) {
        SharePreferUtils.putString(ID_NATIVE_LANGUAGE_DUP_2FLOOR, idAds);
    }

    public static String getIdNativeLanguageDup2Floor() {
        return SharePreferUtils.getString(ID_NATIVE_LANGUAGE_DUP_2FLOOR, "");
    }

    public static void setIdNativeOnboard(String idAds) {
        SharePreferUtils.putString(ID_NATIVE_ONBOARD, idAds);
    }

    public static String getIdNativeOnboarding() {
        return SharePreferUtils.getString(ID_NATIVE_ONBOARD, "");
    }

    public static void setIdNativeOnboard2Floor(String idAds) {
        SharePreferUtils.putString(ID_NATIVE_ONBOARD_2FLOOR, idAds);
    }

    public static String getIdNativeOnboarding2Floor() {
        return SharePreferUtils.getString(ID_NATIVE_ONBOARD_2FLOOR, "");
    }

    public static void setIdNativeFullOnboard(String idAds) {
        SharePreferUtils.putString(ID_NATIVE_FULL_ONBOARD, idAds);
    }

    public static String getIdNativeFullOnboard() {
        return SharePreferUtils.getString(ID_NATIVE_FULL_ONBOARD, "");
    }

    public static void setIdNativeFullOnboard2Floor(String idAds) {
        SharePreferUtils.putString(ID_NATIVE_FULL_ONBOARD_2FLOOR, idAds);
    }

    public static String getIdNativeFullOnboard2Floor() {
        return SharePreferUtils.getString(ID_NATIVE_FULL_ONBOARD_2FLOOR, "");
    }

    public static void setIdNativeInApp(String idAds) {
        SharePreferUtils.putString(ID_NATIVE_IN_APP, idAds);
    }

    public static String getIdNativeInApp() {
        return SharePreferUtils.getString(ID_NATIVE_IN_APP, "");
    }

    public static void setIdNativeInApp2Floor(String idAds) {
        SharePreferUtils.putString(ID_NATIVE_IN_APP_2FLOOR, idAds);
    }

    public static String getIdNativeInApp2Floor() {
        return SharePreferUtils.getString(ID_NATIVE_IN_APP_2FLOOR, "");
    }

    public static void setIdOpenStart(String idAds) {
        SharePreferUtils.putString(ID_OPEN_START, idAds);
    }

    public static String getIdOpenStart() {
        return SharePreferUtils.getString(ID_OPEN_START, "");
    }

    public static void setIdOpenResume(String idAds) {
        SharePreferUtils.putString(ID_OPEN_RESUME, idAds);
    }

    public static String getIdOpenResume() {
        return SharePreferUtils.getString(ID_OPEN_RESUME, "");
    }

    public static void setIdBannerSplash(String idAds) {
        SharePreferUtils.putString(ID_BANNER_SPLASH, idAds);
    }

    public static String getIdBannerSplash() {
        return SharePreferUtils.getString(ID_BANNER_SPLASH, "");
    }

    public static void setIdBannerInApp(String idAds) {
        SharePreferUtils.putString(ID_BANNER_IN_APP, idAds);
    }

    public static String getIdBannerInApp() {
        return SharePreferUtils.getString(ID_BANNER_IN_APP, "");
    }

    public static void setIdRewardInApp(String idAds) {
        SharePreferUtils.putString(ID_REWARD_IN_APP, idAds);
    }

    public static String getIdRewardAll() {
        return SharePreferUtils.getString(ID_REWARD_IN_APP, "");
    }

    public static void setIsShowLanguageReopen(boolean enable) {
        SharePreferUtils.putBoolean(IS_SHOW_LANGUAGE_REOPEN, enable);
    }

    public static boolean getIsShowLanguageReopen() {
        return SharePreferUtils.getBoolean(IS_SHOW_LANGUAGE_REOPEN, false);
    }

    public static void setEnableBannerSplash(boolean enable) {
        SharePreferUtils.putBoolean(ENABLE_BANNER_SPLASH, enable);
    }

    public static boolean getEnableBannerSplash() {
        return SharePreferUtils.getBoolean(ENABLE_BANNER_SPLASH, true);
    }

    public static void setEnableIntersSplash(boolean enable) {
        SharePreferUtils.putBoolean(ENABLE_INTERS_SPLASH, enable);
    }

    public static boolean getEnableIntersSplash() {
        return SharePreferUtils.getBoolean(ENABLE_INTERS_SPLASH, true);
    }

    public static void setEnableIntersSplash2Floor(boolean enable) {
        SharePreferUtils.putBoolean(ENABLE_INTERS_SPLASH_2FLOOR, enable);
    }

    public static boolean getEnableIntersSplash2Floor() {
        return SharePreferUtils.getBoolean(ENABLE_INTERS_SPLASH_2FLOOR, true);
    }

    public static void setEnableNativeLanguage(boolean enable) {
        SharePreferUtils.putBoolean(ENABLE_NATIVE_LANGUAGE, enable);
    }

    public static boolean getEnableNativeLanguage() {
        return SharePreferUtils.getBoolean(ENABLE_NATIVE_LANGUAGE, true);
    }

    public static void setEnableNativeLanguage2Floor(boolean enable) {
        SharePreferUtils.putBoolean(ENABLE_NATIVE_LANGUAGE_2FLOOR, enable);
    }

    public static boolean getEnableNativeLanguage2Floor() {
        return SharePreferUtils.getBoolean(ENABLE_NATIVE_LANGUAGE_2FLOOR, true);
    }

    public static void setEnableNativeLanguageDup(boolean enable) {
        SharePreferUtils.putBoolean(ENABLE_NATIVE_LANGUAGE_DUP, enable);
    }

    public static boolean getEnableNativeLanguageDup() {
        return SharePreferUtils.getBoolean(ENABLE_NATIVE_LANGUAGE_DUP, true);
    }

    public static void setEnableNativeLanguageDup2Floor(boolean enable) {
        SharePreferUtils.putBoolean(ENABLE_NATIVE_LANGUAGE_DUP_2FLOOR, enable);
    }

    public static boolean getEnableNativeLanguageDup2Floor() {
        return SharePreferUtils.getBoolean(ENABLE_NATIVE_LANGUAGE_DUP_2FLOOR, true);
    }

    public static void setEnableNativeOnboard(boolean enable) {
        SharePreferUtils.putBoolean(ENABLE_NATIVE_ONBOARD, enable);
    }

    public static boolean getEnableNativeOnboard() {
        return SharePreferUtils.getBoolean(ENABLE_NATIVE_ONBOARD, true);
    }

    public static void setEnableNativeOnboard2Floor(boolean enable) {
        SharePreferUtils.putBoolean(ENABLE_NATIVE_ONBOARD_2FLOOR, enable);
    }

    public static boolean getEnableNativeOnboard2Floor() {
        return SharePreferUtils.getBoolean(ENABLE_NATIVE_ONBOARD_2FLOOR, true);
    }

    public static void setEnableNativeFullScr(boolean enable) {
        SharePreferUtils.putBoolean(ENABLE_NATIVE_FULL_SCR, enable);
    }

    public static boolean getEnableNativeFullScr() {
        return SharePreferUtils.getBoolean(ENABLE_NATIVE_FULL_SCR, true);
    }

    public static void setEnableNativeFullScr2Floor(boolean enable) {
        SharePreferUtils.putBoolean(ENABLE_NATIVE_FULL_SCR_2FLOOR, enable);
    }

    public static boolean getEnableNativeFullScr2Floor() {
        return SharePreferUtils.getBoolean(ENABLE_NATIVE_FULL_SCR_2FLOOR, true);
    }

    public static void setEnableShowNativeFullScr1(boolean enable) {
        SharePreferUtils.putBoolean(ENABLE_SHOW_NATIVE_FULL_SCR_1, enable);
    }

    public static boolean getEnableShowNativeFullScr1() {
        return SharePreferUtils.getBoolean(ENABLE_SHOW_NATIVE_FULL_SCR_1, true);
    }

    public static void setEnableShowNativeFullScr2(boolean enable) {
        SharePreferUtils.putBoolean(ENABLE_SHOW_NATIVE_FULL_SCR_2, enable);
    }

    public static boolean getEnableShowNativeFullScr2() {
        return SharePreferUtils.getBoolean(ENABLE_SHOW_NATIVE_FULL_SCR_2, true);
    }

    public static void setEnableIntersOnboard(boolean enable) {
        SharePreferUtils.putBoolean(ENABLE_INTERS_ONBOARD, enable);
    }

    public static boolean getEnableIntersOnboard() {
        return SharePreferUtils.getBoolean(ENABLE_INTERS_ONBOARD, true);
    }

    public static void setEnableIntersOnboard2Floor(boolean enable) {
        SharePreferUtils.putBoolean(ENABLE_INTERS_ONBOARD_2FLOOR, enable);
    }

    public static boolean getEnableIntersOnboard2Floor() {
        return SharePreferUtils.getBoolean(ENABLE_INTERS_ONBOARD_2FLOOR, true);
    }

    public static void setEnableIntersInApp(boolean enable) {
        SharePreferUtils.putBoolean(ENABLE_INTERS_IN_APP, enable);
    }

    public static boolean getEnableIntersInApp() {
        return SharePreferUtils.getBoolean(ENABLE_INTERS_IN_APP, true);
    }

    public static void setEnableIntersInApp2Floor(boolean enable) {
        SharePreferUtils.putBoolean(ENABLE_INTERS_IN_APP_2FLOOR, enable);
    }

    public static boolean getEnableIntersInApp2Floor() {
        return SharePreferUtils.getBoolean(ENABLE_INTERS_IN_APP_2FLOOR, true);
    }

    public static void setNfsTimeScroll(long timeScroll) {
        SharePreferUtils.putLong(NFS_TIME_SCROLL, timeScroll);
    }

    public static long getNfsTimeScroll() {
        return SharePreferUtils.getLong(NFS_TIME_SCROLL, 15000);
    }

    public static void setLayoutButtonAds(String layout) {
        SharePreferUtils.putString(LAYOUT_BUTTON_ADS, layout);
    }

    public static String getLayoutButtonAds() {
        return SharePreferUtils.getString(LAYOUT_BUTTON_ADS, "");
    }

    public static void setEnableLfoReloadItem(boolean enable) {
        SharePreferUtils.putBoolean(ENABLE_LFO_RELOAD_ITEM, enable);
    }

    public static boolean getEnableLfoReloadItem() {
        return SharePreferUtils.getBoolean(ENABLE_LFO_RELOAD_ITEM, true);
    }

    public static void setEnableNativeReloadMedia(boolean enable) {
        SharePreferUtils.putBoolean(ENABLE_NATIVE_RELOAD_MEDIA, enable);
    }

    public static boolean getEnableNativeReloadMedia() {
        return SharePreferUtils.getBoolean(ENABLE_NATIVE_RELOAD_MEDIA, true);
    }

    public static void setEnableNativeInApp(boolean enable) {
        SharePreferUtils.putBoolean(ENABLE_NATIVE_IN_APP, enable);
    }

    public static boolean getEnableNativeInApp() {
        return SharePreferUtils.getBoolean(ENABLE_NATIVE_IN_APP, true);
    }

    public static void setEnableNativeInApp2Floor(boolean enable) {
        SharePreferUtils.putBoolean(ENABLE_NATIVE_IN_APP_2FLOOR, enable);
    }

    public static boolean getEnableNativeInApp2Floor() {
        return SharePreferUtils.getBoolean(ENABLE_NATIVE_IN_APP_2FLOOR, true);
    }

    public static void setNotificationLockScr(boolean enable) {
        SharePreferUtils.putBoolean(NOTIFICATION_LOCK_SCR, enable);
    }

    public static boolean getNotificationLockScr() {
        return SharePreferUtils.getBoolean(NOTIFICATION_LOCK_SCR, true);
    }

    public static void setNotificationScr(boolean enable) {
        SharePreferUtils.putBoolean(NOTIFICATION_SCR, enable);
    }

    public static boolean getNotificationScr() {
        return SharePreferUtils.getBoolean(NOTIFICATION_SCR, true);
    }
}