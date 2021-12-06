package com.example.agrotracker.ui.transport_details.seals_adapter

import androidx.recyclerview.widget.RecyclerView
import com.example.agrotracker.databinding.SealListItemBinding
import com.example.data.models.Seal

class SealViewHolder(
    private val binding: SealListItemBinding,
    private val positionClickListener: (Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.root.setOnClickListener {
            positionClickListener(adapterPosition)
        }
    }

    fun bind(seal: Seal) {
        binding.sealNumber.text = seal.sealNumber
    }
}