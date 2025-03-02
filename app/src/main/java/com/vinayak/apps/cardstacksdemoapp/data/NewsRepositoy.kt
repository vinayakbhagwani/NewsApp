package com.vinayak.apps.cardstacksdemoapp.data

import com.vinayak.apps.cardstacksdemoapp.data.local.NewsEntity
import kotlinx.coroutines.flow.Flow

interface NewsRepository {

    suspend fun insert(news: List<NewsEntity>)

    suspend fun deleteAll()

    fun getAllNewsList(): Flow<List<NewsEntity>>

    fun setPeriodicWorkRequest()
}