package com.example.data.photos

import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import androidx.core.net.toUri
import com.example.data.utils.SharedPreferencesKeys
import com.example.data.utils.TIMESTAMP_DATE_FORMAT
import com.example.data.utils.await
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

interface ImageFileCreator {
    fun createImageFile(): File
}

interface PhotosUploader {
    suspend fun uploadPendingPhotos()
}

interface PhotosStorage {
    fun addPendingPhotos(photoNames: List<String>)
}

@Singleton
class PreferencesPhotosStorage @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val storageDir: File
) : PhotosStorage, ImageFileCreator, PhotosUploader {

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        logError(throwable)
    }


    override fun addPendingPhotos(photoNames: List<String>) {
        updatePendingPhotos(getCurrentPendingPhotos() + photoNames)
    }


    override suspend fun uploadPendingPhotos() {
        val pendingPhotoSet = getCurrentPendingPhotos()

        supervisorScope {
            // launch parallel uploading each photo
            pendingPhotoSet.forEach { photoName ->
                launch(errorHandler) {
                    uploadPhoto(photoName)
                    pendingPhotoSet.remove(photoName)
                }
            }
        }

        updatePendingPhotos(pendingPhotoSet)
    }

    private suspend fun uploadPhoto(photoName: String) {
        val file = File(storageDir, photoName)

        Firebase.storage.reference
            .child(photoName)
            .putFile(file.toUri())
            .await()

        file.delete()
    }

    private fun getCurrentPendingPhotos(): MutableSet<String> {
        return sharedPreferences.getStringSet(SharedPreferencesKeys.KEY_PHOTOS_SET, emptySet())!!
            .toMutableSet()
    }

    private fun updatePendingPhotos(newPhotos: Set<String>) {
        sharedPreferences.edit {
            putStringSet(SharedPreferencesKeys.KEY_PHOTOS_SET, newPhotos)
        }
    }

    override fun createImageFile(): File {
        val timeStamp = SimpleDateFormat(TIMESTAMP_DATE_FORMAT, Locale.getDefault()).format(Date())
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }

    private fun logError(
        throwable: Throwable,
        message: String = "Error uploading data: ${throwable.localizedMessage}"
    ) {
        Log.e("PhotosStorage", message, throwable)
    }
}