package com.example.agrotracker.domain

import android.net.Uri
import com.example.data.recognizer.SealRecognizer
import javax.inject.Inject

interface RecognizeSealUseCase {
    suspend operator fun invoke(uri: Uri): String?
    fun close()
}

class RecognizeSealUseCaseDefault @Inject constructor(
    private val sealRecognizer: SealRecognizer
) : RecognizeSealUseCase {
    override suspend fun invoke(uri: Uri): String? {
        return sealRecognizer.recognize(uri)
    }

    override fun close() {
        sealRecognizer.close()
    }
}