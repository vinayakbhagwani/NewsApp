package com.vinayak.apps.cardstacksdemoapp.domain

import com.vinayak.apps.cardstacksdemoapp.data.NewsRepository
import com.vinayak.apps.cardstacksdemoapp.data.local.NewsDao
import com.vinayak.apps.cardstacksdemoapp.data.local.NewsEntity
import kotlinx.coroutines.flow.Flow

class NewsRepositoryImpl(private val newsDao: NewsDao): NewsRepository {

    override suspend fun insert(news: List<NewsEntity>) {
        newsDao.insert(news)
    }

    override suspend fun deleteAll() {
        newsDao.deleteAllNews()
    }

    override fun getAllNewsList(): Flow<List<NewsEntity>> {
        return newsDao.getNewsList()
    }
}