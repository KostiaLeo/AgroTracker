package com.example.agrotracker.photo

import android.net.Uri
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.canhub.cropper.CropImageActivity
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.options
import com.example.data.photos.ImageFileCreator
import kotlinx.coroutines.channels.Channel
import javax.inject.Inject

/**
 * Interface for a handy interaction with camera/gallery and cropping images.
 * It consists of a single suspend function that returns captured photo's uri.
 * Under the hood it brings up the [CropImageActivity] and awaits the result (using [CropPhotoTaker.uriChannel]).
 * When the activity result is up the photo's uri is put into the [CropPhotoTaker.uriChannel].
 * */
interface PhotoTaker {
    suspend fun takePhoto(): Uri?
}

class CropPhotoTaker @Inject constructor(
    private val fragment: Fragment,
    private val imageFileCreator: ImageFileCreator
) : PhotoTaker {

    companion object {
        private const val AUTHORITY = "com.example.android.fileprovider"
    }

    private val cropPhotoLauncher =
        fragment.registerForActivityResult(CropImageContract()) { result ->
            val uri = capturingImageUri?.takeIf { result.isSuccessful }
            uriChannel.trySend(uri)
        }

    private val uriChannel = Channel<Uri?>()

    private var capturingImageUri: Uri? = null

    override suspend fun takePhoto(): Uri? {
        val file = imageFileCreator.createImageFile().also { capturingImageUri = it.toUri() }
        val uri = FileProvider.getUriForFile(fragment.requireContext(), AUTHORITY, file)
        cropPhotoLauncher.launch(options { cropImageOptions.customOutputUri = uri })

        return uriChannel.receive()
    }
}