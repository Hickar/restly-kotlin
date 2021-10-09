package com.hickar.restly.ui.requestBody

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import com.hickar.restly.R
import com.hickar.restly.consts.MimeTypes
import com.hickar.restly.databinding.RequestBodyRawBinding
import com.hickar.restly.ui.request.RequestDetailViewModel

class RequestBodyRawFragment(private val viewModel: RequestDetailViewModel) : Fragment() {
    private var _binding: RequestBodyRawBinding? = null
    private val binding get() = _binding!!

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
        viewModel.bodyType.observe(viewLifecycleOwner) { bodyType ->
            binding.requestDetailBodyRawContentTypeSelectedText.text = bodyType.type
        }
    }

    private fun setupEventListeners() {
        binding.requestDetailBodyRawButton.setOnClickListener {
            popupMenu.show()
        }
    }

    private fun setupPopupMenu() {
        popupMenu = PopupMenu(requireContext(), binding.requestDetailBodyRawButton)
        popupMenu.inflate(R.menu.request_content_type_menu)

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
}