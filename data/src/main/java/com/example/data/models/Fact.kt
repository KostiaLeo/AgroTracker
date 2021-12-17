package com.example.data.models

import com.google.firebase.Timestamp

data class Fact(
    val transportId: String,
    val wayBillId: String,
    val seals: List<Seal> = emptyList(),
    val timestamp: Timestamp = Timestamp.now()
)