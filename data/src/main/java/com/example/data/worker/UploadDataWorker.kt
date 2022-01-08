package com.example.data.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.photos.PhotosUploader
import com.example.data.utils.await
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class UploadDataWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val photosUploader: PhotosUploader
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val TAG = "UploadDataWorker"
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
        photosUploader.uploadPendingPhotos()
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