package com.hickar.restly.ui.requests

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hickar.restly.R
import com.hickar.restly.RestlyApplication
import com.hickar.restly.databinding.FragmentRequestDetailBinding
import com.hickar.restly.databinding.FragmentRequestsBinding
import com.hickar.restly.models.Request
import kotlinx.coroutines.*

class RequestsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private val requestsViewModel: RequestsViewModel by activityViewModels {
        RequestsViewModelFactory(
            (activity?.application as RestlyApplication).repository
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
            val action = RequestsFragmentDirections.actionNavigationRequestsToRequestDetailFragment(it.id)
            view.findNavController().navigate(action)
        }

        recyclerView = binding.requestsList
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        requestsViewModel.allRequests.observe(viewLifecycleOwner, { requests ->
            requests?.let { adapter.submitList(it) }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.top_action_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.request_menu_add_button -> {
                runBlocking coroutineScope@{

                    val newRequestId = requestsViewModel.createNewDefaultRequest()
                    val action = RequestsFragmentDirections.actionNavigationRequestsToRequestDetailFragment(newRequestId)
                    findNavController().navigate(action)

                    return@coroutineScope true
                }
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}