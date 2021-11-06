package com.hickar.restly.view.collectionList

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.hickar.restly.R
import com.hickar.restly.RestlyApplication
import com.hickar.restly.databinding.CollectionListBinding
import com.hickar.restly.view.collectionList.adapters.CollectionListAdapter
import com.hickar.restly.viewModel.CollectionListViewModel
import com.hickar.restly.viewModel.CollectionViewModelFactory
import kotlinx.coroutines.runBlocking

class CollectionListFragment : Fragment() {
    private val viewModel: CollectionListViewModel by viewModels {
        CollectionViewModelFactory((requireActivity().application as RestlyApplication).collectionRepository)
    }
    private lateinit var recyclerView: RecyclerView

    private var _binding: CollectionListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)

        _binding = CollectionListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapters()
        setupObservers()
    }

    private fun setupAdapters() {
        val adapter = CollectionListAdapter {
//            val action = CollectionListFragmentDirections.actionNavigationCollectionsToNavigationRequests(it.id)
//            findNavController().navigate(action)
        }

        recyclerView = binding.collectionList
        recyclerView.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.collections.observe(viewLifecycleOwner) { collections ->
            (recyclerView.adapter as CollectionListAdapter).submitList(collections)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.collection_list_action_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.collection_list_menu_add_button -> {
                runBlocking coroutineScope@{
//                    val newCollectionId = viewModel.createNewCollection()
//                    val action =
//                        CollectionListFragmentDirections.actionNavigationCollectionsToNavigationRequests(newCollectionId)
//
//                    findNavController().navigate(action)

                    return@coroutineScope true
                }
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}