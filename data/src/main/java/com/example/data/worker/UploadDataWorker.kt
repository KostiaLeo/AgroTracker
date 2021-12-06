package com.example.data.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.utils.await
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UploadDataWorker constructor(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    companion object {
        private const val TAG = "UploadDataWorker"
    }

    override suspend fun doWork(): Result {
        return kotlin.runCatching {
            Firebase.firestore.waitForPendingWrites().await()
        }.onFailure(::logError)
            .fold({
                Result.success()
            }, {
                Result.retry()
            })
    }

    private fun logError(throwable: Throwable) {
        Log.e(TAG, "Error uploading data: ${throwable.localizedMessage}", throwable)
    }
}