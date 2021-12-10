package com.example.agrotracker.ui.add_seal

import android.net.Uri

class RecognitionResult(val number: String, val uri: Uri)

sealed class RecognitionState {
    object Loading : RecognitionState()
    class Success(val result: RecognitionResult) : RecognitionState()
    object Error : RecognitionState()
}