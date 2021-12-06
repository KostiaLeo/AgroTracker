package com.example.data.worker

import android.content.Context
import androidx.work.*
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class UploadDataWorkerLauncher @Inject constructor(
    @ApplicationContext private val appContext: Context
) {
    fun enqueueWork() {
        WorkManager.getInstance(appContext)
            .enqueue(buildRequest())
    }


    private fun buildRequest(): WorkRequest {
        return OneTimeWorkRequestBuilder<UploadDataWorker>()
            .setConstraints(buildConstraints())
            .setBackoffCriteria(BackoffPolicy.LINEAR, 1000L, TimeUnit.MILLISECONDS)
            .build()
    }

    private fun buildConstraints(): Constraints {
        return Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
    }
}