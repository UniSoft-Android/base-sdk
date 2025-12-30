package com.module.config

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.viewbinding.ViewBinding
import com.module.ads.utils.FBTracking

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {
    private var _binding: VB? = null
    protected val mBinding: VB get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = inflateBinding(layoutInflater)
        setContentView(mBinding.root)

        initViews()
        onClickViews()
        observerData()
    }

    abstract fun inflateBinding(inflater: LayoutInflater): VB

    open fun initViews() {}

    open fun onClickViews() {}

    open fun observerData() {}

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        hideNavigationBar()
    }

    private fun hideNavigationBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowInsetsControllerCompat(window, window.decorView).let { controller ->
                controller.hide(WindowInsetsCompat.Type.systemBars())
                controller.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            hideSystemUIBelowR()
        }
    }

    private fun hideSystemUIBelowR() {
        val decorView: View = window.decorView
        val uiOptions = decorView.systemUiVisibility
        var newUiOptions = uiOptions
        newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_LOW_PROFILE
        newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE
        newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        decorView.systemUiVisibility = newUiOptions
    }

    fun logEvent(event: String, bundle: Bundle? = null) {
        FBTracking.funcTracking(this, event, bundle)
    }

    override fun onDestroy() {
        _binding = null // tránh leak
        super.onDestroy()
    }
}