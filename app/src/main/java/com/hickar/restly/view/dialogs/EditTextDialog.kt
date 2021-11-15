package com.hickar.restly.view.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.annotation.StringRes
import androidx.fragment.app.DialogFragment
import com.hickar.restly.R
import com.hickar.restly.extensions.toEditable

class EditTextDialog(
    @StringRes private val titleId: Int,
    private val textInputValue: String,
    private val onSubmitCallback: (String) -> Unit
) : DialogFragment() {
    private lateinit var textInput: EditText

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        textInput = EditText(activity)
        textInput.text = textInputValue.toEditable()

        val builder = AlertDialog.Builder(activity)
            .setTitle(titleId)
            .setNegativeButton(R.string.dialog_cancel_option) { dialog, _ ->
                dialog.cancel()
            }
            .setPositiveButton(R.string.dialog_ok_option) { _, _ ->
                onSubmitCallback(textInput.text.toString())
            }

        val dialog = builder.create()
        dialog.setView(textInput, 56, 8, 56, 8)

        return dialog
    }
}