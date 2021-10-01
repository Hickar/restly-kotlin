package com.hickar.restly.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment

class KeyboardUtil {
    companion object {
        fun hideKeyboard(activity: Activity) {
            val inputManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            val view = activity.currentFocus ?: View(activity)
            inputManager.hideSoftInputFromWindow(view.windowToken, 0)
            view.clearFocus()
        }

        fun hideKeyboard(activity: Activity, view: View) {
            val inputManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(view.windowToken, 0)
            view.clearFocus()
        }

        fun hideKeyboard(context: Context, view: View) {
            val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(view.windowToken, 0)
            view.clearFocus()
        }

        fun hideKeyboard(fragment: Fragment) {
            val inputManager = fragment.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            val view = fragment.view ?: View(fragment.context)
            inputManager.hideSoftInputFromWindow(view.rootView?.windowToken, 0)
            view.clearFocus()
        }
    }
}