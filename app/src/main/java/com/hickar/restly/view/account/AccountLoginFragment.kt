package com.hickar.restly.view.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.hickar.restly.databinding.AccountLoginBinding
import com.hickar.restly.view.dialogs.WarningDialog
import com.hickar.restly.viewModel.SettingsViewModel

class AccountLoginFragment : Fragment() {
    private var _binding: AccountLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        _binding = AccountLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupEventListeners()
        setupObservers()
    }

    private fun setupEventListeners() {
        binding.accountLoginSubmitButton.setOnClickListener {
            val username = binding.accountLoginEmailInput.text.toString()
            val password = binding.accountLoginPasswordInput.text.toString()

            viewModel.loginToRestly(username, password)
        }
    }

    private fun setupObservers() {
        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                WarningDialog(error.title, error.message).show(parentFragmentManager, "Login")
                viewModel.error.value = null
            }
        }

        viewModel.isLoggedInRestly.observe(viewLifecycleOwner) { isLogged ->
            if (isLogged) {
                requireActivity().onBackPressed()
            }
        }
    }
}