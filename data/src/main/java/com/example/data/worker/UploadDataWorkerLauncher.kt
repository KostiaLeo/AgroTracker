package com.example.data.worker

import androidx.work.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

interface UploadDataLauncher {
    fun enqueueWork()
}

class UploadDataWorkerLauncher @Inject constructor(
    private val workManager: WorkManager
) : UploadDataLauncher {

    companion object {
        private const val BACKOFF_DELAY_MS = 1000L
    }

    override fun enqueueWork() {
        workManager.enqueue(buildRequest())
    }

    private fun buildRequest(): WorkRequest {
        return OneTimeWorkRequestBuilder<UploadDataWorker>()
            .setConstraints(buildConstraints())
            .addTag(UploadDataWorker.TAG)
            .setBackoffCriteria(BackoffPolicy.LINEAR, BACKOFF_DELAY_MS, TimeUnit.MILLISECONDS)
            .build()
    }

    private fun buildConstraints(): Constraints {
        return Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
    }
}