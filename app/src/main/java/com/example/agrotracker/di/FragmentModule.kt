package com.example.agrotracker.di

import com.example.agrotracker.helpers.LocalPhotoTaker
import com.example.agrotracker.helpers.OfflineSealNumberRecognizer
import com.example.agrotracker.helpers.PhotoTaker
import com.example.agrotracker.helpers.SealNumberRecognizer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
abstract class FragmentModule {
    @Binds
    abstract fun bindPhotoTaker(localPhotoTaker: LocalPhotoTaker): PhotoTaker

    @Binds
    abstract fun bindSealNumberRecognizer(offlineSealNumberRecognizer: OfflineSealNumberRecognizer): SealNumberRecognizer
}