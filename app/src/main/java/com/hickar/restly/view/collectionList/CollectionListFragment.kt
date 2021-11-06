package com.hickar.restly.view.collectionList

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.hickar.restly.R
import com.hickar.restly.databinding.CollectionListBinding
import com.hickar.restly.view.collectionList.adapters.CollectionListAdapter

class CollectionListFragment : Fragment() {
//    private val viewModel: CollectionListViewModel by activityViewModels()
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
//        setupEventListeners()
//        setupObservers()
    }

    private fun setupAdapters() {
        val adapter = CollectionListAdapter {
//            val action =
        }

        recyclerView = binding.collectionList
        recyclerView.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.collection_list_action_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.collection_list_menu_add_button -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}