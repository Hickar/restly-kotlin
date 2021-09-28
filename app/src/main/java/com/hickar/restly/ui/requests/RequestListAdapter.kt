package com.hickar.restly.ui.requests

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hickar.restly.R
import com.hickar.restly.repository.models.Request
import com.hickar.restly.databinding.RequestItemViewBinding

class RequestListAdapter(
    private val onItemClicked: (Request) -> Unit
) : ListAdapter<Request, RequestListAdapter.RequestItemViewHolder>(DiffCallback) {

    class RequestItemViewHolder(
        private var binding: RequestItemViewBinding,
        private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(request: Request) {
            val cardBackgroundColorId = getBackgroundColorId(request.method)
            val cardTextColorId = getTextColorId(request.method)

            val cardBackgroundColor =
                ResourcesCompat.getColor(context.resources, cardBackgroundColorId, null)
            val cardTextColor = ResourcesCompat.getColor(context.resources, cardTextColorId, null)

            binding.requestMethodBox.setCardBackgroundColor(cardBackgroundColor)
            binding.requestMethodLabel.setTextColor(cardTextColor)
            binding.requestMethodLabel.text = getShortMethodName(request.method)
            binding.requestNameLabel.text = request.name
            binding.requestUrlLabel.text = request.url
        }

        private fun getTextColorId(method: String): Int {
            return when (method) {
                "GET", "HEAD" -> R.color.light_blue_700
                "POST" -> R.color.green_700
                "PUT", "PATCH", "OPTIONS" -> R.color.yellow_700
                "DELETE" -> R.color.red_700
                else -> R.color.light_blue_700
            }
        }

        private fun getBackgroundColorId(method: String): Int {
            return when (method) {
                "GET", "HEAD" -> R.color.light_blue_200
                "POST" -> R.color.green_200
                "PUT", "PATCH", "OPTIONS" -> R.color.yellow_200
                "DELETE" -> R.color.red_200
                else -> R.color.light_blue_200
            }
        }

        private fun getShortMethodName(method: String): String {
            return when (method) {
                "GET", "HEAD", "POST", "PUT" -> method
                "PATCH" -> "PTCH"
                "OPTIONS" -> "OPT"
                "DELETE" -> "DEL"
                else -> method
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestItemViewHolder {
        val adapterLayout = RequestItemViewBinding.inflate(
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