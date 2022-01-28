package com.hickar.restly.view.collectionList

import android.os.Bundle
import android.view.*
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import com.hickar.restly.R
import com.hickar.restly.databinding.CollectionEditBinding
import com.hickar.restly.extensions.toEditable
import com.hickar.restly.viewModel.CollectionViewModel
import com.hickar.restly.viewModel.LambdaFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CollectionEditFragment : Fragment() {
    private var _binding: CollectionEditBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var factory: CollectionViewModel.Factory
    private val viewModel: CollectionViewModel by viewModels {
        LambdaFactory(this) { stateHandle ->
            factory.build(stateHandle)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadCollection(arguments?.getString(COLLECTION_ID_KEY))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)

        _binding = CollectionEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupEventListeners()
        setupObservers()
    }

    private fun setupEventListeners() {
        binding.collectionEditNameInput.doAfterTextChanged { newName ->
            viewModel.setName(newName.toString())
        }

        binding.collectionEditDescriptionInput.doAfterTextChanged { newDescription ->
            viewModel.setDescription(newDescription.toString())
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            lifecycle.whenStarted {
                viewModel.collection.collect {
                    binding.collectionEditNameInput.text = it.name.toEditable()
                    binding.collectionEditDescriptionInput.text = it.description.toEditable()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.collection_edit_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.collection_edit_menu_save_option -> {
                viewModel.saveCollection()
                requireActivity().onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val COLLECTION_ID_KEY = "collectionId"
    }
}