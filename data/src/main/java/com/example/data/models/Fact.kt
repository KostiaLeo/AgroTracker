package com.example.data.models

data class Fact(
    val transportId: String,
    val wayBillId: String,
    val seals: List<Seal> = emptyList()
)