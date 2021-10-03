package com.hickar.restly.ui.requestList

import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hickar.restly.R
import com.hickar.restly.RestlyApplication
import com.hickar.restly.databinding.RequestListBinding
import com.hickar.restly.utils.SwipeDeleteCallback
import kotlinx.coroutines.*

class RequestListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private val requestListViewModel: RequestListViewModel by activityViewModels {
        RequestListViewModelFactory(
            (activity?.application as RestlyApplication).repository
        )
    }

    private var _binding: RequestListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)

        _binding = RequestListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setupAdapters()
        setupDecoration()
        setupObservers()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.request_list_action_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.request_list_menu_add_button -> {
                runBlocking coroutineScope@{
                    val newRequestId = requestListViewModel.createNewDefaultRequest()
                    val action =
                        RequestListFragmentDirections.actionNavigationRequestsToRequestDetailFragment(
                            newRequestId
                        )
                    findNavController().navigate(action)

                    return@coroutineScope true
                }
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        requestListViewModel.refreshRequests()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupAdapters() {
        val adapter = RequestListAdapter {
            val action = RequestListFragmentDirections.actionNavigationRequestsToRequestDetailFragment(it.id)
            this.findNavController().navigate(action)
        }

        recyclerView = binding.requestsList
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        val touchHelper = ItemTouchHelper(SwipeDeleteCallback(requireContext()) { position ->
            requestListViewModel.deleteRequest(position)
        })
        touchHelper.attachToRecyclerView(recyclerView)
    }

    private fun setupDecoration() {
        val dividerDecoration = DividerItemDecoration(context, RecyclerView.VERTICAL)
        val dividerDrawable =
            ContextCompat.getDrawable(requireContext(), R.drawable.recycler_divider)
        dividerDecoration.setDrawable(dividerDrawable!!)
        recyclerView.addItemDecoration(dividerDecoration)
    }

    private fun setupObservers() {
        requestListViewModel.requests.observe(viewLifecycleOwner, { requests ->
            (recyclerView.adapter as RequestListAdapter).submitList(requests)
        })
    }
}