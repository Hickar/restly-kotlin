package com.hickar.restly.ui.requestDetail

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class RequestDetailViewPagerAdapter(private val fragment: RequestDetailFragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> {
                RequestDetailBodyUrlEncodedFragment(fragment.requestDetailViewModel)
            }
            1 -> {
                RequestDetailBodyFormDataFragment(fragment.requestDetailViewModel)
            }
            2 -> {
                RequestDetailBodyRawFragment(fragment.requestDetailViewModel)
            }
            3 -> {
                RequestDetailBodyBinaryFragment(fragment.requestDetailViewModel)
            }
            else -> {
                return Fragment()
            }
        }
    }
}