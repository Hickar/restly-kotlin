package com.hickar.restly.view.request

import android.graphics.ColorFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.hickar.restly.R
import com.hickar.restly.databinding.ResponseTabBinding
import com.hickar.restly.extensions.toDocumentSize
import com.hickar.restly.extensions.toResponseTime
import com.hickar.restly.view.request.adapters.ResponseBodyViewPagerAdapter
import com.hickar.restly.viewModel.RequestViewModel

class ResponseTabFragment : Fragment() {
    private var _binding: ResponseTabBinding? = null
    private val binding get() = _binding!!

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    private val viewModel: RequestViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = ResponseTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViewPager()
        setupObservers()
    }

    private fun setupViewPager() {
        tabLayout = binding.responseBodyTabs
        viewPager = binding.responseBodyView
        viewPager.adapter = ResponseBodyViewPagerAdapter(this)
        viewPager.setPageTransformer(MarginPageTransformer(48))
        viewPager.isUserInputEnabled = false

        val bodyTabs = listOf("Raw", "Preview")
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = bodyTabs[position]
        }.attach()
    }

    private fun setupObservers() {
        viewModel.response.observe(viewLifecycleOwner) { response ->
            binding.responseStatusCode.text = response.code.toString()
            binding.responseStatusTime.text = response.roundTripTime.toResponseTime()
            binding.responseStatusSize.text = response.size.toDocumentSize()

            val statusIconColorId = if (response.code < 400) R.color.green_700 else R.color.red_700
            val statusIconColor = ContextCompat.getColor(requireContext(), statusIconColorId)
            binding.responseStatusCodeIcon.setColorFilter(statusIconColor)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.root.requestLayout()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}