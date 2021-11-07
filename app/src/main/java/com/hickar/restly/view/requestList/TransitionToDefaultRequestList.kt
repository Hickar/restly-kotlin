package com.hickar.restly.view.requestList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class TransitionToDefaultRequestList : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)

        val action = TransitionToDefaultRequestListDirections.navigateFromRequestTabToRequestList(null)
        findNavController().navigate(action)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

}