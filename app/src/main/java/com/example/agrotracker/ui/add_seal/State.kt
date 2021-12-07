package com.example.agrotracker.ui.add_seal

import android.net.Uri

class RecognitionResult (val number: String, val uri: Uri)

sealed class AddSealState {
    object Loading : AddSealState()
    class Success (val result: RecognitionResult): AddSealState()
    class Error(throwable: Throwable): AddSealState()
}