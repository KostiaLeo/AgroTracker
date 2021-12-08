package com.example.agrotracker.di

import com.example.agrotracker.helpers.OfflineSealRecognizer
import com.example.agrotracker.helpers.SealRecognizer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class ViewModelModule {

    @Binds
    abstract fun bindSealNumberRecognizer(offlineSealNumberRecognizer: OfflineSealRecognizer): SealRecognizer
}