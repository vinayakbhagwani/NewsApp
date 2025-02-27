package com.vinayak.apps.cardstacksdemoapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [NewsEntity::class], version = 1)
abstract class NewsDatabase: RoomDatabase() {

    companion object {
        fun getInstance(context: Context) = Room.databaseBuilder(context, NewsDatabase::class.java, "newsdb").build()
    }

    abstract fun getNewsDao(): NewsDao

}