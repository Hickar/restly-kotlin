package com.hickar.restly.ui.requestBody.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hickar.restly.databinding.RequestMultipartItemBinding
import com.hickar.restly.databinding.RequestMultipartItemBinding.inflate
import com.hickar.restly.extensions.hide
import com.hickar.restly.extensions.show
import com.hickar.restly.extensions.toEditable
import com.hickar.restly.models.RequestMultipartData

class RequestMultipartDataItemsAdapter(
    private val onCheckBoxClicked: (Int) -> Unit,
    private val onKeyInputFieldTextChanged: (String, Int) -> Unit,
    private val onValueInputFieldTextChanged: (String, Int) -> Unit,
    private val onValueButtonClicked: (Int) -> Unit
) : ListAdapter<RequestMultipartData, RequestMultipartDataViewHolder>(BaseItemCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RequestMultipartDataViewHolder {
        val adapterLayout = inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        val viewHolder = RequestMultipartDataViewHolder(adapterLayout)

        adapterLayout.requestMultipartItemCheckbox.setOnClickListener {
            onCheckBoxClicked(viewHolder.bindingAdapterPosition)
        }

        adapterLayout.requestMultipartItemKeyInputField.doAfterTextChanged {
            onKeyInputFieldTextChanged(it.toString(), viewHolder.bindingAdapterPosition)
        }

        adapterLayout.requestMultipartItemValueInputField.doAfterTextChanged {
            onValueInputFieldTextChanged(it.toString(), viewHolder.bindingAdapterPosition)
        }

        adapterLayout.requestMultipartSelectFileLabel.setOnClickListener {
            onValueButtonClicked(viewHolder.bindingAdapterPosition)
        }

        return viewHolder
    }

    override fun submitList(list: MutableList<RequestMultipartData>?) {
        super.submitList(list?.let { ArrayList(list) })
    }

    override fun onBindViewHolder(holder: RequestMultipartDataViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemCount(): Int {
        return currentList.size
    }
}

class RequestMultipartDataViewHolder(
    private val binding: RequestMultipartItemBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(parameter: RequestMultipartData) {

        binding.apply {
            requestMultipartItemCheckbox.isChecked = parameter.enabled
            requestMultipartItemKeyInputField.text = parameter.key.toEditable()

            if (parameter.type == "text") {
                requestMultipartSelectFileLabel.hide()
                requestMultipartItemValueInputLayout.show()
                requestMultipartItemValueInputField.text = parameter.valueText.toEditable()
            } else {
                requestMultipartItemValueInputLayout.hide()
                requestMultipartSelectFileLabel.show()
                requestMultipartSelectFileLabel.text = if (parameter.valueFile != null) {
                    parameter.valueFile?.name
                } else {
                    "SELECT"
                }
            }
        }
    }
}

internal class BaseItemCallback : DiffUtil.ItemCallback<RequestMultipartData>() {
    override fun areItemsTheSame(oldItem: RequestMultipartData, newItem: RequestMultipartData) = oldItem.toString() == newItem.toString()

    override fun areContentsTheSame(oldItem: RequestMultipartData, newItem: RequestMultipartData) = false
}