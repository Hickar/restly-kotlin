package com.hickar.restly.ui.request

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.*
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.hickar.restly.R
import com.hickar.restly.RestlyApplication
import com.hickar.restly.consts.RequestMethods
import com.hickar.restly.databinding.RequestBinding
import com.hickar.restly.models.BodyType
import com.hickar.restly.models.RequestHeader
import com.hickar.restly.models.RequestQueryParameter
import com.hickar.restly.ui.dialogs.EditTextDialog
import com.hickar.restly.utils.KeyboardUtil
import com.hickar.restly.utils.MethodCardViewUtil
import com.hickar.restly.utils.SwipeDeleteCallback
import kotlinx.coroutines.runBlocking

typealias ParamsListAdapter = RequestParamsListAdapter<RequestQueryParameter>
typealias HeadersListAdapter = RequestParamsListAdapter<RequestHeader>

class RequestDetailFragment : Fragment() {

    private var _binding: RequestBinding? = null
    private val binding get() = _binding!!

    val requestViewModel: RequestViewModel by activityViewModels {
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
        tabLayout = binding.requestDetailBodyTabs
        viewPager = binding.requestDetailBodyView
        viewPager.adapter = RequestViewPagerAdapter(this)

        val tabs = BodyType.values().toList()

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabs[position].type
        }.attach()

        selectBodyTab(requestViewModel.getBodyTypeIndex())
    }

    private fun setupObservers() {
        val editableFactory = Editable.Factory.getInstance()
        binding.requestDetailUrlInputText.text = editableFactory.newEditable(requestViewModel.url.value)

        requestViewModel.name.observe(viewLifecycleOwner, { name ->
            binding.requestNameLabel.text = name
        })

        requestViewModel.method.observe(viewLifecycleOwner, { method ->
            val cardBackgroundColorId = MethodCardViewUtil.getBackgroundColorId(method)
            val cardTextColorId = MethodCardViewUtil.getTextColorId(method)

            val cardBackgroundColor =
                ResourcesCompat.getColor(resources, cardBackgroundColorId, null)
            val cardTextColor = ResourcesCompat.getColor(resources, cardTextColorId, null)

            binding.requestMethodLabel.text = MethodCardViewUtil.getShortMethodName(method)
            binding.requestDetailMethodBox.setCardBackgroundColor(cardBackgroundColor)
            binding.requestMethodLabel.setTextColor(cardTextColor)

            when (method) {
                RequestMethods.POST.method,
                RequestMethods.PUT.method,
                RequestMethods.PATCH.method,
                RequestMethods.OPTIONS.method -> {
                    binding.requestDetailBodySection.visibility = View.VISIBLE
                    requestViewModel.setBodyTypeIndex(TABS.FORMDATA.position)
                }
                else -> {
                    binding.requestDetailBodySection.visibility = View.GONE
                    requestViewModel.setBodyTypeIndex(TABS.NONE.position)
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
        binding.requestDetailParamsAddButton.setOnClickListener { onAddQueryParameter() }
        binding.requestDetailHeadersAddButton.setOnClickListener { onAddHeader() }
        binding.requestDetailUrlInputText.doAfterTextChanged { text ->
            requestViewModel.url.value = text.toString()
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                requestViewModel.setBodyTypeIndex(tab!!.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        binding.requestDetailMethodBox.setOnClickListener {
            methodPopupMenu.show()
        }
    }

    private fun setupListAdapters() {
        paramsRecyclerView = binding.requestDetailParamsRecyclerView
        paramsRecyclerView.layoutManager = LinearLayoutManager(context)
        paramsRecyclerView.adapter = RequestParamsListAdapter<RequestQueryParameter>(
            onParamCheckBoxToggle,
            { text, position ->
                requestViewModel.params.value!![position].key = text
            },
            { text, position ->
                requestViewModel.params.value!![position].value = text
            }
        )
        val paramsTouchHelper = ItemTouchHelper(SwipeDeleteCallback(requireContext()) { position ->
            requestViewModel.deleteQueryParameter(position)
        })
        paramsTouchHelper.attachToRecyclerView(paramsRecyclerView)


        headersRecyclerView = binding.requestDetailHeadersRecyclerView
        headersRecyclerView.layoutManager = LinearLayoutManager(context)
        headersRecyclerView.adapter = RequestParamsListAdapter<RequestHeader>(
            onHeaderCheckBoxToggle,
            { text, position ->
                requestViewModel.headers.value!![position].key = text
            },
            { text, position ->
                requestViewModel.headers.value!![position].value = text
            }
        )
        val headersTouchHelper = ItemTouchHelper(SwipeDeleteCallback(requireContext()) { position ->
            requestViewModel.deleteHeader(position)
        })
        headersTouchHelper.attachToRecyclerView(headersRecyclerView)
    }

    private fun setupPopupMenus() {
        methodPopupMenu = PopupMenu(requireContext(), binding.requestDetailMethodBox)
        methodPopupMenu.inflate(R.menu.request_method_popup_menu)

        methodPopupMenu.setOnMenuItemClickListener { item ->
            val method = when (item.itemId) {
                R.id.method_popup_option_get -> RequestMethods.GET
                R.id.method_popup_option_post -> RequestMethods.POST
                R.id.method_popup_option_put -> RequestMethods.PUT
                R.id.method_popup_option_patch -> RequestMethods.PATCH
                R.id.method_popup_option_head -> RequestMethods.HEAD
                R.id.method_popup_option_options -> RequestMethods.OPTIONS
                R.id.method_popup_option_delete -> RequestMethods.DELETE
                else -> RequestMethods.GET
            }
            requestViewModel.setMethod(method.toString())
            true
        }
    }

    private fun selectBodyTab(position: Int) {
        val currentTab = tabLayout.getTabAt(position)
        tabLayout.selectTab(currentTab)
        viewPager.setCurrentItem(position, false)
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
                runBlocking {
                    requestViewModel.saveRequest()
                    return@runBlocking true
                }
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
                runBlocking {
                    requestViewModel.saveRequest()
                    requestViewModel.sendRequest()
                    return@runBlocking true
                }
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