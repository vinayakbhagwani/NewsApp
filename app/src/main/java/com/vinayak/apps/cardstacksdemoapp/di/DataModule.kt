package com.vinayak.apps.cardstacksdemoapp.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.work.WorkManager
import com.vinayak.apps.cardstacksdemoapp.data.NewsRepository
import com.vinayak.apps.cardstacksdemoapp.data.local.NewsDao
import com.vinayak.apps.cardstacksdemoapp.data.local.NewsDatabase
import com.vinayak.apps.cardstacksdemoapp.domain.NewsRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DataModule {

    const val PREF_NAME = "apipref"

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): NewsDatabase {
        return NewsDatabase.getInstance(context)
    }

    @Provides
    fun provideDao(newsDatabase: NewsDatabase): NewsDao {
        return newsDatabase.getNewsDao()
    }

    @Provides
    fun provideRepository(newsDao: NewsDao, workManager: WorkManager): NewsRepository {
        return NewsRepositoryImpl(newsDao, workManager)
    }

    @Singleton
    @Provides
    fun provideSharedPreference(@ApplicationContext applicationContext: Context) : SharedPreferences {
        return applicationContext.getSharedPreferences(PREF_NAME, MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }
}