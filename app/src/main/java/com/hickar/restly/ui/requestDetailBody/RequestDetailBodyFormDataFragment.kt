package com.hickar.restly.ui.requestDetailBody

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hickar.restly.databinding.RequestDetailBodyFormdataBinding
import com.hickar.restly.models.RequestKeyValue
import com.hickar.restly.ui.requestDetail.ParamsListAdapter
import com.hickar.restly.ui.requestDetail.RequestDetailParamsListAdapter
import com.hickar.restly.ui.requestDetail.RequestDetailViewModel
import com.hickar.restly.utils.SwipeDeleteCallback

class RequestDetailBodyFormDataFragment(private val viewModel: RequestDetailViewModel) : Fragment() {
    private var _binding: RequestDetailBodyFormdataBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = RequestDetailBodyFormdataBinding.inflate(inflater, container, false)
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
        recyclerView.adapter = RequestDetailParamsListAdapter<RequestKeyValue>(
            onParamCheckBoxToggle,
            { text, position ->
                viewModel.formdataParams.value!![position].key = text
            },
            { text, position ->
                viewModel.formdataParams.value!![position].value = text
            }
        )
        val paramsTouchHelper = ItemTouchHelper(SwipeDeleteCallback(requireContext()) { position ->
            viewModel.deleteFormData(position)
        })
        paramsTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun setupObservers() {
        viewModel.formdataParams.observe(viewLifecycleOwner, { params ->
            (recyclerView.adapter as ParamsListAdapter).submitList(params)
        })
    }

    private fun setupEventListeners() {
        binding.requestDetailBodyFormdataAddButton.setOnClickListener {
            viewModel.addFormData()
        }
    }

    private val onParamCheckBoxToggle: (Int) -> Unit = { position ->
        viewModel.toggleFormData(position)
    }
}