package com.hickar.restly.view.collectionList

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.hickar.restly.R
import com.hickar.restly.databinding.CollectionListBinding
import com.hickar.restly.extensions.reattachToRecyclerView
import com.hickar.restly.utils.RecyclerViewDecoration
import com.hickar.restly.utils.SwipeDeleteCallback
import com.hickar.restly.view.collectionList.adapters.CollectionListAdapter
import com.hickar.restly.view.dialogs.ConfirmationDialog
import com.hickar.restly.viewModel.CollectionListViewModel
import com.hickar.restly.viewModel.LambdaFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class CollectionListFragment : Fragment() {
    @Inject
    lateinit var factory: CollectionListViewModel.Factory
    private val viewModel: CollectionListViewModel by activityViewModels {
        LambdaFactory(this) { stateHandle ->
            factory.build(stateHandle)
        }
    }

    private lateinit var recyclerView: RecyclerView

    private lateinit var itemTouchHelper: ItemTouchHelper

    private var _binding: CollectionListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)

        _binding = CollectionListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapters()
        setupObservers()
        setupDecoration()
    }

    private fun setupAdapters() {
        recyclerView = binding.collectionList
        recyclerView.adapter = CollectionListAdapter { navigateToCollection(it.id, it.id) }

        itemTouchHelper = ItemTouchHelper(SwipeDeleteCallback(requireContext()) { position ->
            val dialog = ConfirmationDialog(
                R.string.dialog_delete_collection_title,
                R.string.dialog_delete_collection_message,
                R.string.dialog_ok_confirm_delete_option,
                { dialog, _ ->
                    dialog.cancel()
                    itemTouchHelper.reattachToRecyclerView(recyclerView)
                },
                { _, _ ->
                    viewModel.deleteCollection(position)
                }
            )

            dialog.show(parentFragmentManager, "Confirmation")
        })
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun setupObservers() {
        viewModel.collections.observe(viewLifecycleOwner) { collections ->
            (recyclerView.adapter as CollectionListAdapter).submitList(collections)
        }
    }

    private fun setupDecoration() {
        val decoration = RecyclerViewDecoration.vertical(requireContext(), R.drawable.item_divider)
        recyclerView.addItemDecoration(decoration)
    }

    private fun navigateToCollection(collectionId: String, groupId: String) {
        val action = CollectionListFragmentDirections.navigateFromCollectionTabToRequestGroup(
            collectionId,
            groupId
        )
        findNavController().navigate(action)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.collection_list_action_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.collection_list_menu_add_button -> {
                runBlocking coroutineScope@{
                    val newCollectionId = viewModel.createNewCollection()
                    navigateToCollection(newCollectionId, newCollectionId)

                    return@coroutineScope true
                }
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}