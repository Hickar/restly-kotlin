package com.hickar.restly.ui.requestDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.hickar.restly.RestlyApplication
import com.hickar.restly.databinding.FragmentRequestDetailBinding
import com.hickar.restly.models.RequestHeader
import com.hickar.restly.models.RequestQueryParameter
import com.hickar.restly.utils.MethodCardViewUtil

class RequestDetailFragment : Fragment() {

    private var _binding: FragmentRequestDetailBinding? = null
    private val binding get() = _binding!!

    private val requestDetailViewModel: RequestDetailViewModel by viewModels {
        RequestDetailViewModelFactory(
            (activity?.application as RestlyApplication).repository,
            arguments?.get("requestId") as Long
        )
    }

    private lateinit var paramsRecyclerView: RecyclerView
    private lateinit var headersRecyclerView: RecyclerView

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        setHasOptionsMenu(true)

        _binding = FragmentRequestDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        paramsRecyclerView = binding.requestDetailParamsRecyclerView
        headersRecyclerView = binding.requestDetailHeadersRecyclerView

        val params = requestDetailViewModel.params.value
        val headers = requestDetailViewModel.headers.value

        paramsRecyclerView.adapter = RequestDetailParamsListAdapter(params!!.toMutableList())
        headersRecyclerView.adapter = RequestDetailParamsListAdapter(headers!!.toMutableList())

//        tabLayout = binding.requestDetailBodyTabs
//        viewPager = binding.requestDetailBodyView
//
//        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
//            tab.text = "Tab $position"
//        }.attach()

        setupObservers()
    }

    private fun setupObservers() {
        requestDetailViewModel.name.observe(viewLifecycleOwner, { name ->
            binding.requestNameLabel.text = name
        })

        requestDetailViewModel.method.observe(viewLifecycleOwner, { method ->
            val cardBackgroundColorId = MethodCardViewUtil.getBackgroundColorId(method)
            val cardTextColorId = MethodCardViewUtil.getTextColorId(method)

            val cardBackgroundColor =
                ResourcesCompat.getColor(resources, cardBackgroundColorId, null)
            val cardTextColor = ResourcesCompat.getColor(resources, cardTextColorId, null)

            binding.requestMethodLabel.text = MethodCardViewUtil.getShortMethodName(method)
            binding.requestDetailMethodBox.setCardBackgroundColor(cardBackgroundColor)
            binding.requestMethodLabel.setTextColor(cardTextColor)
        })

        requestDetailViewModel.params.observe(viewLifecycleOwner, { params ->
            (paramsRecyclerView.adapter as RequestDetailParamsListAdapter<RequestQueryParameter>).submitList(params)
        })

        requestDetailViewModel.headers.observe(viewLifecycleOwner, { headers ->
            (headersRecyclerView.adapter as RequestDetailParamsListAdapter<RequestHeader>).submitList(headers)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}