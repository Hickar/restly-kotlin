package com.hickar.restly.ui.requestBody

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.hickar.restly.R
import com.hickar.restly.consts.MimeTypes
import com.hickar.restly.databinding.RequestBodyRawBinding
import com.hickar.restly.ui.request.RequestFragmentDirections
import com.hickar.restly.ui.request.RequestViewModel

class RequestBodyRawFragment : Fragment() {
    private var _binding: RequestBodyRawBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RequestViewModel by activityViewModels()
    private lateinit var popupMenu: PopupMenu

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = RequestBodyRawBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupPopupMenu()
        setupObservers()
        setupEventListeners()
    }

    private fun setupObservers() {
        viewModel.rawData.observe(viewLifecycleOwner) { rawData ->
            binding.requestBodyRawContentTypeSelectedLabel.text = rawData.mimeType
        }
    }

    private fun setupEventListeners() {
        binding.requestBodyRawSelectTypeButton.setOnClickListener {
            popupMenu.show()
        }

        binding.requestBodyRawEditButton.setOnClickListener {
            val rawData = viewModel.rawData.value?.text
            val action = RequestFragmentDirections.actionRequestDetailFragmentToRequestBodyEditRawFragment(rawData)
            findNavController().navigate(action)
        }

        popupMenu.setOnMenuItemClickListener { item ->
            val mimeType = when (item.itemId) {
                R.id.content_type_option_textplain -> MimeTypes.TEXT_PLAIN.type
                R.id.content_type_option_applicationjson -> MimeTypes.APPLICATION_JSON.type
                R.id.content_type_option_applicationxml -> MimeTypes.APPLICATION_XML.type
                else -> MimeTypes.TEXT_PLAIN
            }

            viewModel.setRawBodyMimeType(mimeType.toString())
            true
        }
    }

    private fun setupPopupMenu() {
        popupMenu = PopupMenu(requireContext(), binding.requestBodyRawSelectTypeButton)
        popupMenu.inflate(R.menu.request_content_type_menu)
    }
}