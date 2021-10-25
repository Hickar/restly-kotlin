package com.hickar.restly.view.responseBody

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hickar.restly.databinding.ResponseBodyInfoBinding
import com.hickar.restly.view.responseBody.adapters.ResponseInfoListAdapter
import com.hickar.restly.viewModel.RequestViewModel

class ResponseBodyInfoFragment : Fragment() {
    private var _binding: ResponseBodyInfoBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RequestViewModel by activityViewModels()

    private lateinit var generalParamsRecyclerView: RecyclerView
    private lateinit var headersRecyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = ResponseBodyInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapters()
        setupObservers()
    }

    private fun setupAdapters() {
        generalParamsRecyclerView = binding.responseInfoGeneralList
        generalParamsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        generalParamsRecyclerView.adapter = ResponseInfoListAdapter()

        headersRecyclerView = binding.responseInfoHeadersList
        headersRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        headersRecyclerView.adapter = ResponseInfoListAdapter()
    }

    private fun setupObservers() {
        viewModel.response.observe(viewLifecycleOwner) { response ->
            (generalParamsRecyclerView.adapter as ResponseInfoListAdapter).submitList(response.getGeneralParams())
            (headersRecyclerView.adapter as ResponseInfoListAdapter).submitList(response.getHeaders())
        }
    }
}