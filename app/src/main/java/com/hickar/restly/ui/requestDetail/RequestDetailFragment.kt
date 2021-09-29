package com.hickar.restly.ui.requestDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.hickar.restly.RestlyApplication
import com.hickar.restly.databinding.FragmentRequestDetailBinding
import com.hickar.restly.utils.MethodCardViewUtil

class RequestDetailFragment : Fragment() {

    private var _binding: FragmentRequestDetailBinding? = null
    private val binding get() = _binding!!

    private val requestDetailViewModel: RequestDetailViewModel by activityViewModels {
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

        requestDetailViewModel.currentRequest.value.let {
            paramsRecyclerView.adapter = RequestDetailParamsListAdapter(it!!.queryParams)
            headersRecyclerView.adapter = RequestDetailParamsListAdapter(it.headers)
        }

//        tabLayout = binding.requestDetailBodyTabs
//        viewPager = binding.requestDetailBodyView
//
//        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
//            tab.text = "Tab $position"
//        }.attach()

        requestDetailViewModel.currentRequest.observe(viewLifecycleOwner, { request ->
            val cardBackgroundColorId = MethodCardViewUtil.getBackgroundColorId(request.method)
            val cardTextColorId = MethodCardViewUtil.getTextColorId(request.method)

            val cardBackgroundColor =
                ResourcesCompat.getColor(resources, cardBackgroundColorId, null)
            val cardTextColor = ResourcesCompat.getColor(resources, cardTextColorId, null)

            binding.requestNameLabel.text = request.name
            binding.requestMethodLabel.text = MethodCardViewUtil.getShortMethodName(request.method)
            binding.requestDetailMethodBox.setCardBackgroundColor(cardBackgroundColor)
            binding.requestMethodLabel.setTextColor(cardTextColor)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}