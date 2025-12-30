package com.module.config

import com.module.ads.app.ModuleApplication
import com.module.config.onboard.OnboardActivity

class MyApplication: ModuleApplication() {

    override fun onCreate() {
        super.onCreate()

        addExcludedActivity(OnboardActivity::class.java)
        addExcludedActivity(SplashActivity::class.java)
    }
}