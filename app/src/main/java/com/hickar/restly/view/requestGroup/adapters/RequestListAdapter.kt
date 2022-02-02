package com.hickar.restly.view.requestGroup.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hickar.restly.databinding.RequestGroupRequestItemBinding
import com.hickar.restly.models.RequestItem
import com.hickar.restly.utils.MethodCardViewUtil

class RequestListAdapter(
    private val onItemClicked: (RequestItem) -> Unit
) : ListAdapter<RequestItem, RequestListItemViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestListItemViewHolder {
        val adapterLayout = RequestGroupRequestItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        val viewHolder = RequestListItemViewHolder(adapterLayout, parent.context)

        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.bindingAdapterPosition
            onItemClicked(getItem(position))
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: RequestListItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun submitList(list: MutableList<RequestItem>?) {
        super.submitList(list?.let { ArrayList(list) })
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<RequestItem>() {
            override fun areItemsTheSame(oldItem: RequestItem, newItem: RequestItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: RequestItem, newItem: RequestItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}

class RequestListItemViewHolder(
    private var binding: RequestGroupRequestItemBinding,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: RequestItem) {
        val cardBackgroundColorId = MethodCardViewUtil.getMethodBackgroundColorId(item.request.method)
        val cardTextColorId = MethodCardViewUtil.getMethodTextColorId(item.request.method)

        val cardBackgroundColor =
            ResourcesCompat.getColor(context.resources, cardBackgroundColorId, null)
        val cardTextColor = ResourcesCompat.getColor(context.resources, cardTextColorId, null)

        binding.requestGroupRequestItemMethodBox.setCardBackgroundColor(cardBackgroundColor)
        binding.requestGroupRequestItemMethodLabel.setTextColor(cardTextColor)
        binding.requestGroupRequestItemMethodLabel.text =
            MethodCardViewUtil.getMethodShortMethodName(item.request.method)
        binding.requestGroupRequestItemNameLabel.text = item.name
        binding.requestGroupRequestItemUrlLabel.text = item.request.query.url
    }
}