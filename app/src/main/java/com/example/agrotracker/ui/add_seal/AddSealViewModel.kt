package com.example.agrotracker.ui.add_seal

import androidx.lifecycle.ViewModel
import com.example.agrotracker.helpers.PhotoTaker
import com.example.agrotracker.helpers.SealNumberRecognizer
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddSealViewModel @Inject constructor(
    private val photoTaker: PhotoTaker,
    private val sealNumberRecognizer: SealNumberRecognizer
) : ViewModel()