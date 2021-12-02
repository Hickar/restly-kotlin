package com.hickar.restly.view.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.hickar.restly.databinding.AccountLoginBinding
import com.hickar.restly.viewModel.SettingsViewModel

class AccountLoginFragment : Fragment() {
    private var _binding: AccountLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModels()

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
            val username = binding.accountLoginUsernameInput.text.toString()
            val password = binding.accountLoginPasswordInput.text.toString()

            viewModel.loginToRestly(username, password)
        }
    }

    private fun setupObservers() {}
}