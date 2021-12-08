package com.example.agrotracker.helpers

import android.content.Context
import android.net.Uri
import com.example.agrotracker.utils.Regexes.SEAL_NUMBER_REGEX
import com.example.data.utils.await
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface SealRecognizer {
    suspend fun recognize(uri: Uri): String?
    fun close()
}

class OfflineSealRecognizer @Inject constructor(
    @ApplicationContext private val appContext: Context
) : SealRecognizer {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)


    override suspend fun recognize(uri: Uri): String? = withContext(Dispatchers.Default) {
        val image = InputImage.fromFilePath(appContext, uri)
        val visionText = recognizer.process(image).await()

        val elements =
            visionText.textBlocks.flatMap { it.lines }.flatMap { it.elements }.map { it.text }
        elements.find(::isValidNumber)
    }

    private fun isValidNumber(number: String): Boolean {
        return number.matches(SEAL_NUMBER_REGEX)
    }


    override fun close() {
        recognizer.close()
    }
}