package com.module.config

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.module.ads.utils.FBTracking

abstract class BaseFragment<VB : ViewBinding> : Fragment() {

    private var _binding: VB? = null
    protected val mBinding: VB get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflateBinding(layoutInflater)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        onClickViews()
        observerData()
    }

    abstract fun inflateBinding(inflater: LayoutInflater): VB

    open fun initViews() {}

    open fun onClickViews() {}
    open fun observerData() {}

    fun logEvent(event: String, bundle: Bundle? = null) {
        activity?.let { act ->
            FBTracking.funcTracking(act, event.lowercase(), bundle)
        }
    }

    fun logTime(fragment: Fragment) {
        val bundle = Bundle()
        bundle.putString("screen_name", fragment.javaClass.simpleName)
        bundle.putString("screen_class", fragment.javaClass.simpleName)
        FBTracking.funcTracking(activity, "screen_view", bundle)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}