package com.hickar.restly.view.collectionList

import android.os.Bundle
import android.view.*
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.hickar.restly.R
import com.hickar.restly.databinding.CollectionListEditBinding
import com.hickar.restly.extensions.observeOnce
import com.hickar.restly.extensions.toEditable
import com.hickar.restly.viewModel.CollectionViewModel

class CollectionEditFragment : Fragment() {
    private var _binding: CollectionListEditBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CollectionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadCollection(arguments?.getString("collectionId"))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)

        _binding = CollectionListEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupEventListeners()
        setupObservers()
    }

    private fun setupEventListeners() {
        binding.collectionListEditNameInput.doAfterTextChanged { newName ->
            viewModel.setName(newName.toString())
        }

        binding.collectionListEditDescriptionInput.doAfterTextChanged { newDescription ->
            viewModel.setDescription(newDescription.toString())
        }
    }

    private fun setupObservers() {
        viewModel.name.observeOnce(viewLifecycleOwner) { name ->
            binding.collectionListEditNameInput.text = name.toEditable()
        }

        viewModel.description.observeOnce(viewLifecycleOwner) { description ->
            binding.collectionListEditDescriptionInput.text = description.toEditable()
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
}