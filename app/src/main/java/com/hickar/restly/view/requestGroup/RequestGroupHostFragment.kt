package com.hickar.restly.view.requestGroup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.hickar.restly.R
import com.hickar.restly.databinding.RequestGroupHostBinding

class RequestGroupHostFragment : Fragment() {
    private var _binding: RequestGroupHostBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = RequestGroupHostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val fragmentManager = requireActivity().supportFragmentManager
        fragmentManager.commit {
            setReorderingAllowed(true)
            addToBackStack(null)
            add<RequestGroupFragment>(R.id.request_group_host_fragment)
        }
    }
}