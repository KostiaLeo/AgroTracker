package com.example.agrotracker.helpers

import android.content.Context
import android.net.Uri
import androidx.fragment.app.Fragment
import com.example.data.utils.await
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface SealNumberRecognizer {
    suspend fun recognize(uri: Uri): String
    fun close()
}

class OfflineSealNumberRecognizer @Inject constructor(
    @ApplicationContext private val appContext: Context
) : SealNumberRecognizer {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    override suspend fun recognize(uri: Uri): String = withContext(Dispatchers.Default) {
        val image = InputImage.fromFilePath(appContext, uri)
        val visionText = recognizer.process(image).await()
        visionText.text
    }

    override fun close() {
        recognizer.close()
    }
}