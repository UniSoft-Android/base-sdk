package com.module.config

import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.ads.AdValue
import com.google.android.gms.ads.LoadAdError
import com.module.ads.admob.natives.NativeInApp
import com.module.ads.callback.OnNativeListener

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val lnNative = findViewById<LinearLayout>(R.id.ln_native)

        NativeInApp.getInstance().showOrLoadNativeAd(
            this,
            lnNative,
            R.layout.layout_native_2,
            "ca-app-pub-3940256099942544/2247696110",
            "ca-app-pub-3940256099942544/2247696110",
            AdPlaceName.NATIVE_MAIN2,null
        )
    }
}