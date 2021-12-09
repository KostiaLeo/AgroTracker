package com.example.agrotracker.ui.transport_list.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.data.models.Transport

class TransportDiffUtil : DiffUtil.ItemCallback<Transport>() {
    override fun areItemsTheSame(oldItem: Transport, newItem: Transport): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Transport, newItem: Transport): Boolean {
        return oldItem == newItem
    }
}