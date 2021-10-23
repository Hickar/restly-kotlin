package com.hickar.restly.view.requestBody

import android.content.ContentResolver
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.hickar.restly.databinding.RequestBodyBinaryBinding
import com.hickar.restly.viewModel.RequestViewModel

class RequestBodyBinaryFragment : Fragment() {
    private var _binding: RequestBodyBinaryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RequestViewModel by activityViewModels()
    private lateinit var contentResolver: ContentResolver

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = RequestBodyBinaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupEventListeners()
        setupObservers()
    }

    private fun setupEventListeners() {
        contentResolver = requireContext().contentResolver

        binding.requestBodyBinaryAddButton.setOnClickListener {
            requireActivity().activityResultRegistry.register("key", ActivityResultContracts.OpenDocument()) { uri ->
                if (uri == null) return@register

                contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                viewModel.setBinaryBody(uri)
            }.launch(arrayOf("audio/*", "image/*", "video/*", "text/*", "application/*"))
        }
    }

    private fun setupObservers() {
        viewModel.binaryData.observe(viewLifecycleOwner) { binaryData ->
            binding.requestBodyBinaryLabel.text = if (binaryData.file == null) {
                "Select"
            } else {
                binaryData.file!!.name
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        binding.root.requestLayout()
    }
}