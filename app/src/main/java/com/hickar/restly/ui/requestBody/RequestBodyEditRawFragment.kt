package com.hickar.restly.ui.requestBody

import android.os.Bundle
import android.text.Editable
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.hickar.restly.R
import com.hickar.restly.databinding.RequestBodyEditRawBinding
import com.hickar.restly.ui.request.RequestViewModel

class RequestBodyEditRawFragment : Fragment() {
    private val requestViewModel: RequestViewModel by activityViewModels()

    private var _binding: RequestBodyEditRawBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = RequestBodyEditRawBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val editableFactory = Editable.Factory()
        val textData = arguments?.getString("textData")

        binding.fullscreenEditTextInput.text = editableFactory.newEditable(textData)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fullscreen_edit_text_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.edit_text_menu_done_button -> {
                requestViewModel.setRawBodyText(binding.fullscreenEditTextInput.text.toString())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}