package com.example.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Transport(
    val id: String = "",
    val inProcess: Boolean = true,
    val stateNumber: String = "",
    val driverData: String = "",
    val trailerNumber: String = "",
    val currentWayBill: String = ""
) : Parcelable
