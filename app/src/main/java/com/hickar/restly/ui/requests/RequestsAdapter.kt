package com.hickar.restly.ui.requests

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hickar.restly.R
import com.hickar.restly.models.Request
import com.hickar.restly.databinding.RequestItemViewBinding

class RequestsAdapter(
    private val onItemClicked: (Request) -> Unit
) : ListAdapter<Request, RequestsAdapter.RequestItemViewHolder>(DiffCallback) {

    class RequestItemViewHolder(private var binding: RequestItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(request: Request) {
            binding.requestMethodLabel.text = request.method
            binding.requestNameLabel.text = request.name
            binding.requestUrlLabel.text = request.url
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestItemViewHolder {
        val adapterLayout = RequestItemViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        val viewHolder = RequestItemViewHolder(adapterLayout)

        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.bindingAdapterPosition
            onItemClicked(getItem(position))
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: RequestItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Request>() {
            override fun areItemsTheSame(oldItem: Request, newItem: Request): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Request, newItem: Request): Boolean {
                return oldItem == newItem
            }
        }
    }
}