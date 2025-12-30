package com.module.config.onboard

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.bumptech.glide.Glide
import com.module.ads.admob.natives.NativeInApp
import com.module.config.AdPlaceName
import com.module.config.BaseFragment
import com.module.config.R
import com.module.config.databinding.FragmentOnboardBinding


class OnboardTwoFragment : BaseFragment<FragmentOnboardBinding>() {
    private var isLoadAdOnBoard = false

    companion object {
        fun newInstance(): OnboardTwoFragment {
            val fragment = OnboardTwoFragment()
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
        mBinding.tvContent.text = "Top 2 vietnam"

        mBinding.rlAdsFullscreen.visibility = View.GONE
        mBinding.lnNative.visibility = View.INVISIBLE

        mBinding.ivIndicator1.setImageResource(R.drawable.circle_indicator)
        mBinding.ivIndicator2.setImageResource(R.drawable.circle_indicator_selected)
        mBinding.ivIndicator3.setImageResource(R.drawable.circle_indicator)
    }

    override fun onClickViews() {
        super.onClickViews()
        mBinding.tvNext.setOnClickListener {
            activity?.let { act ->
                if (act is OnboardActivity) {
                    act.onNextPage()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.e("TAG", "onResume: Onboard 2")
        activity?.let { act ->
            if (!isLoadAdOnBoard) {
                NativeInApp.getInstance().preLoad(
                    act,
                    "ca-app-pub-3940256099942544/2247696110",
                    "ca-app-pub-3940256099942544/2247696110",
                    AdPlaceName.NATIVE_OB2,
                )
                NativeInApp.getInstance().preLoad(
                    act,
                    "ca-app-pub-3940256099942544/2247696110",
                    "ca-app-pub-3940256099942544/2247696110",
                    AdPlaceName.NATIVE_FULL_OB2,
                )
                isLoadAdOnBoard = true
            }
        }
    }
}