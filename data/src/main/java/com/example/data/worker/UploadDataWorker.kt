package com.example.data.worker

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.utils.SharedPreferencesKeys
import com.example.data.utils.await
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.lang.Exception

@HiltWorker
class UploadDataWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val sharedPreferences: SharedPreferences
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        private const val TAG = "UploadDataWorker"
    }

    init {
        Log.d(TAG, "Init worker")
    }

    override suspend fun doWork(): Result {
        Log.d(TAG, "start work")
        return kotlin.runCatching {
            uploadPhotos()
            Firebase.firestore.enableNetwork()
            Firebase.firestore.waitForPendingWrites().await()
        }.onFailure(::logError)
            .fold({
                Log.d(TAG, "success work")
                Result.success()
            }, {
                Result.retry()
            })
    }

    private suspend fun uploadPhotos() {
        val set =
            sharedPreferences.getStringSet(SharedPreferencesKeys.PHOTOS_SET, emptySet()).orEmpty()
        val reference = Firebase.storage.reference

        coroutineScope {
            set.forEach {
                val uri = it.toUri()
                launch {
                Log.d(TAG, "uploading photo: $it")
                    try {
                        reference.child(it).putFile(uri).await()

                    } catch (e: Exception) {
                        Log.e(TAG, "error uploading: ${e.localizedMessage}", e)
                    }
                }
            }
        }


    }

    private fun logError(throwable: Throwable) {
        Log.e(TAG, "Error uploading data: ${throwable.localizedMessage}", throwable)
    }
}