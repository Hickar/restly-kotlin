package com.hickar.restly.ui.request

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hickar.restly.ui.requestBody.RequestBodyBinaryFragment
import com.hickar.restly.ui.requestBody.RequestBodyFormdataFragment
import com.hickar.restly.ui.requestBody.RequestBodyRawFragment
import com.hickar.restly.ui.requestBody.RequestDetailBodyFormDataFragment

class RequestViewPagerAdapter(private val fragment: RequestDetailFragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when(position) {
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