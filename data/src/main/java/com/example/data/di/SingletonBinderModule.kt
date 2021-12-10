package com.example.data.di

import com.example.data.worker.UploadDataLauncher
import com.example.data.worker.UploadDataWorkerLauncher
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SingletonBinderModule {

    @Binds
    abstract fun bindUploadWorkerLauncher(uploadDataWorkerLauncher: UploadDataWorkerLauncher): UploadDataLauncher
}