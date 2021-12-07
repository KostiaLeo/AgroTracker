package com.example.agrotracker.di

import com.example.agrotracker.domain.LoadTransportListUseCase
import com.example.agrotracker.domain.LoadTransportListUseCaseDefault
import com.example.agrotracker.domain.SubmitTransportUseCase
import com.example.agrotracker.domain.SubmitTransportUseCaseDefault
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
    abstract fun bindSubmitTransportUseCase(useCaseDefault: SubmitTransportUseCaseDefault): SubmitTransportUseCase
}