package com.example.restly.ui.requests

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.restly.databinding.FragmentRequestsBinding
import com.example.restly.models.Request

class RequestsFragment : Fragment() {

    private lateinit var requestsViewModel: RequestsViewModel
    private var _binding: FragmentRequestsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requestsViewModel =
            ViewModelProvider(this).get(RequestsViewModel::class.java)

        _binding = FragmentRequestsBinding.inflate(inflater, container, false)

        val requests = listOf<Request>(
            Request("GET", "Google", "https://google.com"),
            Request("POST", "ASS", "fisting://anal.wee.wee")
        )

        val recyclerView = binding.requestsList
        recyclerView.adapter = RequestsAdapter(context, requests)
        recyclerView.layoutManager = LinearLayoutManager(context)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}