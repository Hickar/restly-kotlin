package com.hickar.restly.view.responseBody

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.hickar.restly.databinding.ResponseBodyPreviewBinding
import com.hickar.restly.viewModel.RequestViewModel

class ResponseBodyPreviewFragment : Fragment() {
    private var _binding: ResponseBodyPreviewBinding? = null
    private val binding get() = _binding!!

    private lateinit var webView: WebView
    private val viewModel: RequestViewModel by activityViewModels()

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
        webView.settings.javaScriptEnabled = true
    }

    private fun setupObservers() {
        viewModel.response.observe(viewLifecycleOwner) { response ->
            webView.loadDataWithBaseURL(response.url, response.body.rawData!!, "text/html", "utf-8", response.url)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.root.requestLayout()
    }
}