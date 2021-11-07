package com.hickar.restly.view.collectionList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hickar.restly.databinding.CollectionListEditBinding
import com.hickar.restly.viewModel.CollectionListViewModel

class CollectionEditFragment : Fragment() {
    private var _binding: CollectionListEditBinding? = null
    private val binding get() = _binding!!

//    private val viewModel: CollectionListViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)

        _binding = CollectionListEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        setupEventListeners()
//        setupObservers()
    }
}