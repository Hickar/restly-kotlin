package com.hickar.restly.ui.request.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hickar.restly.ui.request.RequestFragment
import com.hickar.restly.ui.request.RequestTabFragment
import com.hickar.restly.ui.request.ResponseTabFragment

class RequestViewPagerAdapter(fragment: RequestFragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when(position) {
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