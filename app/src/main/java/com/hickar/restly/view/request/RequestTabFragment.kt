package com.hickar.restly.view.request

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.hickar.restly.R
import com.hickar.restly.consts.RequestMethod
import com.hickar.restly.databinding.RequestTabBinding
import com.hickar.restly.extensions.hide
import com.hickar.restly.extensions.show
import com.hickar.restly.extensions.toEditable
import com.hickar.restly.models.BodyType
import com.hickar.restly.models.RequestHeader
import com.hickar.restly.models.RequestQueryParameter
import com.hickar.restly.utils.KeyboardUtil
import com.hickar.restly.utils.MethodCardViewUtil
import com.hickar.restly.utils.SwipeDeleteCallback
import com.hickar.restly.view.request.adapters.RequestBodyViewPagerAdapter
import com.hickar.restly.view.request.adapters.RequestParamsListAdapter
import com.hickar.restly.viewModel.RequestViewModel

typealias ParamsListAdapter = RequestParamsListAdapter<RequestQueryParameter>
typealias HeadersListAdapter = RequestParamsListAdapter<RequestHeader>

class RequestTabFragment : Fragment() {

    private var _binding: RequestTabBinding? = null
    private val binding get() = _binding!!

    val viewModel: RequestViewModel by activityViewModels()

    private lateinit var paramsRecyclerView: RecyclerView
    private lateinit var headersRecyclerView: RecyclerView

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    private lateinit var methodPopupMenu: PopupMenu

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = RequestTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        KeyboardUtil.hideKeyboard(requireActivity())
        setupViewPager()
        setupListAdapters()
        setupEventListeners()
        setupObservers()
        setupPopupMenus()
    }

    private fun setupViewPager() {
        tabLayout = binding.requestTabBodyTabs
        viewPager = binding.requestTabBodyView
        viewPager.adapter = RequestBodyViewPagerAdapter(this)
        viewPager.setPageTransformer(MarginPageTransformer(48))
        viewPager.isUserInputEnabled = false

        val tabs = BodyType.values().toList()

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabs[position].type
        }.attach()

        selectBodyTab(viewModel.getBodyTypeIndex())
    }

    private fun setupObservers() {
        viewModel.url.observe(viewLifecycleOwner) { url ->
            binding.requestTabUrlInputField.text = url.toEditable()
        }

        viewModel.name.observe(viewLifecycleOwner) { name ->
            binding.requestTabNameLabel.text = name
        }

        viewModel.method.observe(viewLifecycleOwner, { method ->
            val cardBackgroundColorId = MethodCardViewUtil.getBackgroundColorId(method.value)
            val cardTextColorId = MethodCardViewUtil.getTextColorId(method.value)

            val cardBackgroundColor =
                ResourcesCompat.getColor(resources, cardBackgroundColorId, null)
            val cardTextColor = ResourcesCompat.getColor(resources, cardTextColorId, null)

            binding.requestTabMethodLabel.text = MethodCardViewUtil.getShortMethodName(method.value)
            binding.requestTabMethodBox.setCardBackgroundColor(cardBackgroundColor)
            binding.requestTabMethodLabel.setTextColor(cardTextColor)

            when (method) {
                RequestMethod.POST,
                RequestMethod.PUT,
                RequestMethod.PATCH,
                RequestMethod.OPTIONS -> {
                    binding.requestTabSectionBody.show()
                }
                else -> {
                    binding.requestTabSectionBody.hide()
                }
            }
        })

        viewModel.queryParameters.observe(viewLifecycleOwner, { parameters ->
            (paramsRecyclerView.adapter as ParamsListAdapter).submitList(parameters)
        })

        viewModel.headers.observe(viewLifecycleOwner, { headers ->
            (headersRecyclerView.adapter as HeadersListAdapter).submitList(headers)
        })
    }

    private fun setupEventListeners() {
        binding.requestTabSectionParamsAddButton.setOnClickListener { viewModel.addQueryParameter() }
        binding.requestTabSectionHeadersAddButton.setOnClickListener { viewModel.addHeader() }
        binding.requestTabUrlInputField.doAfterTextChanged { url -> viewModel.setUrl(url.toString()) }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewModel.setBodyTypeIndex(tab!!.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        binding.requestTabMethodBox.setOnClickListener {
            methodPopupMenu.show()
        }
    }

    private fun setupListAdapters() {
        paramsRecyclerView = binding.requestTabSectionParamsList
        paramsRecyclerView.layoutManager = LinearLayoutManager(context)
        paramsRecyclerView.adapter = RequestParamsListAdapter<RequestQueryParameter>(
            { position -> viewModel.toggleQueryParameter(position) },
            { text, position -> viewModel.setQueryParameterKey(text, position) },
            { text, position -> viewModel.setQueryParameterValue(text, position) }
        )

        ItemTouchHelper(SwipeDeleteCallback(requireContext()) { position ->
            viewModel.deleteQueryParameter(position)
        }).attachToRecyclerView(paramsRecyclerView)


        headersRecyclerView = binding.requestTabSectionHeadersList
        headersRecyclerView.layoutManager = LinearLayoutManager(context)
        headersRecyclerView.adapter = RequestParamsListAdapter<RequestHeader>(
            { position -> viewModel.toggleHeader(position) },
            { text, position -> viewModel.setHeaderKey(position, text) },
            { text, position -> viewModel.setHeaderValue(position, text) }
        )
        ItemTouchHelper(SwipeDeleteCallback(requireContext()) { position ->
            viewModel.deleteHeader(position)
        }).attachToRecyclerView(headersRecyclerView)
    }

    private fun setupPopupMenus() {
        methodPopupMenu = PopupMenu(requireContext(), binding.requestTabMethodBox)
        methodPopupMenu.inflate(R.menu.request_method_popup_menu)

        methodPopupMenu.setOnMenuItemClickListener { item ->
            val method = when (item.itemId) {
                R.id.method_popup_option_get -> RequestMethod.GET
                R.id.method_popup_option_post -> RequestMethod.POST
                R.id.method_popup_option_put -> RequestMethod.PUT
                R.id.method_popup_option_patch -> RequestMethod.PATCH
                R.id.method_popup_option_head -> RequestMethod.HEAD
                R.id.method_popup_option_options -> RequestMethod.OPTIONS
                R.id.method_popup_option_delete -> RequestMethod.DELETE
                else -> RequestMethod.GET
            }
            viewModel.setMethod(method)
            true
        }
    }

    private fun selectBodyTab(position: Int) {
        val currentTab = tabLayout.getTabAt(position)
        tabLayout.selectTab(currentTab)
        viewPager.setCurrentItem(position, false)
        viewModel.setBodyTypeIndex(position)
    }

    override fun onDetach() {
        KeyboardUtil.hideKeyboard(requireActivity())
        super.onDetach()
    }

    override fun onResume() {
        super.onResume()
        binding.root.requestLayout()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}