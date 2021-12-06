package com.example.agrotracker.utils

import android.view.LayoutInflater
import android.view.View

fun getInflaterFrom(view: View): LayoutInflater {
    return LayoutInflater.from(view.context)
}