package com.example.agrotracker.ui.transport_details.seals_adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.agrotracker.databinding.SealListItemBinding
import com.example.agrotracker.utils.getInflaterFrom
import com.example.data.models.Seal

class SealsAdapter(
    private val onSealClickListener: (Seal) -> Unit
) : ListAdapter<Seal, SealViewHolder>(Seal.SealDiffUtil()) {

    private val positionClickListener = { position: Int ->
        onSealClickListener(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SealViewHolder {
        return SealViewHolder(
            SealListItemBinding.inflate(getInflaterFrom(parent), parent, false),
            positionClickListener
        )
    }

    override fun onBindViewHolder(holder: SealViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}