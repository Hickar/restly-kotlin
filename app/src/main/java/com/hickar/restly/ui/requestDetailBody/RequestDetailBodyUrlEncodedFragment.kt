package com.hickar.restly.ui.requestDetailBody

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hickar.restly.databinding.RequestDetailBodyUrlencodedBinding
import com.hickar.restly.models.RequestKeyValueParameter
import com.hickar.restly.ui.requestDetail.ParamsListAdapter
import com.hickar.restly.ui.requestDetail.RequestDetailParamsListAdapter
import com.hickar.restly.ui.requestDetail.RequestDetailViewModel
import com.hickar.restly.utils.SwipeDeleteCallback

class RequestDetailBodyUrlEncodedFragment(private val viewModel: RequestDetailViewModel) : Fragment() {
    private var _binding: RequestDetailBodyUrlencodedBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = RequestDetailBodyUrlencodedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupAdapter()
        setupEventListeners()
        setupObservers()
    }

    private fun setupAdapter() {
        recyclerView = binding.requestDetailBodyUrlencoded
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = RequestDetailParamsListAdapter<RequestKeyValueParameter>(
            onParamCheckBoxToggle,
            { text, position ->
                viewModel.urlencodedParams.value!![position].key = text
            },
            { text, position ->
                viewModel.urlencodedParams.value!![position].value = text
            }
        )
        val paramsTouchHelper = ItemTouchHelper(SwipeDeleteCallback(requireContext()) { position ->
            viewModel.deleteUrlEncoded(position)
        })
        paramsTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun setupObservers() {
        viewModel.urlencodedParams.observe(viewLifecycleOwner, { params ->
            (recyclerView.adapter as ParamsListAdapter).submitList(params)
        })
    }

    private fun setupEventListeners() {
        binding.requestDetailBodyUrlencodedAddButton.setOnClickListener {
            viewModel.addUrlEncoded()
        }
    }

    private val onParamCheckBoxToggle: (Int) -> Unit = { position ->
        viewModel.toggleUrlEncoded(position)
    }
}