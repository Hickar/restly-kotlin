package com.hickar.restly.ui.requests

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hickar.restly.R
import com.hickar.restly.RestlyApplication
import com.hickar.restly.databinding.FragmentRequestDetailBinding
import com.hickar.restly.databinding.FragmentRequestsBinding
import com.hickar.restly.models.Request

class RequestsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private val requestsViewModel: RequestsViewModel by activityViewModels {
        RequestsViewModelFactory(
            (activity?.application as RestlyApplication).database.requestDao()
        )
    }

    private var _binding: FragmentRequestsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)

        _binding = FragmentRequestsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = RequestsAdapter {
            val action = RequestsFragmentDirections.actionNavigationRequestsToRequestDetailFragment()
            Log.d("RequestsFragment", "Navigating to RequestDetailFragment")
            view.findNavController().navigate(action)
        }

        recyclerView = binding.requestsList
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.top_action_menu, menu)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}