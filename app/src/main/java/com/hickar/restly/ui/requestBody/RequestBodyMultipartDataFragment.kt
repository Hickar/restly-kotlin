package com.hickar.restly.ui.requestBody

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hickar.restly.databinding.RequestBodyMultipartBinding
import com.hickar.restly.models.RequestMultipartData
import com.hickar.restly.ui.request.RequestParamsListAdapter
import com.hickar.restly.ui.request.RequestViewModel
import com.hickar.restly.utils.SwipeDeleteCallback

typealias MultipartListAdapter = RequestParamsListAdapter<RequestMultipartData>

class RequestDetailBodyFormDataFragment(private val viewModel: RequestViewModel) : Fragment() {
    private var _binding: RequestBodyMultipartBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = RequestBodyMultipartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupAdapter()
        setupEventListeners()
        setupObservers()
    }

    private fun setupAdapter() {
        recyclerView = binding.requestDetailBodyFormdata
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = RequestParamsListAdapter<RequestMultipartData>(
            onParamCheckBoxToggle,
            { text, position ->
                viewModel.multipartData.value!![position].key = text
            },
            { text, position ->
                viewModel.multipartData.value!![position].value = text
            }
        )
        val paramsTouchHelper = ItemTouchHelper(SwipeDeleteCallback(requireContext()) { position ->
            viewModel.deleteMultipartData(position)
        })
        paramsTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun setupObservers() {
        viewModel.multipartData.observe(viewLifecycleOwner, { params ->
            (recyclerView.adapter as MultipartListAdapter).submitList(params)
        })
    }

    private fun setupEventListeners() {
        binding.requestDetailBodyFormdataAddButton.setOnClickListener {
            viewModel.addMultipartData()
        }
    }

    private val onParamCheckBoxToggle: (Int) -> Unit = { position ->
        viewModel.toggleMultipartData(position)
    }
}