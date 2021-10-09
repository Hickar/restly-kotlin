package com.hickar.restly.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.widget.EditText
import androidx.core.view.marginStart
import androidx.fragment.app.DialogFragment
import com.hickar.restly.R

class EditTextDialog(
    private val titleId: Int,
    private val textInputValue: String,
    private val onSubmitCallback: (String) -> Unit
) : DialogFragment() {
    private lateinit var textInput: EditText

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val editableFactory = Editable.Factory()

        textInput = EditText(activity)
        textInput.text = editableFactory.newEditable(textInputValue)

        val builder = AlertDialog.Builder(activity)
            .setTitle(titleId)
            .setNegativeButton(R.string.dialog_cancel) { dialog, _ ->
                dialog.cancel()
            }
            .setPositiveButton(R.string.dialog_ok) { _, _ ->
                onSubmitCallback(textInput.text.toString())
            }

        val dialog = builder.create()
        dialog.setView(textInput, 60, 8, 60, 8)

        return dialog
    }
}