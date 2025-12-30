package com.module.config.onboard

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.google.android.gms.ads.AdValue
import com.google.android.gms.ads.LoadAdError
import com.module.ads.admob.aoa.ResumeAdsManager
import com.module.ads.admob.natives.NativeInApp
import com.module.ads.callback.OnNativeListener
import com.module.config.AdPlaceName
import com.module.config.BaseFragment
import com.module.config.R
import com.module.config.databinding.FragmentOnboardBinding


class OnboardNativeFull2Fragment : BaseFragment<FragmentOnboardBinding>() {

    private var isAdShown = false
    private var isFirstResume = true

    companion object {
        fun newInstance(): OnboardNativeFull2Fragment {
            val fragment = OnboardNativeFull2Fragment()
            return fragment
        }
    }

    override fun inflateBinding(inflater: LayoutInflater): FragmentOnboardBinding {
        return FragmentOnboardBinding.inflate(inflater)
    }

    override fun initViews() {
        super.initViews()
        mBinding.rlAdsFullscreen.visibility = View.VISIBLE
        mBinding.lnNative.visibility = View.INVISIBLE
        mBinding.rlButton.visibility = View.GONE

        if (!ResumeAdsManager.shouldReloadAd) {
            logEvent("native_ob_2_scr_view")
        }
    }

    override fun onResume() {
        super.onResume()
        activity?.let { act ->
            if (isFirstResume) {
                initAdmob()
                isFirstResume = false
                return
            }
            Log.e("TAG", "onResume: Onboard Native Full 2")

            if (!ResumeAdsManager.shouldReloadAd) {
                // Load lại và hiển thị quảng cáo mới trong onResume
                isAdShown = false // Reset để cho phép hiển thị quảng cáo mới
                NativeInApp.getInstance().destroy(AdPlaceName.NATIVE_FULL_OB2) // Xóa quảng cáo cũ
                loadAndShowAds()
            } else {
                ResumeAdsManager.shouldReloadAd = false
            }
        }
    }

    private fun initAdmob() {
        showOrLoadNativeAd()
    }

    private fun showOrLoadNativeAd() {
        activity?.let { act ->
            NativeInApp.getInstance().showOrLoadNativeAd(
                act,
                mBinding.lnNativeFullscreen,
                R.layout.layout_native_fullscreen_4,
                "ca-app-pub-3940256099942544/2247696110",
                "ca-app-pub-3940256099942544/2247696110",
                AdPlaceName.NATIVE_FULL_OB2,object : OnNativeListener() {}
            )
        }
    }

    private fun loadAndShowAds() {
        activity?.let { act ->
            Log.e("TAG", "FOB2 loadAndShowAds :")
            NativeInApp.getInstance().loadAndShow(
                act,
                mBinding.lnNativeFullscreen,
                R.layout.layout_native_fullscreen_4,
                "ca-app-pub-3940256099942544/2247696110",
                "ca-app-pub-3940256099942544/2247696110",
                AdPlaceName.NATIVE_FULL_OB2,
                object : OnNativeListener() {})
        }
    }
}