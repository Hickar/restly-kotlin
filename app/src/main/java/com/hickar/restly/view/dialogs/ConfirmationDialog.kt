package com.hickar.restly.view.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.hickar.restly.R

class ConfirmationDialog(
    private val titleId: Int,
    private val messageId: Int,
    private val confirmButtonTextId: Int,
    private val onNegativeCallback: (DialogInterface, Int) -> Unit,
    private val onPositiveCallback: (DialogInterface, Int) -> Unit
) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
            .setTitle(titleId)
            .setMessage(messageId)
            .setNegativeButton(R.string.dialog_cancel_option) { dialog, id -> onNegativeCallback(dialog, id) }
            .setPositiveButton(confirmButtonTextId) { dialog, id -> onPositiveCallback(dialog, id) }

        return builder.create()
    }
}

interface ConfirmationDialogDelegate {
    fun onNegativeCallback(dialog: DialogInterface, id: Int)
    fun onPositiveCallback(dialog: DialogInterface, id: Int)
}