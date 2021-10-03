package com.hickar.restly.ui.requestDetail

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.hickar.restly.R
import com.hickar.restly.RestlyApplication
import com.hickar.restly.databinding.RequestDetailBinding
import com.hickar.restly.models.RequestKeyValue
import com.hickar.restly.utils.KeyboardUtil
import com.hickar.restly.utils.MethodCardViewUtil
import com.hickar.restly.utils.SwipeDeleteCallback
import kotlinx.coroutines.runBlocking

typealias ParamsListAdapter = RequestDetailParamsListAdapter<RequestKeyValue>

class RequestDetailFragment : Fragment() {

    private var _binding: RequestDetailBinding? = null
    private val binding get() = _binding!!

    private val requestDetailViewModel: RequestDetailViewModel by viewModels {
        RequestDetailViewModelFactory(
            (activity?.application as RestlyApplication).repository,
            arguments?.get("requestId") as Long
        )
    }

    private lateinit var paramsRecyclerView: RecyclerView
    private lateinit var headersRecyclerView: RecyclerView

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (requireActivity() as AppCompatActivity).supportActionBar?.title = ""

        setHasOptionsMenu(true)

        _binding = RequestDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        KeyboardUtil.hideKeyboard(requireActivity())
        setupViewPager()
        setupListAdapters()
        setupEventListeners()
        setupObservers()
    }

    private fun setupViewPager() {
        tabLayout = binding.requestDetailBodyTabs
        viewPager = binding.requestDetailBodyView

        viewPager.adapter = RequestDetailViewPagerAdapter(this)

        val tabs = listOf("URLEncoded", "FormData", "Raw", "Binary")

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabs[position]
        }.attach()
    }

    private fun setupObservers() {
        val editableFactory = Editable.Factory.getInstance()
        binding.requestDetailUrlInputText.text = editableFactory.newEditable(requestDetailViewModel.url.value)

        requestDetailViewModel.name.observe(viewLifecycleOwner, { name ->
            binding.requestNameLabel.text = name
        })

        requestDetailViewModel.method.observe(viewLifecycleOwner, { method ->
            val cardBackgroundColorId = MethodCardViewUtil.getBackgroundColorId(method)
            val cardTextColorId = MethodCardViewUtil.getTextColorId(method)

            val cardBackgroundColor =
                ResourcesCompat.getColor(resources, cardBackgroundColorId, null)
            val cardTextColor = ResourcesCompat.getColor(resources, cardTextColorId, null)

            binding.requestMethodLabel.text = MethodCardViewUtil.getShortMethodName(method)
            binding.requestDetailMethodBox.setCardBackgroundColor(cardBackgroundColor)
            binding.requestMethodLabel.setTextColor(cardTextColor)
        })

        requestDetailViewModel.params.observe(viewLifecycleOwner, { params ->
            (paramsRecyclerView.adapter as ParamsListAdapter).submitList(params)
        })

        requestDetailViewModel.headers.observe(viewLifecycleOwner, { headers ->
            (headersRecyclerView.adapter as ParamsListAdapter).submitList(headers)
        })
    }

    private fun setupEventListeners() {
        binding.requestDetailParamsAddButton.setOnClickListener { onAddQueryParameter() }
        binding.requestDetailHeadersAddButton.setOnClickListener { onAddHeader() }
        binding.requestDetailUrlInputText.doAfterTextChanged { text ->
            requestDetailViewModel.url.value = text.toString()
        }
    }

    private fun setupListAdapters() {
        paramsRecyclerView = binding.requestDetailParamsRecyclerView
        paramsRecyclerView.layoutManager = LinearLayoutManager(context)
        paramsRecyclerView.adapter = RequestDetailParamsListAdapter<RequestKeyValue>(
            onParamCheckBoxToggle,
            { text, position ->
                requestDetailViewModel.params.value!![position].key = text
            },
            { text, position ->
                requestDetailViewModel.params.value!![position].value = text
            }
        )
        val paramsTouchHelper = ItemTouchHelper(SwipeDeleteCallback(requireContext()) { position ->
            requestDetailViewModel.deleteQueryParameter(position)
        })
        paramsTouchHelper.attachToRecyclerView(paramsRecyclerView)


        headersRecyclerView = binding.requestDetailHeadersRecyclerView
        headersRecyclerView.layoutManager = LinearLayoutManager(context)
        headersRecyclerView.adapter = RequestDetailParamsListAdapter<RequestKeyValue>(
            onHeaderCheckBoxToggle,
            { text, position ->
                requestDetailViewModel.headers.value!![position].key = text
            },
            { text, position ->
                requestDetailViewModel.headers.value!![position].value = text
            }
        )
        val headersTouchHelper = ItemTouchHelper(SwipeDeleteCallback(requireContext()) { position ->
            requestDetailViewModel.deleteHeader(position)
        })
        headersTouchHelper.attachToRecyclerView(headersRecyclerView)
    }

    private val onParamCheckBoxToggle: (Int) -> Unit = { position ->
        requestDetailViewModel.toggleParam(position)
    }

    private val onHeaderCheckBoxToggle: (Int) -> Unit = { position ->
        requestDetailViewModel.toggleHeader(position)
    }

    private fun onAddQueryParameter() {
        requestDetailViewModel.addQueryParameter()
    }

    private fun onAddHeader() {
        requestDetailViewModel.addHeader()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.request_detail_action_menu, menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.request_detail_menu_save_button -> {
                runBlocking {
                    requestDetailViewModel.saveRequest()
                    return@runBlocking true
                }
            }
//            R.id.request_detail_menu_send_button -> {
//
//            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDetach() {
        KeyboardUtil.hideKeyboard(requireActivity())
        Log.d("DetailFragment", "DETACH")
        super.onDetach()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}