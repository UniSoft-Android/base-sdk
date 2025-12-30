package com.module.config

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.ads.LoadAdError
import com.module.ads.admob.aoa.OpenAdsManager
import com.module.ads.admob.inters.IntersInApp
import com.module.ads.admob.natives.NativeInApp
import com.module.ads.callback.OnAoaListener
import com.module.ads.callback.OnInterListener
import com.module.config.onboard.OnboardActivity
import java.util.Locale

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        NativeInApp.getInstance().preLoad(
            this,
            "ca-app-pub-3940256099942544/2247696110",
            "ca-app-pub-3940256099942544/2247696110",
            AdPlaceName.NATIVE_OB,
        )

        NativeInApp.getInstance().preLoad(
            this,
            "ca-app-pub-3940256099942544/2247696110",
            "ca-app-pub-3940256099942544/2247696110",
            AdPlaceName.NATIVE_FULL_OB,
        )

//        IntersInApp.getInstance().loadAds(
//            this,
//            "ca-app-pub-3940256099942544/1033173712",
//            "ca-app-pub-3940256099942544/1033173712",
//            AdPlaceName.INTER_SPLASH,
//            false,
//            object : OnInterListener(){
//                override fun onAdLoaded() {
//                    IntersInApp.getInstance().showInters(this@SplashActivity, AdPlaceName.INTER_SPLASH, 10000, false) {
//                        Log.e("TAGTAM", "Splash next acti")
//                        startActivity(Intent(this@SplashActivity, OnboardActivity::class.java))
//                        finish()
//                    }
//                }
//
//                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
//                    Log.e("TAGTAM", "Splash next acti")
//                    startActivity(Intent(this@SplashActivity, OnboardActivity::class.java))
//                    finish()
//                }
//
//            })

        OpenAdsManager.getOpenAds().showOpenAds(
            this,
            "ca-app-pub-3940256099942544/9257395921",
            object : OnAoaListener(){}) {
            startActivity(Intent(this@SplashActivity, OnboardActivity::class.java))
            finish()
        }
    }
}