package com.hickar.restly.utils

import android.text.InputFilter
import android.text.Spanned
import android.util.Log

class NumberRangeInputFilter(
    private val min: Long,
    private val max: Long
) : InputFilter {
    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        val inputString = (source.toString() + dest.toString())
        try {
            val input = inputString.toLong()
            if (isInRange(min, max, input)) return null
        } catch (e: NumberFormatException) {
            if (inputString < Long.MIN_VALUE.toString()) {
                return Long.MIN_VALUE.toString()
            } else if (inputString > Long.MAX_VALUE.toString()) {
                return Long.MAX_VALUE.toString()
            }
            Log.e("MinMaxInputFilter.filter", "Out of range number")
            e.printStackTrace()
        }

        return ""
    }

    private fun isInRange(x: Long, y: Long, z: Long): Boolean {
        return if (x < y) z in x..y else z in y..x
    }
}