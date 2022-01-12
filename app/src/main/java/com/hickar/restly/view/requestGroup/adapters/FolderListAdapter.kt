package com.hickar.restly.view.requestGroup.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hickar.restly.databinding.RequestGroupFolderItemBinding
import com.hickar.restly.databinding.RequestGroupRequestItemBinding
import com.hickar.restly.models.Request
import com.hickar.restly.models.RequestDirectory
import com.hickar.restly.utils.MethodCardViewUtil

class FolderListAdapter(
    private val onItemClicked: (RequestDirectory) -> Unit
) : ListAdapter<RequestDirectory, FolderListItemViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderListItemViewHolder {
        val adapterLayout = RequestGroupFolderItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        val viewHolder = FolderListItemViewHolder(adapterLayout, parent.context)

        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.bindingAdapterPosition
            onItemClicked(getItem(position))
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: FolderListItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun submitList(list: MutableList<RequestDirectory>?) {
        super.submitList(list?.let { ArrayList(list) })
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<RequestDirectory>() {
            override fun areItemsTheSame(oldItem: RequestDirectory, newItem: RequestDirectory): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: RequestDirectory, newItem: RequestDirectory): Boolean {
                return oldItem == newItem
            }
        }
    }
}

class FolderListItemViewHolder(
    private var binding: RequestGroupFolderItemBinding,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(folder: RequestDirectory) {
        binding.requestGroupFolderItemNameLabel.text = folder.name
        binding.requestGroupFolderItemDescriptionLabel.text = folder.description
    }
}