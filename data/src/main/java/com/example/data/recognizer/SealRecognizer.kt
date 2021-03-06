package com.example.data.recognizer

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.data.utils.Regexes.SEAL_NUMBER_REGEX
import com.example.data.utils.await
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognizer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface SealRecognizer {
    suspend fun recognize(uri: Uri): String?
    fun close()
}

private const val TAG = "SealRecognizer"

class OfflineSealRecognizer @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val recognizer: TextRecognizer
) : SealRecognizer {

    /**
     * ML Kit recognizes all text on the picture and represents it as text blocks.
     * We iterate through them and look for the text element that matches a seal number pattern.
     * If such is not found function returns null.
     * @param uri - image uri to scan.
     * @return recognized seal number that matches [SEAL_NUMBER_REGEX] or [null] if a suitable number wasn't detected.
     * */
    override suspend fun recognize(uri: Uri): String? = withContext(Dispatchers.Default) {
        val image = resolveInputImage(uri) ?: return@withContext null
        val visionText = recognizeText(image) ?: return@withContext null

        visionText.textBlocks
            .asSequence()
            .flatMap(Text.TextBlock::getLines)
            .flatMap(Text.Line::getElements)
            .map(Text.Element::getText)
            .find(::isValidNumber)
    }

    private fun isValidNumber(number: String): Boolean {
        return number.matches(SEAL_NUMBER_REGEX)
    }

    private fun resolveInputImage(uri: Uri): InputImage? {
        return try {
            InputImage.fromFilePath(appContext, uri)
        } catch (e: Exception) {
            Log.e(TAG, "Failed resolving InputImage due to: ${e.localizedMessage}", e)
            null
        }
    }

    private suspend fun recognizeText(image: InputImage): Text? {
        return try {
            recognizer.process(image).await()
        } catch (e: Exception) {
            Log.e(TAG, "Failed resolving InputImage due to: ${e.localizedMessage}", e)
            null
        }
    }

    override fun close() {
        recognizer.close()
    }
}