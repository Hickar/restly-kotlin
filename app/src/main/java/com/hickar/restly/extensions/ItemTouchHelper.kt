package com.hickar.restly.extensions

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

// Hack from the official documentation
// https://developer.android.com/reference/android/support/v7/widget/helper/ItemTouchHelper.html#attachToRecyclerView(android.support.v7.widget.RecyclerView)
fun ItemTouchHelper.reattachToRecyclerView(recyclerView: RecyclerView) {
    this.attachToRecyclerView(null)
    this.attachToRecyclerView(recyclerView)
}