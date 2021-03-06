package com.hickar.restly.view.request

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.hickar.restly.MainActivity
import com.hickar.restly.R
import com.hickar.restly.RestlyApplication
import com.hickar.restly.databinding.RequestBinding
import com.hickar.restly.extensions.hide
import com.hickar.restly.extensions.show
import com.hickar.restly.models.ErrorEvent
import com.hickar.restly.utils.KeyboardUtil
import com.hickar.restly.view.dialogs.EditTextDialog
import com.hickar.restly.view.dialogs.WarningDialog
import com.hickar.restly.view.request.adapters.RequestViewPagerAdapter
import com.hickar.restly.viewModel.LambdaFactory
import com.hickar.restly.viewModel.RequestViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RequestFragment : Fragment() {
    private var _binding: RequestBinding? = null
    private val binding get() = _binding!!

    private lateinit var applicationScope: CoroutineScope

    @Inject
    lateinit var factory: RequestViewModel.Factory
    val viewModel: RequestViewModel by activityViewModels {
        LambdaFactory(this) { stateHandle ->
            factory.build(stateHandle)
        }
    }

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadRequest(arguments?.get(REQUEST_ID_KEY) as String)
        applicationScope = (requireActivity().application as RestlyApplication).applicationScope
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (requireActivity() as AppCompatActivity).supportActionBar?.title = viewModel.name.value
        (requireActivity() as MainActivity).navView.hide()

        setHasOptionsMenu(true)

        _binding = RequestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        KeyboardUtil.hideKeyboard(requireActivity())
        setupViewPager()
        setupObservers()
    }

    private fun setupViewPager() {
        tabLayout = binding.requestBodyTabs
        viewPager = binding.requestBodyView
        viewPager.adapter = RequestViewPagerAdapter(this)
        viewPager.setPageTransformer(MarginPageTransformer(48))
        viewPager.isUserInputEnabled = false

        val tabs = arrayListOf("Request", "Response")

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabs[position]
        }.attach()
    }

    private fun setupObservers() {
        viewModel.name.observe(viewLifecycleOwner) { name ->
            (requireActivity() as AppCompatActivity).supportActionBar?.title = name
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            WarningDialog(error.title, error.message).show(parentFragmentManager, "RequestError")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.request_menu_save_button -> {
                viewModel.saveRequest()
                return true
            }
            R.id.request_menu_rename_button -> {
                EditTextDialog(R.string.dialog_rename_title, viewModel.name.value!!) { newName ->
                    viewModel.setName(newName)
                }.show(parentFragmentManager, "Rename")
                true
            }
            R.id.request_menu_send_button -> {
                if (viewModel.query.url.isEmpty()) {
                    WarningDialog(ErrorEvent.EmptyUrl.title, ErrorEvent.EmptyUrl.message)
                        .show(parentFragmentManager, "Warning")
                } else {
                    applicationScope.launch {
                        viewModel.saveRequest()
                        viewModel.sendRequest()
                    }
                    val responseTab = tabLayout.getTabAt(1)
                    tabLayout.selectTab(responseTab)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.request_action_menu, menu)
    }

    override fun onDestroy() {
        (requireActivity() as MainActivity).navView.show()
        requireActivity().viewModelStore.clear()
        _binding = null
        super.onDestroy()
    }

    companion object {
        const val REQUEST_ID_KEY = "requestId"
    }
}