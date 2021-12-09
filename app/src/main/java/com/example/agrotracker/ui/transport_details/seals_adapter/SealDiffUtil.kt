package com.example.agrotracker.ui.transport_details.seals_adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.data.models.Seal

class SealDiffUtil : DiffUtil.ItemCallback<Seal>() {
    override fun areItemsTheSame(oldItem: Seal, newItem: Seal): Boolean {
        return oldItem.sealNumber == newItem.sealNumber
    }

    override fun areContentsTheSame(oldItem: Seal, newItem: Seal): Boolean {
        return oldItem == newItem
    }
}