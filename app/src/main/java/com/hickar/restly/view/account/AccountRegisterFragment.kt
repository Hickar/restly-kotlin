package com.hickar.restly.view.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.hickar.restly.databinding.AccountRegisterBinding
import com.hickar.restly.viewModel.SettingsViewModel

class AccountRegisterFragment : Fragment() {
    private var _binding: AccountRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        _binding = AccountRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupEventListeners()
        setupObservers()
    }

    private fun setupEventListeners() {
        binding.accountRegisterSubmitButton.setOnClickListener {
            val email = binding.accountRegisterEmailInput.text.toString()
            val username = binding.accountRegisterUsernameInput.text.toString()
            val password = binding.accountRegisterPasswordInput.text.toString()

            viewModel.signUpInRestly(email, username, password)
        }
    }

    private fun setupObservers() {}
}