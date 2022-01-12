package com.hickar.restly.view.requestGroup

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.appcompat.view.SupportMenuInflater
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hickar.restly.MainActivity
import com.hickar.restly.R
import com.hickar.restly.databinding.RequestGroupBinding
import com.hickar.restly.extensions.show
import com.hickar.restly.utils.SwipeDeleteCallback
import com.hickar.restly.view.requestGroup.adapters.FolderListAdapter
import com.hickar.restly.view.requestGroup.adapters.RequestListAdapter
import com.hickar.restly.viewModel.CollectionViewModel
import com.hickar.restly.viewModel.LambdaFactory
import com.hickar.restly.viewModel.RequestGroupViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RequestGroupFragment : Fragment() {

    private lateinit var requestsRecyclerView: RecyclerView
    private lateinit var foldersRecyclerView: RecyclerView

    @Inject
    lateinit var collectionFactory: CollectionViewModel.Factory
    private val requestGroupViewModel: RequestGroupViewModel by viewModels {
        LambdaFactory(this) { stateHandle ->
            requestGroupFactory.build(stateHandle)
        }
    }

    @Inject
    lateinit var requestGroupFactory: RequestGroupViewModel.Factory
    private val collectionViewModel: CollectionViewModel by viewModels {
        LambdaFactory(this) { stateHandle ->
            collectionFactory.build(stateHandle)
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

        val collection = collectionViewModel.collection
        val group = requestGroupViewModel.group.value

        if (collection.isDefault() && collection.parentId == null && collection.id == group?.id) {
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
        val requestsAdapter = RequestListAdapter { navigateToRequest(it.id) }
        requestsRecyclerView = binding.requestGroupRequests
        requestsRecyclerView.layoutManager = LinearLayoutManager(context)
        requestsRecyclerView.adapter = requestsAdapter

        ItemTouchHelper(SwipeDeleteCallback(requireContext()) { position ->
            requestGroupViewModel.deleteRequest(position)
        }).attachToRecyclerView(requestsRecyclerView)

        val foldersAdapter = FolderListAdapter { navigateToFolder(it.id) }
        foldersRecyclerView = binding.requestGroupFolders
        foldersRecyclerView.layoutManager = LinearLayoutManager(context)
        foldersRecyclerView.adapter = foldersAdapter

        ItemTouchHelper(SwipeDeleteCallback(requireContext()) { position ->
            requestGroupViewModel.deleteRequest(position)
        }).attachToRecyclerView(requestsRecyclerView)
    }

    private fun navigateToRequest(id: String) {
        val action = RequestGroupFragmentDirections.actionRequestGroupFragmentToRequestFragment(id)
        this.findNavController().navigate(action)
    }

    private fun navigateToFolder(groupId: String) {
        val collectionId = this@RequestGroupFragment.collectionViewModel.collection.id

        val action = RequestGroupFragmentDirections.actionRequestGroupFragmentSelf(collectionId, groupId)
        this.findNavController().navigate(action)
    }

    private fun setupDecoration() {
        val dividerDecoration = DividerItemDecoration(context, RecyclerView.VERTICAL)
        val dividerDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.item_divider)
        dividerDecoration.setDrawable(dividerDrawable!!)

        requestsRecyclerView.addItemDecoration(dividerDecoration)
        foldersRecyclerView.addItemDecoration(dividerDecoration)
    }

    private fun setupObservers() {
        requestGroupViewModel.requests.observe(viewLifecycleOwner, { requests ->
            if (requests.size > 0) binding.requestGroupRequests.show()
            (requestsRecyclerView.adapter as RequestListAdapter).submitList(requests)
        })

        requestGroupViewModel.folders.observe(viewLifecycleOwner, { folders ->
            if (folders.size > 0) binding.requestGroupFolders.show()
            (foldersRecyclerView.adapter as FolderListAdapter).submitList(folders)
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
        return when (item.itemId) {
            R.id.request_group_menu_add_button -> {
                lifecycleScope.launch {
                    val newRequestId = requestGroupViewModel.createNewDefaultRequest()
                    val action =
                        RequestGroupFragmentDirections.actionRequestGroupFragmentToRequestFragment(
                            newRequestId
                        )
                    findNavController().navigate(action)
                }
                true
            }
            R.id.request_group_collection_menu_edit_button -> {
                val action =
                    RequestGroupFragmentDirections.actionRequestGroupFragmentToCollectionEditFragment(
                        collectionViewModel.collection.id
                    )
                findNavController().navigate(action)
                true
            }
            R.id.request_group_add_folder_button -> {
                lifecycleScope.launch {
                    val collectionId = collectionViewModel.collection.id
                    val newGroupId = requestGroupViewModel.createNewGroup()
                    val action = RequestGroupFragmentDirections.actionRequestGroupFragmentSelf(
                        collectionId, newGroupId
                    )
                    findNavController().navigate(action)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        requestGroupViewModel.refreshRequestGroup()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val COLLECTION_ID_KEY = "collectionId"
        const val GROUP_ID_KEY = "groupId"
        const val COLLECTION_NAME_KEY = "collectionName"
    }
}