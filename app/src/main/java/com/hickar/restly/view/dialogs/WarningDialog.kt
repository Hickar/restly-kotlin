package com.hickar.restly.view.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.fragment.app.DialogFragment
import com.hickar.restly.R

class WarningDialog(
    @StringRes private val titleId: Int,
    @StringRes private val messageId: Int
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
            .setTitle(titleId)
            .setPositiveButton(R.string.dialog_ok_option) { dialog, _ ->
                dialog.cancel()
            }
            .setMessage(messageId)

        return builder.create()
    }

}