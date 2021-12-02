package com.hickar.restly.view.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SwitchCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.slider.Slider
import com.hickar.restly.R
import com.hickar.restly.databinding.SettingsBinding
import com.hickar.restly.extensions.hide
import com.hickar.restly.extensions.show
import com.hickar.restly.extensions.toEditable
import com.hickar.restly.extensions.toLongSafely
import com.hickar.restly.utils.NumberRangeInputFilter
import com.hickar.restly.view.dialogs.EditTextDialog
import com.hickar.restly.view.dialogs.WarningDialog
import com.hickar.restly.viewModel.LambdaFactory
import com.hickar.restly.viewModel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    @Inject lateinit var factory: SettingsViewModel.Factory
    private val viewModel: SettingsViewModel by viewModels {
        LambdaFactory(this) { stateHandle ->
            factory.build(stateHandle)
        }
    }

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
        binding.settingsLoginRestlyButton.setOnClickListener {
            val action = SettingsFragmentDirections.actionNavigationSettingsToLoginFragment()
            findNavController().navigate(action)
        }

        binding.settingsLogoutRestlyButton.setOnClickListener {
            viewModel.logoutFromRestly()
        }

        binding.settingsLoginPostmanButton.setOnClickListener {
            EditTextDialog(R.string.settings_login_postman_dialog_title, "") { apiKey ->
                viewModel.loginToPostman(apiKey)
            }.show(parentFragmentManager, "Postman Login")
        }

        binding.settingsLogoutPostmanButton.setOnClickListener {
            viewModel.logoutFromPostman()
        }

        binding.settingsRequestTimeoutInput.filters = arrayOf(NumberRangeInputFilter(Long.MIN_VALUE, Long.MAX_VALUE))
        binding.settingsRequestSslverificationSwitch.setOnClickListener {
            viewModel.setRequestSslVerificationEnabled((it as SwitchCompat).isChecked)
        }

        binding.settingsRequestMaxsizeInput.filters = arrayOf(NumberRangeInputFilter(Long.MIN_VALUE, Long.MAX_VALUE))
        binding.settingsRequestMaxsizeInput.doAfterTextChanged { text ->
            if (text.toString().isNotBlank()) {
                viewModel.setRequestMaxSize(text.toString().toLongSafely())
            }
        }

        binding.settingsRequestTimeoutInput.doAfterTextChanged { text ->
            if (text.toString().isNotBlank()) {
                viewModel.setRequestTimeout(text.toString().toLongSafely())
            }
        }

        binding.settingsWebviewJavascriptenabledSwitch.setOnClickListener {
            viewModel.setWebViewJavascriptEnabled((it as SwitchCompat).isChecked)
        }

        binding.settingsWebviewTextsizeSlider.addOnChangeListener(object : Slider.OnChangeListener {
            override fun onValueChange(slider: Slider, value: Float, fromUser: Boolean) {
                viewModel.setWebViewTextSize(value.toInt())
            }
        })
    }

    private fun setupObservers() {
        viewModel.isLoggedInPostman.observe(viewLifecycleOwner) { isLoggedIn ->
            if (isLoggedIn) {
                binding.settingsLoginPostmanLoggedinContainer.show()
                binding.settingsLoginPostmanNotloggedinContainer.hide()
            } else {
                binding.settingsLoginPostmanLoggedinContainer.hide()
                binding.settingsLoginPostmanNotloggedinContainer.show()
            }
        }

        viewModel.isLoggedInRestly.observe(viewLifecycleOwner) { isLoggedIn ->
            if (isLoggedIn) {
                binding.settingsLoginRestlyLoggedinContainer.show()
                binding.settingsLoginRestlyNotloggedinContainer.hide()
            } else {
                binding.settingsLoginRestlyLoggedinContainer.hide()
                binding.settingsLoginRestlyNotloggedinContainer.show()
            }
        }

        viewModel.userInfo.observe(viewLifecycleOwner) { userInfo ->
            if (userInfo != null)  {
                binding.settingsLoginPostmanFullnameLabel.text = userInfo.fullName.toEditable()
                binding.settingsLoginPostmanEmailLabel.text = userInfo.email.toEditable()
            }
        }

        viewModel.requestPrefs.observe(viewLifecycleOwner) { requestPrefs ->
            binding.settingsRequestSslverificationSwitch.isChecked = requestPrefs.sslVerificationEnabled
            binding.settingsRequestMaxsizeInput.text = requestPrefs.maxSize.toString().toEditable()
            binding.settingsRequestTimeoutInput.text = requestPrefs.timeout.toString().toEditable()
        }

        viewModel.webViewPrefs.observe(viewLifecycleOwner) { webViewPrefs ->
            binding.settingsWebviewJavascriptenabledSwitch.isChecked = webViewPrefs.javascriptEnabled
            binding.settingsWebviewTextsizeSlider.value = webViewPrefs.textSize.toFloat()
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            WarningDialog(error.title, error.message).show(parentFragmentManager, "AuthError")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}