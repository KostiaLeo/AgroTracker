package com.example.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Transport(
    val id: String = "",
    val inProcess: Boolean = true,
    val stateNumber: String = "",
    val driverData: String = "",
    val trailerNumber: String = ""
) : Parcelable


data class Fact(
    val transportId: String,
    val seals: List<Seal> = emptyList()
)


data class Seal(
    val sealNumber: String,
    val photoName: String? = null
)
