package com.hickar.restly.view.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.hickar.restly.R
import com.hickar.restly.databinding.SettingsBinding
import com.hickar.restly.extensions.hide
import com.hickar.restly.extensions.show
import com.hickar.restly.extensions.toEditable
import com.hickar.restly.view.dialogs.EditTextDialog
import com.hickar.restly.viewModel.SettingsViewModel

class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModels()

    private var _binding: SettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupEventListeners()
        setupObservers()
    }

    private fun setupEventListeners() {
        binding.settingsLoginButton.setOnClickListener {
            val dialog = EditTextDialog(R.string.settings_login_postman_dialog_title, "") { apiKey ->
                viewModel.loginToPostman(apiKey)
            }
            dialog.show(parentFragmentManager, "Postman Login")
        }
    }

    private fun setupObservers() {
        viewModel.isLoggedIn.observe(viewLifecycleOwner) { isLoggedIn ->
            if (isLoggedIn) {
                binding.settingsLogginLoggedinContainer.show()
                binding.settingsLoginNotloggedinContainer.hide()
            } else {
                binding.settingsLogginLoggedinContainer.hide()
                binding.settingsLoginNotloggedinContainer.show()
            }
        }

        viewModel.userInfo.observe(viewLifecycleOwner) { userInfo ->
            binding.settingsLoginFullnameLabel.text = userInfo.fullName.toEditable()
            binding.settingsLoginEmailLabel.text = userInfo.email.toEditable()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}