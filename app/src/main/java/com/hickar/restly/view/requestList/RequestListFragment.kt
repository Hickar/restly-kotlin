package com.hickar.restly.view.requestList

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hickar.restly.MainActivity
import com.hickar.restly.R
import com.hickar.restly.RestlyApplication
import com.hickar.restly.databinding.RequestListBinding
import com.hickar.restly.utils.SwipeDeleteCallback
import com.hickar.restly.view.requestList.adapters.RequestListAdapter
import com.hickar.restly.viewModel.RequestListViewModel
import com.hickar.restly.viewModel.RequestViewModelFactory
import kotlinx.coroutines.*

class RequestListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private val viewModel: RequestListViewModel by viewModels {
        RequestViewModelFactory(
            (requireActivity().application as RestlyApplication).requestRepository,
            arguments?.getString("collectionId")
        )
    }

    private var _binding: RequestListBinding? = null
    private val binding get() = _binding!!

    @SuppressLint("RestrictedApi")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)

        (requireActivity() as MainActivity).supportActionBar?.title = arguments?.getString("collectionName")

        _binding = RequestListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapters()
        setupDecoration()
        setupObservers()
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (arguments?.getString("collectionId") == null) {
            inflater.inflate(R.menu.request_list_action_menu, menu)
        } else {
            inflater.inflate(R.menu.request_list_collection_action_menu, menu)
        }

        val homeButtonEnabled = arguments?.getString("collectionId") != null
        (requireActivity() as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(homeButtonEnabled)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.request_list_menu_add_button -> {
                runBlocking coroutineScope@{
                    val newRequestId = viewModel.createNewDefaultRequest()
                    val action =
                        RequestListFragmentDirections.actionRequestListFragmentToRequestDetailFragment(
                            newRequestId
                        )
                    findNavController().navigate(action)

                    return@coroutineScope true
                }
            }
            R.id.request_list_collection_menu_edit_button -> {
                val action = RequestListFragmentDirections.actionRequestListFragmentToCollectionEditFragment()
                findNavController().navigate(action)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshRequests()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupAdapters() {
        val adapter = RequestListAdapter {
            val action = RequestListFragmentDirections.actionRequestListFragmentToRequestDetailFragment(it.id)
            this.findNavController().navigate(action)
        }

        recyclerView = binding.requestList
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        val touchHelper = ItemTouchHelper(SwipeDeleteCallback(requireContext()) { position ->
            viewModel.deleteRequest(position)
        })
        touchHelper.attachToRecyclerView(recyclerView)
    }

    private fun setupDecoration() {
        val dividerDecoration = DividerItemDecoration(context, RecyclerView.VERTICAL)
        val dividerDrawable =
            ContextCompat.getDrawable(requireContext(), R.drawable.item_divider)
        dividerDecoration.setDrawable(dividerDrawable!!)
        recyclerView.addItemDecoration(dividerDecoration)
    }

    private fun setupObservers() {
        viewModel.requests.observe(viewLifecycleOwner, { requests ->
            (recyclerView.adapter as RequestListAdapter).submitList(requests)
        })
    }
}