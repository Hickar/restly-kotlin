package com.hickar.restly.view.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.hickar.restly.R

class ConfirmationDialog(
    private val titleId: Int,
    private val messageId: Int,
    private val confirmButtonTextId: Int,
    private val onConfirmCallback: () -> Unit
) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
            .setTitle(titleId)
            .setMessage(messageId)
            .setNegativeButton(R.string.dialog_cancel_option) { dialog, _ -> dialog.cancel() }
            .setPositiveButton(confirmButtonTextId) { _, _ -> onConfirmCallback() }

        return builder.create()
    }
}