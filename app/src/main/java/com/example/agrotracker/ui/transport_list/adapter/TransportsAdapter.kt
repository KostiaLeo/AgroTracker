package com.example.agrotracker.ui.transport_list.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.agrotracker.databinding.TransportListItemBinding
import com.example.agrotracker.utils.getInflaterFrom
import com.example.data.models.Transport

class TransportsAdapter(
    private val clickListener: (Transport) -> Unit
) : ListAdapter<Transport, TransportViewHolder>(TransportDiffUtil()) {

    private val positionClickListener = { position: Int ->
        clickListener(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransportViewHolder {
        return TransportViewHolder(
            TransportListItemBinding.inflate(getInflaterFrom(parent), parent, false),
            positionClickListener
        )
    }

    override fun onBindViewHolder(holder: TransportViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}