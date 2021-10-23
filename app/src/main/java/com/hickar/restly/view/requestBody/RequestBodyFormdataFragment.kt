package com.hickar.restly.view.requestBody

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hickar.restly.databinding.RequestBodyFormdataBinding
import com.hickar.restly.models.RequestFormData
import com.hickar.restly.models.RequestKeyValueData
import com.hickar.restly.utils.SwipeDeleteCallback
import com.hickar.restly.view.request.adapters.RequestParamsListAdapter
import com.hickar.restly.viewModel.RequestViewModel

typealias FormDataListAdapter = RequestParamsListAdapter<RequestFormData>

class RequestBodyFormdataFragment : Fragment() {
    private var _binding: RequestBodyFormdataBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RequestViewModel by activityViewModels()
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = RequestBodyFormdataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupAdapter()
        setupEventListeners()
        setupObservers()
    }

    private fun setupAdapter() {
        recyclerView = binding.requestBodyFormdataList
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = RequestParamsListAdapter<RequestKeyValueData>(
            onParamCheckBoxToggle,
            { text, position ->
                viewModel.formData.value!![position].key = text
            },
            { text, position ->
                viewModel.formData.value!![position].valueText = text
            }
        )
        val paramsTouchHelper = ItemTouchHelper(SwipeDeleteCallback(requireContext()) { position ->
            viewModel.deleteFormData(position)
        })
        paramsTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun setupObservers() {
        viewModel.formData.observe(viewLifecycleOwner, { params ->
            (recyclerView.adapter as FormDataListAdapter).submitList(params)
        })
    }

    private fun setupEventListeners() {
        binding.requestBodyFormdataAddButton.setOnClickListener {
            viewModel.addFormData()
        }
    }

    private val onParamCheckBoxToggle: (Int) -> Unit = { position ->
        viewModel.toggleFormData(position)
    }

    override fun onResume() {
        super.onResume()
        binding.root.requestLayout()
    }
}