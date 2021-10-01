package com.hickar.restly.ui.requestDetail

import android.text.Editable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hickar.restly.databinding.RequestDetailParamsItemBinding
import com.hickar.restly.models.RequestKeyValue

class RequestDetailParamsListAdapter<T : RequestKeyValue>(
    private val onCheckBoxClicked: (Int) -> Unit,
    private val onKeyInputFieldTextChanged: (String, Int) -> Unit,
    private val onValueInputFieldTextChanged: (String, Int) -> Unit
) : ListAdapter<T, RequestDetailParamsViewHolder<T>>(BaseItemCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RequestDetailParamsViewHolder<T> {
        val adapterLayout = RequestDetailParamsItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        val viewHolder = RequestDetailParamsViewHolder<T>(adapterLayout)


        adapterLayout.requestDetailParamsItemCheckbox.setOnClickListener {
            onCheckBoxClicked(viewHolder.bindingAdapterPosition)
        }

        adapterLayout.requestDetailParamsItemKeyTextInput.doAfterTextChanged {
            onKeyInputFieldTextChanged(it.toString(), viewHolder.bindingAdapterPosition)
        }

        adapterLayout.requestDetailParamsItemValueTextInput.doAfterTextChanged {
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

class RequestDetailParamsViewHolder<T : RequestKeyValue>(
    private val binding: RequestDetailParamsItemBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(parameter: T) {
        val editableFactory = Editable.Factory.getInstance()

        binding.requestDetailParamsItemCheckbox.isChecked = parameter.enabled
        binding.requestDetailParamsItemKeyTextInput.text = editableFactory.newEditable(parameter.key)
        binding.requestDetailParamsItemValueTextInput.text = editableFactory.newEditable(parameter.value)
    }
}

internal class BaseItemCallback<T : RequestKeyValue> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T) = oldItem.toString() == newItem.toString()

    override fun areContentsTheSame(oldItem: T, newItem: T) = oldItem == newItem
}