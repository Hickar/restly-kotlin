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
import com.hickar.restly.R
import com.hickar.restly.RestlyApplication
import com.hickar.restly.ViewModelFactory
import com.hickar.restly.databinding.RequestBinding
import com.hickar.restly.view.dialogs.EditTextDialog
import com.hickar.restly.view.request.adapters.RequestViewPagerAdapter
import com.hickar.restly.utils.KeyboardUtil
import com.hickar.restly.viewModel.RequestViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class RequestFragment : Fragment() {
    private var _binding: RequestBinding? = null
    private val binding get() = _binding!!

    private lateinit var applicationScope: CoroutineScope

    private val viewModel: RequestViewModel by activityViewModels {
        val application = requireActivity().application as RestlyApplication
        ViewModelFactory(application.repository)
    }

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadRequest(arguments?.get("requestId") as Long)
        applicationScope = (requireActivity().application as RestlyApplication).applicationScope
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (requireActivity() as AppCompatActivity).supportActionBar?.title = ""

        setHasOptionsMenu(true)

        _binding = RequestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        KeyboardUtil.hideKeyboard(requireActivity())
        setupViewPager()
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.request_menu_save_button -> {
                viewModel.saveRequest()
                return true
            }
            R.id.request_menu_rename_button -> {
                val nameEditDialog =
                    EditTextDialog(R.string.rename_dialog_title, viewModel.name.value!!) { newName ->
                        viewModel.setName(newName)
                    }
                nameEditDialog.show(parentFragmentManager, "Rename")
                true
            }
            R.id.request_menu_send_button -> {
                applicationScope.launch {
                    viewModel.saveRequest()
                    viewModel.sendRequest()
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
        super.onDestroy()
        requireActivity().viewModelStore.clear()
        _binding = null
    }
}