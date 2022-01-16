package com.example.data.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.data.photos.PhotosUploader

class TestWorkerFactory(val photosUpdater: PhotosUploader) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker {
        return UploadDataWorker(appContext, workerParameters, photosUpdater)
    }
}