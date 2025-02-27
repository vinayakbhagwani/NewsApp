package com.vinayak.apps.cardstacksdemoapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(news: List<NewsEntity>)

    @Query("DELETE FROM news_table")
    suspend fun deleteAllNews()

    @Query("SELECT * FROM news_table")
    fun getNewsList(): Flow<List<NewsEntity>>
}