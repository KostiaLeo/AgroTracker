package com.example.data.di

import com.example.data.api.FirebaseTransportsApi
import com.example.data.api.TransportsApi
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ApiModule {
    @Binds
    abstract fun bindTransportsApi(transportsApi: FirebaseTransportsApi): TransportsApi
}