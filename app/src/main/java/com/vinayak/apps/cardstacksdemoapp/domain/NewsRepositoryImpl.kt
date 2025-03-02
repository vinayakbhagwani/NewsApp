package com.vinayak.apps.cardstacksdemoapp.domain

import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.vinayak.apps.cardstacksdemoapp.data.NewsRepository
import com.vinayak.apps.cardstacksdemoapp.data.local.NewsDao
import com.vinayak.apps.cardstacksdemoapp.data.local.NewsEntity
import com.vinayak.apps.cardstacksdemoapp.utils.NotificationWorker
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.TimeUnit

class NewsRepositoryImpl(
    private val newsDao: NewsDao,
    private val workManager: WorkManager): NewsRepository {

    override suspend fun insert(news: List<NewsEntity>) {
        newsDao.insert(news)
    }

    override suspend fun deleteAll() {
        newsDao.deleteAllNews()
    }

    override fun getAllNewsList(): Flow<List<NewsEntity>> {
        return newsDao.getNewsList()
    }

    override fun setPeriodicWorkRequest() {
        val constraints = Constraints.Builder()
//            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val workRequest = PeriodicWorkRequest.Builder(
            NotificationWorker::class.java,
            15,
            TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()
        Log.d("WM-----","reached here")
        workManager.enqueueUniquePeriodicWork(
            "gettingnotified",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }
}