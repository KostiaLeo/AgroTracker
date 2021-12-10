package com.example.agrotracker.helpers

import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.options
import kotlinx.coroutines.channels.Channel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

interface PhotoTaker {
    suspend fun capturePhoto(): Uri?
}

class CropPhotoTaker @Inject constructor(
    private val fragment: Fragment
) : PhotoTaker {

    companion object {
        private const val TIMESTAMP_DATE_FORMAT = "yyyyMMdd_HHmmss"
        private const val AUTHORITY = "com.example.android.fileprovider"
    }

    private val cropPhotoLauncher =
        fragment.registerForActivityResult(CropImageContract()) { result ->
            val uri = capturingImageUri?.takeIf { result.isSuccessful }
            uriChannel.trySend(uri)
        }

    private val uriChannel = Channel<Uri?>()

    private var capturingImageUri: Uri? = null

    override suspend fun capturePhoto(): Uri? {
        val file = createImageFile()
        val uri = FileProvider.getUriForFile(fragment.requireContext(), AUTHORITY, file)
        cropPhotoLauncher.launch(options { cropImageOptions.customOutputUri = uri })

        return uriChannel.receive()
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat(TIMESTAMP_DATE_FORMAT, Locale.getDefault()).format(Date())
        val storageDir =
            fragment.requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).also { file ->
            capturingImageUri = file.toUri()
        }
    }
}