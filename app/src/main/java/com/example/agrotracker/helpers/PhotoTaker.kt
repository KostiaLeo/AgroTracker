package com.example.agrotracker.helpers

import android.net.Uri
import android.os.Environment
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

interface PhotoTaker {
    suspend fun capturePhoto(): Uri?
    suspend fun pickFromGallery(): Uri?
}

class LocalPhotoTaker @Inject constructor(
    private val fragment: Fragment
) : PhotoTaker {

    companion object {
        private const val TIMESTAMP_DATE_FORMAT = "yyyyMMdd_HHmmss"
        private const val AUTHORITY = "com.example.android.fileprovider"

        private const val IMAGE_MIME_TYPE = "image/*"
    }

    private val capturePhotoLauncher =
        fragment.registerForActivityResult(ActivityResultContracts.TakePicture()) { captured ->
            val uri = capturingImageUri?.takeIf { captured }
            uriChannel.trySend(uri)
        }

    private val pickFromGalleryLauncher =
        fragment.registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uriChannel.trySend(uri)
        }

    private val uriChannel = Channel<Uri?>()

    private var capturingImageUri: Uri? = null

    override suspend fun capturePhoto(): Uri? {
        val file = createImageFile()
        val uri = FileProvider.getUriForFile(fragment.requireContext(), AUTHORITY, file)
        capturePhotoLauncher.launch(uri)

        return uriChannel.receive()
    }

    override suspend fun pickFromGallery(): Uri? {
        pickFromGalleryLauncher.launch(IMAGE_MIME_TYPE)
        val photoUri = uriChannel.receive() ?: return null

        return withContext(Dispatchers.IO) {
            val targetFile = createImageFile()
            copyFromGalleryToLocalStorage(photoUri, targetFile)
            FileProvider.getUriForFile(fragment.requireContext(), AUTHORITY, targetFile)
        }
    }

    private fun copyFromGalleryToLocalStorage(photoUri: Uri, targetFile: File) {
        fragment.requireActivity().contentResolver.openInputStream(photoUri)?.use { input ->
            targetFile.outputStream().use { output ->
                input.copyTo(output, DEFAULT_BUFFER_SIZE)
            }
        }
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