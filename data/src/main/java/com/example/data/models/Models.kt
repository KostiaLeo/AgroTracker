package com.example.data.models

import android.net.Uri
import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Transport(
    val id: String = "",
    val inProcess: Boolean = true,
    val stateNumber: String = "",
    val driverData: String = "",
    val trailerNumber: String = ""
) : Parcelable {
    class TransportDiffUtil : DiffUtil.ItemCallback<Transport>() {
        override fun areItemsTheSame(oldItem: Transport, newItem: Transport): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Transport, newItem: Transport): Boolean {
            return oldItem == newItem
        }
    }
}


data class Fact(
    val transportId: String,
    val seals: List<Seal> = emptyList()
)


data class Seal(
    val sealNumber: String,
    val imageUri: String? = null
) {
    class SealDiffUtil : DiffUtil.ItemCallback<Seal>() {
        override fun areItemsTheSame(oldItem: Seal, newItem: Seal): Boolean {
            return oldItem.sealNumber == newItem.sealNumber
        }

        override fun areContentsTheSame(oldItem: Seal, newItem: Seal): Boolean {
            return oldItem == newItem
        }
    }
}
