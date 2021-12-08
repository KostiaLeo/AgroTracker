package com.example.agrotracker.ui.transport_details.seals_adapter

import androidx.recyclerview.widget.RecyclerView
import com.example.agrotracker.databinding.SealListItemBinding
import com.example.data.models.Seal

class SealViewHolder(
    private val binding: SealListItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(seal: Seal) {
        binding.sealNumber.text = seal.sealNumber
    }
}