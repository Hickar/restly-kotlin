package com.hickar.restly.ui.requestBody

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.hickar.restly.databinding.RequestBodyBinaryBinding
import com.hickar.restly.models.RequestBinaryData
import com.hickar.restly.ui.request.RequestViewModel

class RequestBodyBinaryFragment(private val viewModel: RequestViewModel) : Fragment() {
    private var _binding: RequestBodyBinaryBinding? = null
    private val binding get() = _binding!!

    private lateinit var contentResolver: ContentResolver

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = RequestBodyBinaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupEventListeners()
        setupObservers()
    }

    private fun getFileMetadata(uri: Uri): RequestBinaryData? {
        val cursor = contentResolver.query(uri, null, null, null, null)

        cursor?.use {
            if (it.moveToFirst()) {
                val name = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))

                val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
                val size = if (!it.isNull(sizeIndex)) {
                    it.getString(sizeIndex)
                } else {
                    "Unknown"
                }

                return RequestBinaryData(name, size, uri.toString())
            }
        }

        return null
    }

    private fun setupEventListeners() {
        contentResolver = requireContext().contentResolver

        binding.requestBodyBinaryAddButton.setOnClickListener {
            requireActivity().activityResultRegistry.register("key", ActivityResultContracts.OpenDocument()) { uri ->
                if (uri == null) return@register

                contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                getFileMetadata(uri)?.let { fileMeta -> viewModel.setBinaryBody(fileMeta) }
            }.launch(arrayOf("audio/*", "image/*", "video/*", "text/*", "application/*"))
        }
    }

    private fun setupObservers() {
        viewModel.binaryData.observe(viewLifecycleOwner) { binaryData ->
            binding.requestBodyBinaryLabel.text = if (binaryData.name == "") {
                "Select"
            } else {
                binaryData.name
            }
        }
    }
}