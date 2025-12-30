package com.module.config

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.ads.AdValue
import com.google.android.gms.ads.LoadAdError
import com.module.ads.admob.banner.BannerInApp
import com.module.ads.admob.inters.IntersInApp
import com.module.ads.admob.natives.NativeInApp
import com.module.ads.admob.reward.RewardInApp
import com.module.ads.callback.OnBannerListener
import com.module.ads.callback.OnInterListener
import com.module.ads.callback.OnNativeListener
import com.module.ads.callback.OnRewardListener
import com.module.config.onboard.OnboardActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val lnNative = findViewById<LinearLayout>(R.id.ln_native)
        val next = findViewById<Button>(R.id.btnNext)
        val next2 = findViewById<Button>(R.id.btnNext2)

        next.setOnClickListener {
            IntersInApp.getInstance().showInters(
                this@MainActivity,
                AdPlaceName.INTER_MAIN,
                10000,
                false
            ) {
                startActivity(Intent(this, MainActivity2::class.java))
            }
        }

        next2.setOnClickListener {
            RewardInApp.getInstance().showReward(this, AdPlaceName.REWARD_MAIN) {
                startActivity(Intent(this, MainActivity2::class.java))
            }
        }

        NativeInApp.getInstance().loadAndShow(
            this,
            lnNative,
            R.layout.layout_native_2,
            "ca-app-pub-3940256099942544/2247696110",
            "ca-app-pub-3940256099942544/2247696110",
            "NATIVE_IN_APP",
            object : OnNativeListener() {})

        NativeInApp.getInstance().preLoad(
            this,
            "ca-app-pub-3940256099942544/2247696110",
            "ca-app-pub-3940256099942544/2247696110",
            AdPlaceName.NATIVE_MAIN2
        )

        BannerInApp.getInstance().loadAndShow(
            this@MainActivity,
            findViewById(R.id.ln_banner),
            "ca-app-pub-3940256099942544/6300978111",
            false,
            object : OnBannerListener() {})

        RewardInApp.getInstance().loadReward(
            this,
            "ca-app-pub-3940256099942544/5224354917",
            "ca-app-pub-3940256099942544/5224354917",
            AdPlaceName.REWARD_MAIN,
            object : OnRewardListener() {})

        IntersInApp.getInstance().loadAds(
            this,
            "ca-app-pub-3940256099942544/1033173712",
            "ca-app-pub-3940256099942544/1033173712",
            AdPlaceName.INTER_MAIN,
            false,
            object : OnInterListener() {})
    }
}