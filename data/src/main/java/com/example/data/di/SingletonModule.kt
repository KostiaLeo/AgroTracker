package com.example.data.di

import android.content.Context
import android.content.SharedPreferences
import android.os.Environment
import androidx.work.WorkManager
import com.example.data.utils.SharedPreferencesKeys
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SingletonModule {

    @Provides
    @Singleton
    fun providesSharedPreferences(@ApplicationContext appContext: Context): SharedPreferences {
        return appContext.getSharedPreferences(
            SharedPreferencesKeys.PREFERENCES_NAME,
            Context.MODE_PRIVATE
        )
    }

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext appContext: Context): WorkManager {
        return WorkManager.getInstance(appContext)
    }

    @Provides
    @Singleton
    fun provideExternalStorageDir(@ApplicationContext appContext: Context): File? {
        return appContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    }
}