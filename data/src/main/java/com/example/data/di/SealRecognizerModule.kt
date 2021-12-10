package com.example.data.di

import com.example.data.recognizer.OfflineSealRecognizer
import com.example.data.recognizer.SealRecognizer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class SealRecognizerModule {

    @Binds
    abstract fun bindSealNumberRecognizer(offlineSealNumberRecognizer: OfflineSealRecognizer): SealRecognizer
}