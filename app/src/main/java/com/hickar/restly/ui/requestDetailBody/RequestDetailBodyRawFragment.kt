package com.hickar.restly.ui.requestDetailBody

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.hickar.restly.databinding.RequestDetailBodyRawBinding
import com.hickar.restly.ui.requestDetail.RequestDetailViewModel

class RequestDetailBodyRawFragment(private val viewModel: RequestDetailViewModel) : Fragment() {
    private var _binding: RequestDetailBodyRawBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = RequestDetailBodyRawBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    }
}