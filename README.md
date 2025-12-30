## Quảng cáo (AdMob Integration)

Project sử dụng **Google AdMob** để hiển thị quảng cáo.

### 1. Bắt đầu với implementation

Trong dependency của build.gradle (:app)

    implementation 'com.google.android.gms:play-services-ads:24.7.0'
    implementation 'com.github.UniSoft-Android:base-sdk:1.3' hoặc mới nhất

Trong setting.gradle

    dependencyResolutionManagement {
        repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
        repositories {
            mavenCentral()
            maven { url 'https://jitpack.io' }
        }
    }

### 3. Khởi tạo ModuleApplication (chỉ gọi 1 lần)

Gọi ngay khi app khởi động, tốt nhất trong `Application` class, extend lại ModuleApplication()

    class MyApplication: ModuleApplication() {
        override fun onCreate() {
            super.onCreate()

            addExcludedActivity(OnboardActivity::class.java)  // Thêm activity mà bạn không muốn show resume ads vào đây
            addExcludedActivity(SplashActivity::class.java)
        }
    }

### 3. Aoa ads

    OpenAdsManager.getOpenAds().showOpenAds(
        this,                                      // Activity
        "ca-app-pub-3940256099942544/9257395921",  // Id ads Aoa
        object : OnAoaListener(){}) {              // Callback khi load Aoa, Override lại các hàm bên trong nếu cần dùng
        nextAction()                               // Next action, chuyển màn hoặc ...
    }

### 4. Interstitial ads

     IntersInApp.getInstance().loadAds(
        this,                                       // Activity
        "ca-app-pub-3940256099942544/1033173712",   // Id ads inter High floor
        "ca-app-pub-3940256099942544/1033173712",   // Id ads inter Normal
        AdPlaceName.INTER_MAIN,                     // Vị trí gắn ads (dùng để break các vị trí ads khác nhau
        false,                                      // Bật tắt reload ads, "true" cho phép reload ads sau khi dismiss Inter
        object : OnInterListener() {}               // Callback khi load Inter, Override lại các hàm bên trong nếu cần dùng
     )

### 5. Native ads

*** Trong activity gọi Native

     NativeInApp.getInstance().loadAndShow(
        this,                                       // Activity
        lnNative,                                   // LinearLayout chứa Native
        R.layout.layout_native_2,                   // Layout Ads Dev có thể tự custom rồi gọi vào
        "ca-app-pub-3940256099942544/2247696110",   // Id ads Native High floor
        "ca-app-pub-3940256099942544/2247696110",   // Id ads Native Normal
        AdPlaceName.NATIVE_MAIN,                    // Vị trí gắn ads (dùng để break các vị trí ads khác nhau)
        object : OnNativeListener() {}              // Callback khi load Native, Override lại các hàm bên trong nếu cần dùng
     )

     NativeInApp.getInstance().preLoad(
        this,                                       // Activity
        "ca-app-pub-3940256099942544/2247696110",   // Id ads Native High floor 
        "ca-app-pub-3940256099942544/2247696110",   // Id ads Native Normal  
        AdPlaceName.NATIVE_MAIN2                    // Vị trí gắn ads (dùng để break các vị trí ads khác nhau)
     )

     NativeInApp.getInstance().showOrLoadNativeAd(
        this,                                       // Activity
        lnNative,                                   // LinearLayout chứa Native
        R.layout.layout_native_2,                   // Layout Ads Dev có thể tự custom rồi gọi vào
        "ca-app-pub-3940256099942544/2247696110",   // Id ads Native High floor
        "ca-app-pub-3940256099942544/2247696110",   // Id ads Native Normal
        AdPlaceName.NATIVE_MAIN,                    // Vị trí gắn ads (dùng để break các vị trí ads khác nhau)
        object : OnNativeListener() {}              // Callback khi load Native, Override lại các hàm bên trong nếu cần dùng
     )

*** Trong .xml của màn đó

    <LinearLayout
        android:id="@+id/ln_native"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical">

        <include layout="@layout/shimmer_native_large" />
    </LinearLayout>

### 6. Banner ads

*** Trong activity gọi Banner

    BannerInApp.getInstance().loadAndShow(
        this,                                       // Activity
        lnBanner,                                   // LinearLayout chứa Banner
        "ca-app-pub-3940256099942544/6300978111",   // Id ads Native Normal
        false,                                      // Bật tắt show banner collapse
        object : OnBannerListener() {}              // Callback khi load Banner, Override lại các hàm bên trong nếu cần dùng
    )

*** Trong .xml của màn đó

    <LinearLayout
        android:id="@+id/ln_banner"
        android:layout_width="match_parent"
        android:layout_height="@dimen/_45sdp"
        android:orientation="vertical"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent">

        <include layout="@layout/shimmer_banner" />
    </LinearLayout>


### 7. Reward Ads
Logic load trước show sau (có highfloor)

    RewardInApp.getInstance().loadReward(       
        this,                                        // Activity
        "ca-app-pub-3940256099942544/5224354917",    // Id ads Native High floor
        "ca-app-pub-3940256099942544/5224354917",    // Id ads Native Normal
        AdPlaceName.REWARD_MAIN,                     // Vị trí gắn ads (dùng để break các vị trí ads khác nhau)
        object : OnRewardListener() {}               // Callback khi load Reward, Override lại các hàm bên trong nếu cần dùng
    )

    RewardInApp.getInstance().showReward(this, AdPlaceName.REWARD_MAIN) {
        nextAction()
    }

### 8. Adjust
Khởi tạo adjust ở MyApplication và truyền id vào đó

    class MyApplication: ModuleApplication() {
        override fun onCreate() {
            super.onCreate()
            AdjustTracking.initAdjust(this, "adjust_id")
            addExcludedActivity(OnboardActivity::class.java)  // Thêm activity mà bạn không muốn show resume ads vào đây
            addExcludedActivity(SplashActivity::class.java)
        }
    }







