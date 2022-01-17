package com.hickar.restly.view.requestGroup

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.hickar.restly.R
import com.hickar.restly.databinding.RequestGroupEditBinding
import com.hickar.restly.extensions.observeOnce
import com.hickar.restly.extensions.toEditable
import com.hickar.restly.viewModel.LambdaFactory
import com.hickar.restly.viewModel.RequestGroupViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RequestGroupEditFragment : Fragment() {
    private var _binding: RequestGroupEditBinding? = null
    private val binding get() = _binding!!

    @Inject lateinit var factory: RequestGroupViewModel.Factory
    private val viewModel: RequestGroupViewModel by viewModels {
        LambdaFactory(this) { stateHandle ->
            factory.build(stateHandle)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadRequestGroup(arguments?.getString(GROUP_ID_KEY))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)

        _binding = RequestGroupEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupEventListeners()
        setupObservers()
    }

    private fun setupEventListeners() {
        binding.requestGroupEditNameInput.doAfterTextChanged { newName ->
            viewModel.setName(newName.toString())
        }

        binding.requestGroupEditDescriptionInput.doAfterTextChanged { newDescription ->
            viewModel.setDescription(newDescription.toString())
        }
    }

    private fun setupObservers() {
        viewModel.name.observeOnce(viewLifecycleOwner) { name ->
            (requireActivity() as AppCompatActivity).supportActionBar?.title = name
            binding.requestGroupEditNameInput.text = name.toEditable()
        }

        viewModel.description.observeOnce(viewLifecycleOwner) { description ->
            binding.requestGroupEditDescriptionInput.text = description?.toEditable() ?: "".toEditable()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.collection_edit_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.collection_edit_menu_save_option -> {
                viewModel.saveRequestGroup()
                requireActivity().onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val GROUP_ID_KEY = "groupId"
    }
}