package com.hickar.restly.utils

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewDecoration {
    companion object {
        fun vertical(context: Context, @DrawableRes id: Int): DividerItemDecoration {
            val dividerDecoration = DividerItemDecoration(context, RecyclerView.VERTICAL)
            val dividerDrawable = ContextCompat.getDrawable(context, id)
            dividerDecoration.setDrawable(dividerDrawable!!)
            return dividerDecoration
        }
    }
}