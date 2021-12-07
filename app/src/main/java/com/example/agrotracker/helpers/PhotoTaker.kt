package com.example.agrotracker.helpers

import android.net.Uri
import android.os.Environment
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

interface PhotoTaker {
    suspend fun takePhoto(): Uri?
}

class LocalPhotoTaker @Inject constructor(
    private val fragment: Fragment
) : PhotoTaker {

    companion object {
        private const val TIMESTAMP_DATE_FORMAT = "yyyyMMdd_HHmmss"
        private const val AUTHORITY = "com.example.android.fileprovider"
    }

    private val takePhotoLauncher =
        fragment.registerForActivityResult(ActivityResultContracts.TakePicture()) { captured ->
            fragment.lifecycleScope.launch {
                val uri = currentImageUri.takeIf { captured }
                uriChannel.send(uri)
            }
        }

    private val uriChannel = Channel<Uri?>()

    private lateinit var currentImageUri: Uri

    override suspend fun takePhoto(): Uri? {
        val file = createImageFile()
        val uri = FileProvider.getUriForFile(fragment.requireContext(), AUTHORITY, file)
        takePhotoLauncher.launch(uri)

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
            currentImageUri = file.toUri()
        }
    }
}