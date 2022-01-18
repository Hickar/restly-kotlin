package com.hickar.restly.view.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.fragment.app.DialogFragment
import com.hickar.restly.R

class ConfirmationDialog(
    @StringRes private val titleId: Int,
    @StringRes private val messageId: Int,
    @StringRes private val cancelButtonTextId: Int = R.string.dialog_cancel_option,
    @StringRes private val confirmButtonTextId: Int = R.string.dialog_confirm_option,
    private val onCancelCallback: (DialogInterface, Int) -> Unit,
    private val onConfirmCallback: (DialogInterface, Int) -> Unit
) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
            .setTitle(titleId)
            .setMessage(messageId)
            .setNegativeButton(cancelButtonTextId) { dialog, id -> onCancelCallback(dialog, id) }
            .setPositiveButton(confirmButtonTextId) { dialog, id -> onConfirmCallback(dialog, id) }

        return builder.create()
    }
}