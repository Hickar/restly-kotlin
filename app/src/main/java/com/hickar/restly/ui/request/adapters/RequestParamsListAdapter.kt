package com.hickar.restly.ui.request.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hickar.restly.databinding.RequestParamsItemBinding
import com.hickar.restly.databinding.RequestParamsItemBinding.inflate
import com.hickar.restly.extensions.toEditable
import com.hickar.restly.models.RequestKeyValueData

class RequestParamsListAdapter<T : RequestKeyValueData>(
    private val onCheckBoxClicked: (Int) -> Unit,
    private val onKeyInputFieldTextChanged: (String, Int) -> Unit,
    private val onValueInputFieldTextChanged: (String, Int) -> Unit
) : ListAdapter<T, RequestDetailParamsViewHolder<T>>(BaseItemCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RequestDetailParamsViewHolder<T> {
        val adapterLayout = inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        val viewHolder = RequestDetailParamsViewHolder<T>(adapterLayout)

        adapterLayout.requestParamsItemCheckbox.setOnClickListener {
            onCheckBoxClicked(viewHolder.bindingAdapterPosition)
        }

        adapterLayout.requestParamsItemKeyInputField.doAfterTextChanged {
            onKeyInputFieldTextChanged(it.toString(), viewHolder.bindingAdapterPosition)
        }

        adapterLayout.requestParamsItemValueInputField.doAfterTextChanged {
            onValueInputFieldTextChanged(it.toString(), viewHolder.bindingAdapterPosition)
        }

        return viewHolder
    }

    override fun submitList(list: MutableList<T>?) {
        super.submitList(list?.let { ArrayList(list) })
    }

    override fun onBindViewHolder(holder: RequestDetailParamsViewHolder<T>, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemCount(): Int {
        return currentList.size
    }
}

class RequestDetailParamsViewHolder<T : RequestKeyValueData>(
    private val binding: RequestParamsItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(parameter: T) {
        binding.apply {
            requestParamsItemCheckbox.isChecked = parameter.enabled
            requestParamsItemKeyInputField.text = parameter.key.toEditable()
            requestParamsItemValueInputField.text = parameter.valueText.toEditable()
        }
    }

}

internal class BaseItemCallback<T : RequestKeyValueData> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T) = oldItem.toString() == newItem.toString()

    override fun areContentsTheSame(oldItem: T, newItem: T) = false
}