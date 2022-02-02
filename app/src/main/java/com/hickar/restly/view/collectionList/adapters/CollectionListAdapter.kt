package com.hickar.restly.view.collectionList.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hickar.restly.databinding.CollectionListItemBinding
import com.hickar.restly.extensions.toEditable
import com.hickar.restly.models.Collection
import com.hickar.restly.utils.MethodCardViewUtil

class CollectionListAdapter(
    private val onItemClicked: (Collection) -> Unit
) : ListAdapter<Collection, CollectionItemViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionItemViewHolder {
        val adapterLayout = CollectionListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        val viewHolder = CollectionItemViewHolder(adapterLayout, parent.context)

        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.bindingAdapterPosition
            onItemClicked(getItem(position))
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: CollectionItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun submitList(list: MutableList<Collection>?) {
        super.submitList(list?.let { ArrayList(list) })
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Collection>() {
            override fun areItemsTheSame(oldItem: Collection, newItem: Collection): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Collection, newItem: Collection): Boolean {
                return oldItem == newItem
            }
        }
    }
}

class CollectionItemViewHolder(
    private var binding: CollectionListItemBinding,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(collection: Collection) {
        binding.collectionListItemNameLabel.text = collection.name
        binding.collectionListItemDescription.text = collection.description

        val backgroundColorId = MethodCardViewUtil.getCollectionOriginBackgroundColorId(collection.origin)
        val textColorId = MethodCardViewUtil.getCollectionOriginTextColor(collection.origin)
        val originText = MethodCardViewUtil.getCollectionOriginShortName(collection.origin)

        val backgroundColor = ResourcesCompat.getColor(context.resources, backgroundColorId, null)
        val textColor = ResourcesCompat.getColor(context.resources, textColorId, null)

        binding.collectionListItemBadge.setCardBackgroundColor(backgroundColor)
        binding.collectionListItemBadgeText.setTextColor(textColor)
        binding.collectionListItemBadgeText.text = originText.toEditable()
    }
}