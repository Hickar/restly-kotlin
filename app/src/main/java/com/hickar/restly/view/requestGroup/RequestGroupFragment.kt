package com.hickar.restly.view.requestGroup

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.appcompat.view.SupportMenuInflater
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hickar.restly.MainActivity
import com.hickar.restly.R
import com.hickar.restly.databinding.RequestGroupBinding
import com.hickar.restly.extensions.reattachToRecyclerView
import com.hickar.restly.extensions.show
import com.hickar.restly.models.Collection
import com.hickar.restly.models.RequestDirectory
import com.hickar.restly.utils.RecyclerViewDecoration
import com.hickar.restly.utils.SwipeDeleteCallback
import com.hickar.restly.view.dialogs.ConfirmationDialog
import com.hickar.restly.view.requestGroup.adapters.FolderListAdapter
import com.hickar.restly.view.requestGroup.adapters.RequestListAdapter
import com.hickar.restly.viewModel.CollectionViewModel
import com.hickar.restly.viewModel.LambdaFactory
import com.hickar.restly.viewModel.RequestGroupViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RequestGroupFragment : Fragment() {

    private lateinit var requestsRecyclerView: RecyclerView
    private lateinit var foldersRecyclerView: RecyclerView

    private lateinit var foldersItemTouchHelper: ItemTouchHelper

    @Inject
    lateinit var collectionFactory: CollectionViewModel.Factory
    private val collectionViewModel: CollectionViewModel by viewModels {
        LambdaFactory(this) { stateHandle ->
            collectionFactory.build(stateHandle)
        }
    }

    @Inject
    lateinit var requestGroupFactory: RequestGroupViewModel.Factory
    private val requestGroupViewModel: RequestGroupViewModel by viewModels {
        LambdaFactory(this) { stateHandle ->
            requestGroupFactory.build(stateHandle)
        }
    }

    private var _binding: RequestGroupBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        collectionViewModel.loadCollection(arguments?.getString(COLLECTION_ID_KEY))
        requestGroupViewModel.loadRequestGroup(arguments?.getString(GROUP_ID_KEY))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        _binding = RequestGroupBinding.inflate(inflater, container, false)
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

        val menuId: Int
        val backButtonEnabled: Boolean

        val collection = collectionViewModel.collection.value
        val group = requestGroupViewModel.group.value

        if (collection.isDefault() && group.isRoot()) {
            menuId = R.menu.request_group_default_collection_menu
            backButtonEnabled = false
        } else {
            menuId = R.menu.request_group_custom_collection_menu
            backButtonEnabled = true
        }

        val inflater = SupportMenuInflater(requireContext())
        inflater.inflate(menuId, menu)
        (requireActivity() as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(
            backButtonEnabled
        )
    }

    private fun setupAdapters() {
        requestsRecyclerView = binding.requestGroupRequests
        requestsRecyclerView.layoutManager = LinearLayoutManager(context)
        requestsRecyclerView.adapter = RequestListAdapter { navigateToRequest(it.id) }

        ItemTouchHelper(SwipeDeleteCallback(requireContext()) { position ->
            requestGroupViewModel.deleteRequest(position)
        }).attachToRecyclerView(requestsRecyclerView)

        foldersRecyclerView = binding.requestGroupFolders
        foldersRecyclerView.layoutManager = LinearLayoutManager(context)
        foldersRecyclerView.adapter = FolderListAdapter {
            val collectionId = this@RequestGroupFragment.collectionViewModel.collection.value.id
            navigateToFolder(collectionId, it.id)
        }

        foldersItemTouchHelper = ItemTouchHelper(SwipeDeleteCallback(requireContext()) { position ->
            val dialog = ConfirmationDialog(
                titleId = R.string.dialog_delete_group_title,
                messageId = R.string.dialog_delete_group_message,
                confirmButtonTextId = R.string.dialog_ok_confirm_delete_option,
                onCancelCallback = { dialog, _ ->
                    dialog.cancel()
                    foldersItemTouchHelper.reattachToRecyclerView(foldersRecyclerView)
                },
                onConfirmCallback = { _, _ ->
                    requestGroupViewModel.deleteFolder(position)
                }
            )

            dialog.show(parentFragmentManager, "Confirmation")
        })

        foldersItemTouchHelper.attachToRecyclerView(foldersRecyclerView)
    }

    private fun setupDecoration() {
        val decoration = RecyclerViewDecoration.vertical(requireContext(), R.drawable.item_divider)
        requestsRecyclerView.addItemDecoration(decoration)
        foldersRecyclerView.addItemDecoration(decoration)
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            lifecycle.whenStarted {
                val requestGroupFlow = requestGroupViewModel.group
                val collectionFlow = collectionViewModel.collection

                combine(requestGroupFlow, collectionFlow) { requestGroup, collection ->
                    Pair(requestGroup, collection)
                }.collect {
                    setTitle(it.first, it.second)
                }
            }
        }
    }

    private fun setTitle(requestGroup: RequestDirectory, collection: Collection) {
        if (requestGroup.requests.size > 0) binding.requestGroupRequests.show()
        (requestsRecyclerView.adapter as RequestListAdapter).submitList(requestGroup.requests)

        if (requestGroup.subgroups.size > 0) binding.requestGroupFolders.show()
        (foldersRecyclerView.adapter as FolderListAdapter).submitList(requestGroup.subgroups)

        requireActivity().invalidateOptionsMenu()

        if (requestGroup.isRoot()) {
            (requireActivity() as MainActivity).supportActionBar?.title = when {
                collection.isDefault() -> getString(R.string.default_collection_title)
                else -> collection.name
            }
        } else {
            (requireActivity() as MainActivity).supportActionBar?.title = requestGroup.name
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        updateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        updateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.request_group_menu_add_button -> {
                lifecycleScope.launch {
                    val newRequestId = requestGroupViewModel.createNewDefaultRequest()
                    navigateToRequest(newRequestId)
                }
                true
            }
            R.id.request_group_collection_menu_edit_button -> {
                if (requestGroupViewModel.group.value.isRoot()) {
                    navigateToCollectionEdit(collectionViewModel.collection.value.id)
                } else {
                    navigateToRequestGroupEdit(requestGroupViewModel.group.value.id)
                }
                true
            }
            R.id.request_group_add_folder_button -> {
                lifecycleScope.launch {
                    val collectionId = collectionViewModel.collection.value.id
                    val newGroupId = requestGroupViewModel.createNewGroup()
                    navigateToFolder(collectionId, newGroupId)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun navigateToRequest(id: String) {
        val action = RequestGroupFragmentDirections.actionRequestGroupFragmentToRequestFragment(id)
        findNavController().navigate(action)
    }

    private fun navigateToFolder(collectionId: String, groupId: String) {
        val action = RequestGroupFragmentDirections.actionRequestGroupFragmentSelf(collectionId, groupId)
        findNavController().navigate(action)
    }

    private fun navigateToCollectionEdit(collectionId: String) {
        val action = RequestGroupFragmentDirections.actionRequestGroupFragmentToCollectionEditFragment(
            collectionId
        )
        findNavController().navigate(action)
    }

    private fun navigateToRequestGroupEdit(groupId: String) {
        val action = RequestGroupFragmentDirections.actionRequestGroupFragmentToRequestGroupEditFragment(
            groupId
        )
        findNavController().navigate(action)
    }

//    override fun onResume() {
//        super.onResume()
//        requestGroupViewModel.refreshCurrentRequestGroup()
//        collectionViewModel.refreshCurrentCollection()
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val COLLECTION_ID_KEY = "collectionId"
        const val GROUP_ID_KEY = "groupId"
    }
}