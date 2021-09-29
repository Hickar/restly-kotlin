package com.hickar.restly.ui.requestDetail

import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hickar.restly.databinding.RequestDetailParamsItemBinding
import com.hickar.restly.models.RequestKeyValue
import com.hickar.restly.models.RequestQueryParameter

class RequestDetailParamsListAdapter(
    private val params: List<RequestKeyValue>
    ) : RecyclerView.Adapter<RequestDetailParamsViewHolder>() {



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RequestDetailParamsViewHolder {
        val adapterLayout = RequestDetailParamsItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        val viewHolder = RequestDetailParamsViewHolder(adapterLayout)
        return viewHolder
    }

    override fun onBindViewHolder(holder: RequestDetailParamsViewHolder, position: Int) {
        holder.bind(params[position])
    }

    override fun getItemCount(): Int = params.size
}

class RequestDetailParamsViewHolder(
    private val binding: RequestDetailParamsItemBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(parameter: RequestKeyValue) {
        val editableFactory = Editable.Factory.getInstance()

        binding.requestDetailParamsItemCheckbox.isEnabled = parameter.enabled
        binding.requestDetailParamsItemKeyTextInput.text = editableFactory.newEditable(parameter.key)
        binding.requestDetailParamsItemValueTextInput.text = editableFactory.newEditable(parameter.value)
    }
}