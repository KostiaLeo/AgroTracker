package com.example.agrotracker.ui.transport_list.adapter

import androidx.recyclerview.widget.RecyclerView
import com.example.agrotracker.databinding.TransportListItemBinding
import com.example.data.models.Transport

class TransportViewHolder(
    private val binding: TransportListItemBinding,
    private val onClickListener: (Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.root.setOnClickListener {
            onClickListener(adapterPosition)
        }
    }

    fun bind(transport: Transport) {
        binding.stateNumber.text = transport.stateNumber
        binding.driverData.text = transport.driverData
    }
}