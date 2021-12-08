package com.example.data.worker

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.edit
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.utils.SharedPreferencesKeys
import com.example.data.utils.await
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import java.io.File

@HiltWorker
class UploadDataWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val sharedPreferences: SharedPreferences
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val TAG = "UploadDataWorker"
    }

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        logError(throwable)
    }

    override suspend fun doWork(): Result {
        return kotlin.runCatching {

            uploadPhotos()
            awaitWritesToFirestore()

        }.onFailure(::logError)
            .fold({
                Result.success()
            }, {
                Result.retry()
            })
    }

    private suspend fun uploadPhotos() {
        val pendingPhotoSet =
            sharedPreferences.getStringSet(SharedPreferencesKeys.KEY_PHOTOS_SET, emptySet())
                .orEmpty()
                .toMutableSet()

        supervisorScope {
            pendingPhotoSet.forEach { photoName ->
                launch(errorHandler) {
                    uploadPhoto(photoName)
                    pendingPhotoSet.remove(photoName)
                }
            }
        }

        sharedPreferences.edit {
            putStringSet(SharedPreferencesKeys.KEY_PHOTOS_SET, pendingPhotoSet)
        }
    }

    private suspend fun uploadPhoto(photoName: String) {
        val reference = Firebase.storage.reference
        val uri = buildUriToImageFile(photoName)

        reference.child(photoName).putFile(uri).await()

        uri.toFile().delete()
    }

    private fun buildUriToImageFile(fileName: String): Uri {
        val storageDir = applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File(storageDir, fileName).toUri()
    }


    private suspend fun awaitWritesToFirestore() {
        with(Firebase.firestore) {
            enableNetwork()
            waitForPendingWrites().await()
        }
    }

    private fun logError(
        throwable: Throwable,
        message: String = "Error uploading data: ${throwable.localizedMessage}"
    ) {
        Log.e(TAG, message, throwable)
    }
}