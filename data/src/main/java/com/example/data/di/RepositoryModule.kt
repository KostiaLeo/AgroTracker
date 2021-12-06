package com.example.data.di

import com.example.data.repository.TransportsRepository
import com.example.data.repository.TransportsRepositoryDefault
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindTransportsRepository(repository: TransportsRepositoryDefault): TransportsRepository
}