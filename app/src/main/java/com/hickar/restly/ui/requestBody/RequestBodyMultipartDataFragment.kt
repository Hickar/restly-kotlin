package com.hickar.restly.ui.requestBody

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hickar.restly.R
import com.hickar.restly.databinding.RequestBodyMultipartBinding
import com.hickar.restly.models.RequestBinaryData
import com.hickar.restly.ui.request.RequestViewModel
import com.hickar.restly.utils.SwipeDeleteCallback

class RequestDetailBodyFormDataFragment() : Fragment() {
    private var _binding: RequestBodyMultipartBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RequestViewModel by viewModels({ requireParentFragment() })
    private lateinit var contentResolver: ContentResolver

    private lateinit var recyclerView: RecyclerView
    private lateinit var popupMenu: PopupMenu

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = RequestBodyMultipartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        contentResolver = requireContext().contentResolver

        setupAdapter()
        setupPopupMenu()
        setupObservers()
        setupEventListeners()
    }


    private fun setupAdapter() {
        recyclerView = binding.requestBodyMultipart
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = RequestMultipartDataItemsAdapter(
            onParamCheckBoxToggle,
            { text, position ->
                viewModel.multipartData.value!![position].key = text
            },
            { text, position ->
                viewModel.multipartData.value!![position].value = text
            },
            { position ->
                requireActivity().activityResultRegistry.register("key", ActivityResultContracts.OpenDocument()) { uri ->
                    if (uri == null) return@register

                    contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    getFileMetadata(uri)?.let { fileMeta -> viewModel.setMultipartFileBody(position, fileMeta.uri) }
                }.launch(arrayOf("audio/*", "image/*", "video/*", "text/*", "application/*"))
            }
        )
        val paramsTouchHelper = ItemTouchHelper(SwipeDeleteCallback(requireContext()) { position ->
            viewModel.deleteMultipartData(position)
        })
        paramsTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun setupObservers() {
        viewModel.multipartData.observe(viewLifecycleOwner, { params ->
            (recyclerView.adapter as RequestMultipartDataItemsAdapter).submitList(params)
        })
    }

    private fun setupEventListeners() {
        binding.requestBodyMultipartAddButton.setOnClickListener {
            popupMenu.show()
        }

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.request_multipart_type_menu_option_text -> {
                    viewModel.addMultipartData("text")
                    true
                }
                R.id.request_multipart_type_menu_option_file -> {
                    viewModel.addMultipartData("file")
                    true
                }
                else -> throw IllegalArgumentException("Invalid menu item id was provided: ${item.itemId}")
            }
        }
    }

    private fun setupPopupMenu() {
        popupMenu = PopupMenu(requireContext(), binding.requestBodyMultipartAddButton)
        popupMenu.inflate(R.menu.request_multipart_type_menu)
    }

    private val onParamCheckBoxToggle: (Int) -> Unit = { position ->
        viewModel.toggleMultipartData(position)
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
}