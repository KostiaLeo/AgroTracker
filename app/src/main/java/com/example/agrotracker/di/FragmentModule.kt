package com.example.agrotracker.di

import com.example.agrotracker.helpers.LocalPhotoTaker
import com.example.agrotracker.helpers.PhotoTaker
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
abstract class FragmentModule {
    @Binds
    abstract fun bindPhotoTaker(localPhotoTaker: LocalPhotoTaker): PhotoTaker
}