package com.example.data.di

import com.example.data.photos.ImageFileCreator
import com.example.data.photos.PhotosStorage
import com.example.data.photos.PhotosUploader
import com.example.data.photos.PreferencesPhotosStorage
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

    @Binds
    abstract fun bindPhotosStorage(photosStorage: PreferencesPhotosStorage): PhotosStorage

    @Binds
    abstract fun bindPhotosUploader(photosStorage: PreferencesPhotosStorage): PhotosUploader

    @Binds
    abstract fun bindImageFileCreator(photosStorage: PreferencesPhotosStorage): ImageFileCreator
}