package com.module.config.views.activities.onboard

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class OnboardAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    private val listFragment = arrayListOf<Fragment>()

    fun submitData(newData: List<Fragment>) {
        listFragment.clear()
        listFragment.addAll(newData)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = listFragment.size

    override fun createFragment(position: Int): Fragment {
        return listFragment[position]
    }
}