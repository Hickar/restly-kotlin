package com.hickar.restly.view.requestList

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.view.SupportMenuInflater
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
import com.hickar.restly.viewModel.CollectionViewModel
import com.hickar.restly.viewModel.CollectionViewModelFactory
import com.hickar.restly.viewModel.RequestListViewModel
import com.hickar.restly.viewModel.RequestViewModelFactory
import kotlinx.coroutines.*

class RequestListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    private val collectionViewModel: CollectionViewModel by viewModels {
        CollectionViewModelFactory(
            (requireActivity().application as RestlyApplication)
        )
    }
    private val requestListViewModel: RequestListViewModel by viewModels {
        RequestViewModelFactory(
            (requireActivity().application as RestlyApplication).requestRepository,
            arguments?.getString("collectionId")
        )
    }

    private var _binding: RequestListBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        collectionViewModel.loadCollection(arguments?.getString("collectionId"))
    }

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

    @SuppressLint("RestrictedApi")
    private fun updateOptionsMenu(menu: Menu) {
        menu.clear()

        var menuId: Int
        var backButtonEnabled: Boolean
        if (collectionViewModel.collection.isDefault()) {
            menuId = R.menu.request_list_default_collection_menu
            backButtonEnabled = false
        } else {
            menuId = R.menu.request_list_custom_collection_menu
            backButtonEnabled = true
        }

        val inflater = SupportMenuInflater(requireContext())
        inflater.inflate(menuId, menu)
        (requireActivity() as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(backButtonEnabled)
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
            requestListViewModel.deleteRequest(position)
        })
        touchHelper.attachToRecyclerView(recyclerView)
    }

    private fun setupDecoration() {
        val dividerDecoration = DividerItemDecoration(context, RecyclerView.VERTICAL)
        val dividerDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.item_divider)
        dividerDecoration.setDrawable(dividerDrawable!!)
        recyclerView.addItemDecoration(dividerDecoration)
    }

    private fun setupObservers() {
        requestListViewModel.requests.observe(viewLifecycleOwner, { requests ->
            (recyclerView.adapter as RequestListAdapter).submitList(requests)
        })

        collectionViewModel.name.observe(viewLifecycleOwner) { name ->
            requireActivity().invalidateOptionsMenu()
            (requireActivity() as MainActivity).supportActionBar?.title =
                if (collectionViewModel.collection.isDefault()) {
                    "Requests"
                } else {
                    name
                }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        updateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        updateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d("RequestList.onOptionItemSelected", "Collection Name: ${collectionViewModel.collection.name}")
        Log.d("RequestList.collectionViewModel", collectionViewModel.toString())
        return when (item.itemId) {
            R.id.request_list_menu_add_button -> {
                runBlocking coroutineScope@{
                    val newRequestId = requestListViewModel.createNewDefaultRequest()
                    val action =
                        RequestListFragmentDirections.actionRequestListFragmentToRequestDetailFragment(
                            newRequestId
                        )
                    findNavController().navigate(action)

                    return@coroutineScope true
                }
            }
            R.id.request_list_collection_menu_edit_button -> {
                val action = RequestListFragmentDirections.actionRequestListFragmentToCollectionEditFragment(
                    collectionViewModel.collection.id
                )
                findNavController().navigate(action)
                true
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
}