package com.example.agrotracker.helpers

import android.net.Uri
import androidx.fragment.app.Fragment
import com.example.data.utils.await
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface SealNumberRecognizer {
    suspend fun recognize(uri: Uri): String
}

class OfflineSealNumberRecognizer @Inject constructor(
    private val fragment: Fragment
) : SealNumberRecognizer {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        .also(fragment.lifecycle::addObserver)

    override suspend fun recognize(uri: Uri): String = withContext(Dispatchers.Default) {
        val image = InputImage.fromFilePath(fragment.requireContext(), uri)
        val visionText = recognizer.process(image).await()
        visionText.text
    }
}