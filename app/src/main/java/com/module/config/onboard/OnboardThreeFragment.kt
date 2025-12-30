package com.module.config.onboard

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdValue
import com.google.android.gms.ads.LoadAdError
import com.module.ads.admob.aoa.ResumeAdsManager
import com.module.ads.admob.natives.NativeInApp
import com.module.ads.callback.OnNativeListener
import com.module.ads.remote.FirebaseQuery
import com.module.ads.utils.PurchaseUtils
import com.module.config.AdPlaceName
import com.module.config.BaseFragment
import com.module.config.MainActivity
import com.module.config.R
import com.module.config.databinding.FragmentOnboardBinding

class OnboardThreeFragment : BaseFragment<FragmentOnboardBinding>() {

    private var isAdShown = false
    private var isFirstResume = true
    private var isNextActivity = false

    companion object {
        fun newInstance(): OnboardThreeFragment {
            val fragment = OnboardThreeFragment()
            return fragment
        }
    }

    override fun inflateBinding(inflater: LayoutInflater): FragmentOnboardBinding {
        return FragmentOnboardBinding.inflate(inflater)
    }

    override fun initViews() {
        super.initViews()
        Glide.with(this).load(R.drawable.img_on_board_1).into(mBinding.ivThumb)
        mBinding.tvTitle.text = "Shubarb"
        mBinding.tvContent.text = "Top 3 vietnam"
        mBinding.rlAdsFullscreen.visibility = View.GONE

        mBinding.ivIndicator1.setImageResource(R.drawable.circle_indicator)
        mBinding.ivIndicator2.setImageResource(R.drawable.circle_indicator)
        mBinding.ivIndicator3.setImageResource(R.drawable.circle_indicator_selected)
    }

    override fun onClickViews() {
        super.onClickViews()
        activity?.let { activity ->
            mBinding.tvNext.setOnClickListener {
                logEvent("onboarding_3_start_click")
                startActivity(Intent(activity, MainActivity::class.java))
                activity.finish()
            }
        }

    }

    override fun onResume() {
        super.onResume()

        activity?.let { act ->
            if (PurchaseUtils.isNoAds(act) || !FirebaseQuery.getEnableAds()) {
                mBinding.lnNative.visibility = View.GONE
            }
        }
        if (isNextActivity) return
        if (isFirstResume) {
            initAdmob()
            isFirstResume = false
            return
        }

        if (!ResumeAdsManager.shouldReloadAd) {
            // Load lại và hiển thị quảng cáo mới trong onResume
            isAdShown = false // Reset để cho phép hiển thị quảng cáo mới
            NativeInApp.getInstance().destroy(AdPlaceName.NATIVE_OB2) // Xóa quảng cáo cũ
            loadAndShowAds()
        } else {
            ResumeAdsManager.shouldReloadAd = false
        }
    }

    private fun initAdmob() {
        showOrLoadNativeAd()
    }

    private fun showOrLoadNativeAd() {
        activity?.let { act ->
            NativeInApp.getInstance().showOrLoadNativeAd(
                act,
                mBinding.lnNative,
                R.layout.layout_native_2,
                "ca-app-pub-3940256099942544/2247696110",
                "ca-app-pub-3940256099942544/2247696110",
                AdPlaceName.NATIVE_OB2,
                object : OnNativeListener() {}
            )
        }
    }

    private fun loadAndShowAds() {
        activity?.let { act ->
            Log.e("TAG", "Ob2 loadAndShowAds: ")
            NativeInApp.getInstance().loadAndShow(
                act,
                mBinding.lnNative,
                R.layout.layout_native_2,
                "ca-app-pub-3940256099942544/2247696110",
                "ca-app-pub-3940256099942544/2247696110",
                AdPlaceName.NATIVE_OB2,
                object : OnNativeListener() {})
        }
    }
}