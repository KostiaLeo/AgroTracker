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
        private const val UNIQUE_WORK_NAME = "uploadDataArgo"
    }

    override fun enqueueWork() {
        workManager.enqueueUniqueWork(UNIQUE_WORK_NAME, ExistingWorkPolicy.KEEP, buildRequest())
    }

    private fun buildRequest(): OneTimeWorkRequest {
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