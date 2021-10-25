package com.hickar.restly.view.request.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hickar.restly.view.responseBody.ResponseBodyPreviewFragment
import com.hickar.restly.view.responseBody.ResponseBodyRawFragment

class ResponseBodyViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ResponseBodyRawFragment()
            1 -> ResponseBodyPreviewFragment()
            else -> Fragment()
        }
    }

}