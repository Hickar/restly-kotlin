package com.hickar.restly.view.requestGroup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hickar.restly.models.Collection

class TransitionToDefaultRequestGroup : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)

        val action = TransitionToDefaultRequestGroupDirections.navigateFromRequestTabToRequestGroup(
            Collection.DEFAULT,
        )
        findNavController().navigate(action)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

}