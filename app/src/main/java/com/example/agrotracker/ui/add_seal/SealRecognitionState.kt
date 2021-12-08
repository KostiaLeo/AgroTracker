package com.example.agrotracker.ui.add_seal

import android.net.Uri

class RecognitionResult(val number: String, val uri: Uri)

sealed class SealRecognitionState {
    object Loading : SealRecognitionState()
    class Success(val result: RecognitionResult) : SealRecognitionState()
    class Error(throwable: Throwable) : SealRecognitionState()
}