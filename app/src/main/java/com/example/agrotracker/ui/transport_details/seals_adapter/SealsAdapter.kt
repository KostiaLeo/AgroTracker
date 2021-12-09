package com.example.agrotracker.ui.transport_details.seals_adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.agrotracker.databinding.SealListItemBinding
import com.example.agrotracker.utils.getInflaterFrom
import com.example.data.models.Seal

class SealsAdapter : ListAdapter<Seal, SealViewHolder>(SealDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SealViewHolder {
        return SealViewHolder(
            SealListItemBinding.inflate(getInflaterFrom(parent), parent, false)
        )
    }

    override fun onBindViewHolder(holder: SealViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}