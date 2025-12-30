package com.module.config.onboard


import android.os.CountDownTimer
import android.view.LayoutInflater
import androidx.viewpager2.widget.ViewPager2
import com.module.ads.admob.natives.NativeInApp
import com.module.ads.remote.FirebaseQuery
import com.module.config.AdPlaceName
import com.module.config.BaseActivity
import com.module.config.databinding.ActivityOnboardBinding
import com.module.config.views.activities.onboard.OnboardAdapter


class OnboardActivity : BaseActivity<ActivityOnboardBinding>() {
    private var onboardAdapter: OnboardAdapter? = null

    private var countTimer1: CountDownTimer? = null
    private var countTimer2: CountDownTimer? = null

    override fun inflateBinding(inflater: LayoutInflater): ActivityOnboardBinding {
        return ActivityOnboardBinding.inflate(inflater)
    }

    override fun initViews() {
        super.initViews()
        initList()
    }

    private fun initList() {
        val listFragment = arrayListOf(
            OnboardOneFragment.newInstance(),
            OnboardNativeFullFragment.newInstance(),
            OnboardTwoFragment.newInstance(),
            OnboardNativeFull2Fragment.newInstance(),
            OnboardThreeFragment.newInstance()
        )
        onboardAdapter = OnboardAdapter(this)
        onboardAdapter?.submitData(listFragment)
        mBinding.vpOnBoarding.apply {
            offscreenPageLimit = 1
            currentItem = 0
            adapter = onboardAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                private var lastPage = -1

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    countTimer1?.cancel()
                    countTimer2?.cancel()
                    NativeInApp.getInstance().isShowing = false

                    // Nếu vừa từ NativeFull1 (1) sang OB2 (2) hoặc OB1 (0)
                    if (lastPage == 1 && (position == 0 || position == 2)) {
                        logEvent("native_ob_1_scr_complete")
                    }

                    // Nếu vừa từ NativeFull2 (3) sang OB2 (2) hoặc OB3 (4)
                    if (lastPage == 3 && (position == 2 || position == 4)) {
                        logEvent("native_ob_2_scr_complete")
                    }

                    lastPage = position

                    when (position) {
                        1, 3 -> {
                            NativeInApp.getInstance().isShowing = true
                            if (position == 1) {
                                countTimer1 = object :
                                    CountDownTimer(FirebaseQuery.getNfsTimeScroll(), 1000) {
                                    override fun onTick(millisUntilFinished: Long) {

                                    }

                                    override fun onFinish() {
                                        mBinding.vpOnBoarding.currentItem = 2
                                    }
                                }
                                countTimer1?.start()
                            }
                            if (position == 3) {
                                countTimer2 = object :
                                    CountDownTimer(FirebaseQuery.getNfsTimeScroll(), 1000) {
                                    override fun onTick(millisUntilFinished: Long) {}

                                    override fun onFinish() {
                                        mBinding.vpOnBoarding.currentItem = 4
                                    }
                                }
                                countTimer2?.start()
                            }
                        }
                    }
                }


            })
        }
    }

    fun onNextPage() {
        onboardAdapter?.let {
            if (mBinding.vpOnBoarding.currentItem + 1 < it.itemCount) {
                mBinding.vpOnBoarding.currentItem += 1
            }
        }
    }

    override fun onBackPressed() {}

    override fun onDestroy() {
        NativeInApp.getInstance().destroy(AdPlaceName.NATIVE_OB)
        NativeInApp.getInstance().destroy(AdPlaceName.NATIVE_FULL_OB)
        super.onDestroy()
    }
}