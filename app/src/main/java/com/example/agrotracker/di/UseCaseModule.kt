package com.example.agrotracker.di

import com.example.agrotracker.domain.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class UseCaseModule {

    @Binds
    abstract fun bindLoadTransportListUseCase(useCase: LoadTransportListUseCaseDefault): LoadTransportListUseCase

    @Binds
    abstract fun bindSubmitTransportUseCase(useCase: SubmitTransportUseCaseDefault): SubmitTransportUseCase

    @Binds
    abstract fun bindRecognizeSealUseCase(useCase: RecognizeSealUseCaseDefault): RecognizeSealUseCase
}