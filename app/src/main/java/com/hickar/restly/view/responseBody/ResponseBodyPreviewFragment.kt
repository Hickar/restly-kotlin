package com.hickar.restly.view.responseBody

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import com.hickar.restly.databinding.ResponseBodyPreviewBinding
import com.hickar.restly.extensions.show
import com.hickar.restly.services.SharedPreferencesHelper
import com.hickar.restly.viewModel.RequestViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.last
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ResponseBodyPreviewFragment : Fragment() {
    private var _binding: ResponseBodyPreviewBinding? = null
    private val binding get() = _binding!!

    private lateinit var webView: WebView
    private val viewModel: RequestViewModel by activityViewModels()

    @Inject
    lateinit var prefs: SharedPreferencesHelper

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = ResponseBodyPreviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupWebView()
        setupObservers()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        webView = binding.responseBodyPreviewWebView
        lifecycleScope.launchWhenStarted {
            val webViewPrefs = prefs.getWebViewPrefs().last()
            webView.settings.javaScriptEnabled = webViewPrefs.javascriptEnabled
            webView.settings.minimumFontSize = webViewPrefs.textSize
        }
    }

    private fun setupObservers() {
        viewModel.response.observe(viewLifecycleOwner) { response ->
            val contentType = response.body.contentType

            when {
                contentType.contains("html") -> {
                    webView.show()
                    webView.loadDataWithBaseURL(
                        response.url,
                        response.body.rawData!!,
                        "text/html",
                        "utf-8",
                        response.url
                    )
                }
                contentType.contains("image") -> {
                    val imageViewContainer = binding.responseBodyPreviewImageViewContainer
                    val imageView = binding.responseBodyPreviewImageView
                    imageViewContainer.show()
                    imageView.setImageBitmap(viewModel.getResponseImageBitmap())
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.root.requestLayout()
    }
}