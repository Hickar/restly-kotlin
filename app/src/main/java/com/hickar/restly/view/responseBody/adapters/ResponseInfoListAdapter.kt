package com.hickar.restly.view.responseBody.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hickar.restly.databinding.ResponseBodyInfoItemBinding

class ResponseInfoListAdapter :
    androidx.recyclerview.widget.ListAdapter<Pair<String, String>, ResponseInfoViewHolder>(BaseItemCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ResponseInfoViewHolder {
        val adapterLayout = ResponseBodyInfoItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ResponseInfoViewHolder(adapterLayout)
    }

    override fun submitList(list: List<Pair<String, String>>?) {
        super.submitList(list?.let { ArrayList(list) })
    }

    override fun onBindViewHolder(holder: ResponseInfoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemCount(): Int {
        return currentList.size
    }
}

class ResponseInfoViewHolder(
    private val binding: ResponseBodyInfoItemBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(parameter: Pair<String, String>) {

        binding.apply {
            responseBodyInfoItemHeader.text = parameter.first
            responseBodyInfoItemText.text = parameter.second
        }
    }
}

internal class BaseItemCallback : DiffUtil.ItemCallback<Pair<String, String>>() {
    override fun areItemsTheSame(oldItem: Pair<String, String>, newItem: Pair<String, String>) =
        oldItem.toString() == newItem.toString()

    override fun areContentsTheSame(oldItem: Pair<String, String>, newItem: Pair<String, String>) = false
}