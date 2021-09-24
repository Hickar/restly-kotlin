package com.example.restly.ui.requests

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.restly.R
import com.example.restly.models.Request
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView

class RequestsAdapter(
    private val context: Context?,
    private val dataset: List<Request>
    ) : RecyclerView.Adapter<RequestsAdapter.RequestItemViewHolder>() {

    class RequestItemViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
        val methodBoxView: MaterialCardView = view!!.findViewById(R.id.request_method_box)
        val methodTextView: MaterialTextView = view!!.findViewById(R.id.request_method_label)
        val nameTextView: MaterialTextView = view!!.findViewById(R.id.request_name_label)
        val urlTextView: MaterialTextView = view!!.findViewById(R.id.request_url_label)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.request_item_view, parent, false)

        return RequestItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: RequestItemViewHolder, position: Int) {
        val item = dataset[position]

        holder.methodTextView.text = item.method
        holder.nameTextView.text = item.name
        holder.urlTextView.text = item.url
    }

    override fun getItemCount(): Int = dataset.size
}