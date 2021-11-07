package com.hickar.restly.view.collectionList

import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.hickar.restly.R
import com.hickar.restly.RestlyApplication
import com.hickar.restly.databinding.CollectionListBinding
import com.hickar.restly.utils.SwipeDeleteCallback
import com.hickar.restly.view.collectionList.adapters.CollectionListAdapter
import com.hickar.restly.view.dialogs.ConfirmationDialog
import com.hickar.restly.view.dialogs.ConfirmationDialogDelegate
import com.hickar.restly.viewModel.CollectionListViewModel
import com.hickar.restly.viewModel.CollectionViewModelFactory
import kotlinx.coroutines.runBlocking

class CollectionListFragment : Fragment() {
    private val viewModel: CollectionListViewModel by viewModels {
        CollectionViewModelFactory(requireActivity().application as RestlyApplication)
    }
    private lateinit var recyclerView: RecyclerView

    private lateinit var itemTouchHelper: ItemTouchHelper

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
        setupDecoration()
    }

    private fun setupAdapters() {
        val adapter = CollectionListAdapter {
            val bundle = Bundle()
            bundle.putString("collectionId", it.id)
            bundle.putString("collectionName", it.name)
            findNavController().navigate(R.id.navigate_fromCollectionTab_toRequestList, bundle)
        }

        recyclerView = binding.collectionList
        recyclerView.adapter = adapter

        itemTouchHelper = ItemTouchHelper(SwipeDeleteCallback(requireContext()) { position ->
            val dialog = ConfirmationDialog(
                R.string.dialog_delete_collection_title,
                R.string.dialog_delete_collection_message,
                R.string.dialog_ok_confirm_delete_option,
                { dialog, _ ->
                    dialog.cancel()
// Hack from the official documentation
// https://developer.android.com/reference/android/support/v7/widget/helper/ItemTouchHelper.html#attachToRecyclerView(android.support.v7.widget.RecyclerView)
                    itemTouchHelper.attachToRecyclerView(null)
                    itemTouchHelper.attachToRecyclerView(recyclerView)
                },
                { _, _ -> viewModel.deleteCollection(position) }
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
        val dividerDecoration = DividerItemDecoration(context, RecyclerView.VERTICAL)
        val dividerDrawable =
            ContextCompat.getDrawable(requireContext(), R.drawable.item_divider)
        dividerDecoration.setDrawable(dividerDrawable!!)
        recyclerView.addItemDecoration(dividerDecoration)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.collection_list_action_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.collection_list_menu_add_button -> {
                runBlocking coroutineScope@{
                    val newCollectionId = viewModel.createNewCollection()
                    val bundle = Bundle()

                    bundle.putString("collectionId", newCollectionId)
                    bundle.putString("collectionName", "New Collection")
                    findNavController().navigate(R.id.navigate_fromCollectionTab_toRequestList, bundle)

                    return@coroutineScope true
                }
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}