package com.hickar.restly.view.request.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hickar.restly.view.request.RequestTabFragment
import com.hickar.restly.view.requestBody.RequestBodyBinaryFragment
import com.hickar.restly.view.requestBody.RequestBodyFormdataFragment
import com.hickar.restly.view.requestBody.RequestBodyRawFragment
import com.hickar.restly.view.requestBody.RequestDetailBodyFormDataFragment

class RequestBodyViewPagerAdapter(fragment: RequestTabFragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                RequestBodyFormdataFragment()
            }
            1 -> {
                RequestDetailBodyFormDataFragment()
            }
            2 -> {
                RequestBodyRawFragment()
            }
            3 -> {
                RequestBodyBinaryFragment()
            }
            else -> {
                return Fragment()
            }
        }
    }
}