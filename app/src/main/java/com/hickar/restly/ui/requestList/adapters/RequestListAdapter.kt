package com.hickar.restly.ui.requestList.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hickar.restly.databinding.RequestListItemBinding
import com.hickar.restly.models.Request
import com.hickar.restly.utils.MethodCardViewUtil

class RequestListAdapter(
    private val onItemClicked: (Request) -> Unit
) : ListAdapter<Request, RequestItemViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestItemViewHolder {
        val adapterLayout = RequestListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        val viewHolder = RequestItemViewHolder(adapterLayout, parent.context)

        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.bindingAdapterPosition
            onItemClicked(getItem(position))
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: RequestItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun submitList(list: MutableList<Request>?) {
        super.submitList(list?.let{ ArrayList(list) })
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

class RequestItemViewHolder(
    private var binding: RequestListItemBinding,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(request: Request) {
        val cardBackgroundColorId = MethodCardViewUtil.getBackgroundColorId(request.method.method)
        val cardTextColorId = MethodCardViewUtil.getTextColorId(request.method.method)

        val cardBackgroundColor =
            ResourcesCompat.getColor(context.resources, cardBackgroundColorId, null)
        val cardTextColor = ResourcesCompat.getColor(context.resources, cardTextColorId, null)

        binding.requestListItemMethodBox.setCardBackgroundColor(cardBackgroundColor)
        binding.requestListItemMethodLabel.setTextColor(cardTextColor)
        binding.requestListItemMethodLabel.text = MethodCardViewUtil.getShortMethodName(request.method.method)
        binding.requestListItemNameLabel.text = request.name
        binding.requestListItemUrlLabel.text = request.url
    }
}