package com.hickar.restly.view.request.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hickar.restly.view.request.RequestFragment
import com.hickar.restly.view.request.RequestTabFragment
import com.hickar.restly.view.request.ResponseTabFragment

class RequestViewPagerAdapter(fragment: RequestFragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                RequestTabFragment()
            }
            1 -> {
                ResponseTabFragment()
            }
            else -> {
                return Fragment()
            }
        }
    }
}