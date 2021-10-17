package com.hickar.restly.ui.request

import android.graphics.Rect
import android.os.Bundle
import android.view.*
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.marginStart
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.hickar.restly.R
import com.hickar.restly.RestlyApplication
import com.hickar.restly.consts.RequestMethod
import com.hickar.restly.databinding.RequestBinding
import com.hickar.restly.extensions.hide
import com.hickar.restly.extensions.show
import com.hickar.restly.extensions.toEditable
import com.hickar.restly.models.BodyType
import com.hickar.restly.models.RequestHeader
import com.hickar.restly.models.RequestQueryParameter
import com.hickar.restly.ui.dialogs.EditTextDialog
import com.hickar.restly.ui.request.adapters.RequestParamsListAdapter
import com.hickar.restly.ui.request.adapters.RequestViewPagerAdapter
import com.hickar.restly.utils.KeyboardUtil
import com.hickar.restly.utils.MethodCardViewUtil
import com.hickar.restly.utils.SwipeDeleteCallback
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

typealias ParamsListAdapter = RequestParamsListAdapter<RequestQueryParameter>
typealias HeadersListAdapter = RequestParamsListAdapter<RequestHeader>

class RequestDetailFragment : Fragment() {

    private var _binding: RequestBinding? = null
    private val binding get() = _binding!!

    val requestViewModel: RequestViewModel by viewModels {
        RequestViewModelFactory(
            (activity?.application as RestlyApplication).repository,
            arguments?.get("requestId") as Long
        )
    }

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
        (requireActivity() as AppCompatActivity).supportActionBar?.title = ""

        setHasOptionsMenu(true)

        _binding = RequestBinding.inflate(inflater, container, false)
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
        tabLayout = binding.requestBodyTabs
        viewPager = binding.requestBodyView
        viewPager.adapter = RequestViewPagerAdapter(this)
        viewPager.setPageTransformer(MarginPageTransformer(48))

        val tabs = BodyType.values().toList()

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabs[position].type
        }.attach()

        selectBodyTab(requestViewModel.getBodyTypeIndex())
    }

    private fun setupObservers() {
        binding.requestUrlInputField.text = requestViewModel.url.value?.toEditable()

        requestViewModel.name.observe(viewLifecycleOwner, { name ->
            binding.requestNameLabel.text = name
        })

        requestViewModel.method.observe(viewLifecycleOwner, { method ->
            val cardBackgroundColorId = MethodCardViewUtil.getBackgroundColorId(method.method)
            val cardTextColorId = MethodCardViewUtil.getTextColorId(method.method)

            val cardBackgroundColor =
                ResourcesCompat.getColor(resources, cardBackgroundColorId, null)
            val cardTextColor = ResourcesCompat.getColor(resources, cardTextColorId, null)

            binding.requestMethodLabel.text = MethodCardViewUtil.getShortMethodName(method.method)
            binding.requestMethodBox.setCardBackgroundColor(cardBackgroundColor)
            binding.requestMethodLabel.setTextColor(cardTextColor)

            when (method) {
                RequestMethod.POST,
                RequestMethod.PUT,
                RequestMethod.PATCH,
                RequestMethod.OPTIONS -> {
                    binding.requestSectionBody.show()
                }
                else -> {
                    binding.requestSectionBody.hide()
                }
            }
        })

        requestViewModel.params.observe(viewLifecycleOwner, { params ->
            (paramsRecyclerView.adapter as ParamsListAdapter).submitList(params)
        })

        requestViewModel.headers.observe(viewLifecycleOwner, { headers ->
            (headersRecyclerView.adapter as HeadersListAdapter).submitList(headers)
        })
    }

    private fun setupEventListeners() {
        binding.requestSectionParamsAddButton.setOnClickListener { onAddQueryParameter() }
        binding.requestSectionHeadersAddButton.setOnClickListener { onAddHeader() }
        binding.requestUrlInputField.doAfterTextChanged { text ->
            requestViewModel.url.value = text.toString()
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                requestViewModel.setBodyTypeIndex(tab!!.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        binding.requestMethodBox.setOnClickListener {
            methodPopupMenu.show()
        }
    }

    private fun setupListAdapters() {
        paramsRecyclerView = binding.requestSectionParamsList
        paramsRecyclerView.layoutManager = LinearLayoutManager(context)
        paramsRecyclerView.adapter = RequestParamsListAdapter<RequestQueryParameter>(
            onParamCheckBoxToggle,
            { text, position ->
                requestViewModel.params.value!![position].key = text
            },
            { text, position ->
                requestViewModel.params.value!![position].valueText = text
            }
        )
        val paramsTouchHelper = ItemTouchHelper(SwipeDeleteCallback(requireContext()) { position ->
            requestViewModel.deleteQueryParameter(position)
        })
        paramsTouchHelper.attachToRecyclerView(paramsRecyclerView)


        headersRecyclerView = binding.requestSectionHeadersList
        headersRecyclerView.layoutManager = LinearLayoutManager(context)
        headersRecyclerView.adapter = RequestParamsListAdapter<RequestHeader>(
            onHeaderCheckBoxToggle,
            { text, position ->
                requestViewModel.headers.value!![position].key = text
            },
            { text, position ->
                requestViewModel.headers.value!![position].valueText = text
            }
        )
        val headersTouchHelper = ItemTouchHelper(SwipeDeleteCallback(requireContext()) { position ->
            requestViewModel.deleteHeader(position)
        })
        headersTouchHelper.attachToRecyclerView(headersRecyclerView)
    }

    private fun setupPopupMenus() {
        methodPopupMenu = PopupMenu(requireContext(), binding.requestMethodBox)
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
            requestViewModel.setMethod(method)
            true
        }
    }

    private fun selectBodyTab(position: Int) {
        val currentTab = tabLayout.getTabAt(position)
        tabLayout.selectTab(currentTab)
        viewPager.setCurrentItem(position, false)
        requestViewModel.setBodyTypeIndex(position)
    }

    private val onParamCheckBoxToggle: (Int) -> Unit = { position ->
        requestViewModel.toggleParam(position)
    }

    private val onHeaderCheckBoxToggle: (Int) -> Unit = { position ->
        requestViewModel.toggleHeader(position)
    }

    private fun onAddQueryParameter() {
        requestViewModel.addQueryParameter()
    }

    private fun onAddHeader() {
        requestViewModel.addHeader()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.request_action_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.request_menu_save_button -> {
                requestViewModel.saveRequest()
                return true
            }
            R.id.request_menu_rename_button -> {
                val nameEditDialog =
                    EditTextDialog(R.string.rename_dialog_title, requestViewModel.name.value!!) { newName ->
                        requestViewModel.setName(newName)
                    }
                nameEditDialog.show(parentFragmentManager, "Rename")
                true
            }
            R.id.request_menu_send_button -> {
                GlobalScope.launch {
                    requestViewModel.saveRequest()
                    requestViewModel.sendRequest()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDetach() {
        KeyboardUtil.hideKeyboard(requireActivity())
        super.onDetach()
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.viewModelStore?.clear()
        _binding = null
    }
}